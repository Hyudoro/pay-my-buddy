package dev.hyudoro.pay_my_buddy_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import dev.hyudoro.pay_my_buddy_service.dto.TransactionRequest;
import dev.hyudoro.pay_my_buddy_service.dto.UserTransactionResponse;
import dev.hyudoro.pay_my_buddy_service.entity.ConnectionId;
import dev.hyudoro.pay_my_buddy_service.entity.Transaction;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.ConnectionNotFoundException;
import dev.hyudoro.pay_my_buddy_service.exception.SelfTransactionException;
import dev.hyudoro.pay_my_buddy_service.exception.SenderInsufficientBalanceException;
import dev.hyudoro.pay_my_buddy_service.exception.UserNotFoundException;
import dev.hyudoro.pay_my_buddy_service.repository.ConnectionRepository;
import dev.hyudoro.pay_my_buddy_service.repository.TransactionRepository;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private ConnectionRepository connectionRepository;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private static final String AUTHENTICATED_EMAIL = "sender@mail.com";
    private static final UUID SENDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID RECEIVER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @BeforeEach
    void setupSecurityContext() {
        Authentication auth = mock(Authentication.class);
        given(auth.getName()).willReturn(AUTHENTICATED_EMAIL);
        SecurityContext ctx = mock(SecurityContext.class);
        given(ctx.getAuthentication()).willReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private User buildUserWithIdAndBalance(UUID id, String email, BigDecimal balance) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(email.split("@")[0]);
        user.setBalance(balance);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }


    @Test
    void makeTransaction_savesTransactionAndUpdatesBalances_whenValid() {
        // given
        User sender = buildUserWithIdAndBalance(SENDER_ID, AUTHENTICATED_EMAIL, new BigDecimal("100.00"));
        User receiver = buildUserWithIdAndBalance(RECEIVER_ID, "receiver@mail.com", new BigDecimal("50.00"));
        given(userRepository.findByEmailForUpdate(AUTHENTICATED_EMAIL)).willReturn(Optional.of(sender));
        given(userRepository.findByIdForUpdate(RECEIVER_ID)).willReturn(Optional.of(receiver));
        given(connectionRepository.existsById(any(ConnectionId.class))).willReturn(true);
        var request = new TransactionRequest(RECEIVER_ID, new BigDecimal("30.00"), "lunch");

        // when
        transactionService.makeTransaction(request);

        // then
        then(transactionRepository).should().save(any(Transaction.class));
        assertThat(sender.getBalance()).isEqualByComparingTo("70.00");
        assertThat(receiver.getBalance()).isEqualByComparingTo("80.00");
    }

    @Test
    void makeTransaction_throwsSelfTransaction_whenSenderAndReceiverAreTheSameUser() {
        // given
        User sender = buildUserWithIdAndBalance(SENDER_ID, AUTHENTICATED_EMAIL, new BigDecimal("100.00"));
        given(userRepository.findByEmailForUpdate(AUTHENTICATED_EMAIL)).willReturn(Optional.of(sender));
        var request = new TransactionRequest(SENDER_ID, new BigDecimal("10.00"), "self");

        // when/then
        assertThatThrownBy(() -> transactionService.makeTransaction(request))
                .isInstanceOf(SelfTransactionException.class);
    }

    @Test
    void makeTransaction_throwsUserNotFound_whenSenderDoesNotExist() {
        // given
        given(userRepository.findByEmailForUpdate(AUTHENTICATED_EMAIL)).willReturn(Optional.empty());
        var request = new TransactionRequest(RECEIVER_ID, new BigDecimal("10.00"), "test");

        // when/then
        assertThatThrownBy(() -> transactionService.makeTransaction(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void makeTransaction_throwsUserNotFound_whenReceiverDoesNotExist() {
        // given
        User sender = buildUserWithIdAndBalance(SENDER_ID, AUTHENTICATED_EMAIL, new BigDecimal("100.00"));
        given(userRepository.findByEmailForUpdate(AUTHENTICATED_EMAIL)).willReturn(Optional.of(sender));
        given(userRepository.findByIdForUpdate(RECEIVER_ID)).willReturn(Optional.empty());
        var request = new TransactionRequest(RECEIVER_ID, new BigDecimal("10.00"), "test");

        // when/then
        assertThatThrownBy(() -> transactionService.makeTransaction(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void makeTransaction_throwsConnectionNotFound_whenUsersAreNotConnected() {
        // given
        User sender = buildUserWithIdAndBalance(SENDER_ID, AUTHENTICATED_EMAIL, new BigDecimal("100.00"));
        User receiver = buildUserWithIdAndBalance(RECEIVER_ID, "receiver@mail.com", new BigDecimal("50.00"));
        given(userRepository.findByEmailForUpdate(AUTHENTICATED_EMAIL)).willReturn(Optional.of(sender));
        given(userRepository.findByIdForUpdate(RECEIVER_ID)).willReturn(Optional.of(receiver));
        given(connectionRepository.existsById(any(ConnectionId.class))).willReturn(false);
        var request = new TransactionRequest(RECEIVER_ID, new BigDecimal("10.00"), "test");

        // when/then
        assertThatThrownBy(() -> transactionService.makeTransaction(request))
                .isInstanceOf(ConnectionNotFoundException.class);
    }

    @Test
    void makeTransaction_throwsInsufficientBalance_whenSenderBalanceTooLow() {
        // given
        User sender = buildUserWithIdAndBalance(SENDER_ID, AUTHENTICATED_EMAIL, new BigDecimal("5.00"));
        User receiver = buildUserWithIdAndBalance(RECEIVER_ID, "receiver@mail.com", new BigDecimal("50.00"));
        given(userRepository.findByEmailForUpdate(AUTHENTICATED_EMAIL)).willReturn(Optional.of(sender));
        given(userRepository.findByIdForUpdate(RECEIVER_ID)).willReturn(Optional.of(receiver));
        given(connectionRepository.existsById(any(ConnectionId.class))).willReturn(true);
        var request = new TransactionRequest(RECEIVER_ID, new BigDecimal("10.00"), "test");

        // when/then
        assertThatThrownBy(() -> transactionService.makeTransaction(request))
                .isInstanceOf(SenderInsufficientBalanceException.class);
    }


    @Test
    void listTransaction_returnsPagedTransactions_whenCalled() {
        // given
        User currentUser = buildUserWithIdAndBalance(SENDER_ID, AUTHENTICATED_EMAIL, BigDecimal.ZERO);
        var transactionPage = new PageImpl<>(List.of(
                new UserTransactionResponse("sender", "receiver", "dinner", BigDecimal.TEN, LocalDateTime.now())
        ));
        given(userRepository.findByEmail(AUTHENTICATED_EMAIL)).willReturn(Optional.of(currentUser));
        given(transactionRepository.findTransactionsOf(SENDER_ID, Pageable.unpaged())).willReturn(transactionPage);

        // when
        Page<UserTransactionResponse> result = transactionService.listTransaction(Pageable.unpaged());

        // then
        assertThat(result.getContent())
                .hasSize(1)
                .first()
                .extracting(UserTransactionResponse::description)
                .isEqualTo("dinner");
    }
}
