package dev.hyudoro.pay_my_buddy_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import dev.hyudoro.pay_my_buddy_service.dto.ConnectionRequest;
import dev.hyudoro.pay_my_buddy_service.dto.UserConnectionResponse;
import dev.hyudoro.pay_my_buddy_service.entity.Connection;
import dev.hyudoro.pay_my_buddy_service.entity.ConnectionId;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.ConnectionAlreadyExistsException;
import dev.hyudoro.pay_my_buddy_service.exception.SelfConnectionException;
import dev.hyudoro.pay_my_buddy_service.exception.UserNotFoundException;
import dev.hyudoro.pay_my_buddy_service.repository.ConnectionRepository;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ConnectionServiceImplTest {

    @Mock private ConnectionRepository connectionRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ConnectionServiceImpl connectionService;

    private static final String AUTHENTICATED_EMAIL = "john@mail.com";
    private static final String CONNECTION_EMAIL = "jane@mail.com";

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

    private User buildUserWithId(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        return user;
    }


    @Test
    void addConnection_savesConnection_whenValid() {
        // given
        User currentUser = buildUserWithId("john", AUTHENTICATED_EMAIL);
        User target = buildUserWithId("jane", CONNECTION_EMAIL);
        given(userRepository.findByEmail(AUTHENTICATED_EMAIL)).willReturn(Optional.of(currentUser));
        given(userRepository.findByEmail(CONNECTION_EMAIL)).willReturn(Optional.of(target));
        given(connectionRepository.existsById(any(ConnectionId.class))).willReturn(false);
        var request = new ConnectionRequest(CONNECTION_EMAIL);

        // when
        connectionService.addConnection(request);

        // then
        then(connectionRepository).should().save(any(Connection.class));
    }

    @Test
    void addConnection_throwsSelfConnection_whenRequestEmailMatchesAuthenticatedUser() {
        // given
        var request = new ConnectionRequest(AUTHENTICATED_EMAIL);

        // when/then
        assertThatThrownBy(() -> connectionService.addConnection(request))
                .isInstanceOf(SelfConnectionException.class);
    }

    @Test
    void addConnection_throwsUserNotFound_whenAuthenticatedUserMissing() {
        // given
        given(userRepository.findByEmail(AUTHENTICATED_EMAIL)).willReturn(Optional.empty());
        var request = new ConnectionRequest(CONNECTION_EMAIL);

        // when/then
        assertThatThrownBy(() -> connectionService.addConnection(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void addConnection_throwsUserNotFound_whenTargetUserMissing() {
        // given
        User currentUser = buildUserWithId("john", AUTHENTICATED_EMAIL);
        given(userRepository.findByEmail(AUTHENTICATED_EMAIL)).willReturn(Optional.of(currentUser));
        given(userRepository.findByEmail(CONNECTION_EMAIL)).willReturn(Optional.empty());
        var request = new ConnectionRequest(CONNECTION_EMAIL);

        // when / then
        assertThatThrownBy(() -> connectionService.addConnection(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void addConnection_throwsConnectionAlreadyExists_whenAlreadyConnected() {
        // given
        User currentUser = buildUserWithId("john", AUTHENTICATED_EMAIL);
        User target = buildUserWithId("jane", CONNECTION_EMAIL);
        given(userRepository.findByEmail(AUTHENTICATED_EMAIL)).willReturn(Optional.of(currentUser));
        given(userRepository.findByEmail(CONNECTION_EMAIL)).willReturn(Optional.of(target));
        given(connectionRepository.existsById(any(ConnectionId.class))).willReturn(true);
        var request = new ConnectionRequest(CONNECTION_EMAIL);

        // when/then
        assertThatThrownBy(() -> connectionService.addConnection(request))
                .isInstanceOf(ConnectionAlreadyExistsException.class);
    }


    @Test
    void listConnection_returnsMappedResponses_whenConnectionsExist() {
        // given
        User currentUser = buildUserWithId("john", AUTHENTICATED_EMAIL);
        User friend = buildUserWithId("jane", CONNECTION_EMAIL);
        given(userRepository.findByEmail(AUTHENTICATED_EMAIL)).willReturn(Optional.of(currentUser));
        given(connectionRepository.findConnectionsOf(currentUser.getId())).willReturn(List.of(friend));

        // when
        List<UserConnectionResponse> result = connectionService.listConnection();

        // then
        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(UserConnectionResponse::username)
                .isEqualTo("jane");
    }

    @Test
    void listConnection_returnsEmptyList_whenNoConnectionsExist() {
        // given
        User currentUser = buildUserWithId("john", AUTHENTICATED_EMAIL);
        given(userRepository.findByEmail(AUTHENTICATED_EMAIL)).willReturn(Optional.of(currentUser));
        given(connectionRepository.findConnectionsOf(currentUser.getId())).willReturn(List.of());

        // when
        List<UserConnectionResponse> result = connectionService.listConnection();

        // then
        assertThat(result).isEmpty();
    }
}
