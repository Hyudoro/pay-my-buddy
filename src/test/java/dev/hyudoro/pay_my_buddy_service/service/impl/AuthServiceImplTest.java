package dev.hyudoro.pay_my_buddy_service.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    @Test
    void register_savesNewUser_whenEmailNotTaken() {
        // given
        var request = new RegisterRequest("john", "john@mail.com", "RawPwd123!");
        given(userRepository.existsByEmail("john@mail.com")).willReturn(false);
        given(passwordEncoder.encode("RawPwd123!")).willReturn("hashedPwd");

        // when
        authService.register(request);

        // then
        then(userRepository).should().save(any(User.class));
    }

    @Test
    void register_throwsEmailAlreadyExists_whenEmailTaken() {
        // given
        var request = new RegisterRequest("john", "john@mail.com", "RawPwd123!");
        given(userRepository.existsByEmail("john@mail.com")).willReturn(true);

        //when
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);
        //then
        then(userRepository).should().existsByEmail("john@mail.com");
        then(userRepository).shouldHaveNoMoreInteractions();
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
    }
}
