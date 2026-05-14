package dev.hyudoro.pay_my_buddy_service.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;

import dev.hyudoro.pay_my_buddy_service.dto.LoginRequest;
import dev.hyudoro.pay_my_buddy_service.dto.RegisterRequest;
import dev.hyudoro.pay_my_buddy_service.exception.EmailAlreadyExistsException;
import dev.hyudoro.pay_my_buddy_service.integration.AbstractIntegrationTest;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;
import dev.hyudoro.pay_my_buddy_service.service.inter.AuthService;

@Sql("/sql/insert_test_users.sql")
class AuthServiceIT extends AbstractIntegrationTest {

    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;

    @Test
    void register_persistsNewUser_whenEmailNotTaken() {
        // given
        var request = new RegisterRequest("newuser", "newuser@mail.com", "NewPassword1!");

        // when
        authService.register(request);

        // then
        assertThat(userRepository.existsByEmail("newuser@mail.com")).isTrue();
    }

    @Test
    void register_throwsEmailAlreadyExists_whenEmailTaken() {
        // given
        var request = new RegisterRequest("rika", "rikachess@gmail.com", "Test123456789*");

        // when /then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void login_authenticatesUser_whenCredentialsAreValid() {
        // given
        var request = new LoginRequest("rikachess@gmail.com", "Test123456789*");

        // when
        authService.login(request);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo("rikachess@gmail.com");
    }
}
