package dev.hyudoro.pay_my_buddy_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.hyudoro.pay_my_buddy_service.dto.LoginRequest;
import dev.hyudoro.pay_my_buddy_service.dto.RegisterRequest;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.EmailAlreadyExistsException;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_savesNewUser_whenEmailNotTaken() {
        // given
        var request = new RegisterRequest("john", "john@mail.com", "RawPwd123!");
        given(userRepository.existsByEmail("john@mail.com")).willReturn(false);
        given(passwordEncoder.encode("RawPwd123!")).willReturn("hashedPwd");

        // when
        authService.register(request);

        // then
        var captor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("john@mail.com");
        assertThat(captor.getValue().getHashedPassword()).isEqualTo("hashedPwd");
        assertThat(captor.getValue().getUsername()).isEqualTo("john");
    }

    @Test
    void register_throwsEmailAlreadyExists_whenEmailTaken() {
        // given
        var request = new RegisterRequest("john", "john@mail.com", "RawPwd123!");
        given(userRepository.existsByEmail("john@mail.com")).willReturn(true);

        // when / then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        then(userRepository).should().existsByEmail("john@mail.com");
        then(userRepository).shouldHaveNoMoreInteractions();
        then(passwordEncoder).shouldHaveNoInteractions();
    }

    @Test
    void login_delegatesToAuthenticationManager() {
        // given
        var request = new LoginRequest("john@mail.com", "RawPwd123!");
        var auth = mock(Authentication.class);
        given(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(auth);

        // when
        authService.login(request);

        // then
        then(authManager).should().authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(auth);
    }
}
