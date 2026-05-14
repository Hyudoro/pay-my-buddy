package dev.hyudoro.pay_my_buddy_service.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.jdbc.Sql;

import dev.hyudoro.pay_my_buddy_service.dto.ConnectionRequest;
import dev.hyudoro.pay_my_buddy_service.dto.UserConnectionResponse;
import dev.hyudoro.pay_my_buddy_service.exception.ConnectionAlreadyExistsException;
import dev.hyudoro.pay_my_buddy_service.exception.SelfConnectionException;
import dev.hyudoro.pay_my_buddy_service.integration.AbstractIntegrationTest;
import dev.hyudoro.pay_my_buddy_service.service.inter.ConnectionService;

@Sql("/sql/insert_test_users.sql")
class ConnectionServiceIT extends AbstractIntegrationTest {

    @Autowired private ConnectionService connectionService;
    @Autowired private UserDetailsService userDetailsService;

    private static final String RIKA_EMAIL = "rikachess@gmail.com";
    private static final String BUDDY_EMAIL = "buddy@gmail.com";

    @BeforeEach
    void setupSecurityContext() {
        UserDetails rika = userDetailsService.loadUserByUsername(RIKA_EMAIL);
        Authentication auth = new UsernamePasswordAuthenticationToken(rika, null, rika.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addConnection_persistsConnection_whenValid() {
        // when
        connectionService.addConnection(new ConnectionRequest(BUDDY_EMAIL));

        // then
        List<UserConnectionResponse> connections = connectionService.listConnection();
        assertThat(connections)
                .extracting(UserConnectionResponse::username)
                .contains("buddy");
    }

    @Test
    void addConnection_throwsSelfConnection_whenConnectingToSelf() {
        // when / then
        assertThatThrownBy(() -> connectionService.addConnection(new ConnectionRequest(RIKA_EMAIL)))
                .isInstanceOf(SelfConnectionException.class);
    }

    @Test
    void addConnection_throwsConnectionAlreadyExists_whenAddedTwice() {
        // given
        connectionService.addConnection(new ConnectionRequest(BUDDY_EMAIL));

        // when / then
        assertThatThrownBy(() -> connectionService.addConnection(new ConnectionRequest(BUDDY_EMAIL)))
                .isInstanceOf(ConnectionAlreadyExistsException.class);
    }

    @Test
    void listConnection_returnsConnectedUsers_afterConnectionAdded() {
        // given
        connectionService.addConnection(new ConnectionRequest(BUDDY_EMAIL));

        // when
        List<UserConnectionResponse> connections = connectionService.listConnection();

        // then
        assertThat(connections).hasSize(1);
        assertThat(connections.getFirst().username()).isEqualTo("buddy");
    }

    @Test
    void listConnection_returnsEmptyList_whenNoConnectionsExist() {
        // when
        List<UserConnectionResponse> connections = connectionService.listConnection();

        // then
        assertThat(connections).isEmpty();
    }
}
