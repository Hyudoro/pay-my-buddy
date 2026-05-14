package dev.hyudoro.pay_my_buddy_service.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.jdbc.Sql;

import dev.hyudoro.pay_my_buddy_service.dto.ConnectionRequest;
import dev.hyudoro.pay_my_buddy_service.dto.TransactionRequest;
import dev.hyudoro.pay_my_buddy_service.dto.UserTransactionResponse;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.ConnectionNotFoundException;
import dev.hyudoro.pay_my_buddy_service.exception.SenderInsufficientBalanceException;
import dev.hyudoro.pay_my_buddy_service.integration.AbstractIntegrationTest;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;
import dev.hyudoro.pay_my_buddy_service.service.inter.ConnectionService;
import dev.hyudoro.pay_my_buddy_service.service.inter.TransactionService;

@Sql("/sql/insert_test_users.sql")
class TransactionServiceIT extends AbstractIntegrationTest {

    @Autowired private TransactionService transactionService;
    @Autowired private ConnectionService connectionService;
    @Autowired private UserRepository userRepository;
    @Autowired private UserDetailsService userDetailsService;

    private static final String RIKA_EMAIL = "rikachess@gmail.com";
    private static final String BUDDY_EMAIL = "buddy@gmail.com";

    @BeforeEach
    void setupSecurityContextAndConnection() {
        UserDetails rika = userDetailsService.loadUserByUsername(RIKA_EMAIL);
        Authentication auth = new UsernamePasswordAuthenticationToken(rika, null, rika.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // establish connection between rika and buddy for most tests
        connectionService.addConnection(new ConnectionRequest(BUDDY_EMAIL));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private TransactionRequest transactionToBuddy(BigDecimal amount) {
        User buddy = userRepository.findByEmail(BUDDY_EMAIL).orElseThrow();
        return new TransactionRequest(buddy.getId(), amount, "test transfer");
    }

    @Test
    void makeTransaction_updatesBalances_whenValid() {
        // given
        var request = transactionToBuddy(new BigDecimal("50.00"));

        // when
        transactionService.makeTransaction(request);

        // then
        User rika = userRepository.findByEmail(RIKA_EMAIL).orElseThrow();
        User buddy = userRepository.findByEmail(BUDDY_EMAIL).orElseThrow();
        assertThat(rika.getBalance()).isEqualByComparingTo("450.00");
        assertThat(buddy.getBalance()).isEqualByComparingTo("550.00");
    }

    @Test
    void makeTransaction_throwsInsufficientBalance_whenBalanceTooLow() {
        // given
        var request = transactionToBuddy(new BigDecimal("9999.00"));

        // when / then
        assertThatThrownBy(() -> transactionService.makeTransaction(request))
                .isInstanceOf(SenderInsufficientBalanceException.class);
    }

    @Test
    void makeTransaction_throwsConnectionNotFound_whenUsersNotConnected() {
        // given — insert a third user with no connection to rika
        User stranger = new User();
        stranger.setEmail("stranger@mail.com");
        stranger.setUsername("stranger");
        stranger.setHashedPassword("hash");
        userRepository.save(stranger);

        var request = new TransactionRequest(stranger.getId(), new BigDecimal("10.00"), "no connection");

        // when / then
        assertThatThrownBy(() -> transactionService.makeTransaction(request))
                .isInstanceOf(ConnectionNotFoundException.class);
    }

    @Test
    void listTransaction_returnsPaginatedResults_afterTransaction() {
        // given
        transactionService.makeTransaction(transactionToBuddy(new BigDecimal("25.00")));

        // when
        Page<UserTransactionResponse> page = transactionService.listTransaction(PageRequest.of(0, 10));

        // then
        assertThat(page.getContent())
                .hasSize(1)
                .first()
                .satisfies(tx -> {
                    assertThat(tx.senderUsername()).isEqualTo("rika");
                    assertThat(tx.receiverUsername()).isEqualTo("buddy");
                    assertThat(tx.amount()).isEqualByComparingTo("25.00");
                });
    }

    @Test
    void listTransaction_returnsEmptyPage_whenNoTransactionsExist() {
        // when
        Page<UserTransactionResponse> page = transactionService.listTransaction(PageRequest.of(0, 10));

        // then
        assertThat(page.getContent()).isEmpty();
    }
}
