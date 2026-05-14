package dev.hyudoro.pay_my_buddy_service.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

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

import dev.hyudoro.pay_my_buddy_service.dto.ProfileResponse;
import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateDataRequest;
import dev.hyudoro.pay_my_buddy_service.integration.AbstractIntegrationTest;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;
import dev.hyudoro.pay_my_buddy_service.service.inter.ProfileService;

@Sql("/sql/insert_test_users.sql")
class ProfileServiceIT extends AbstractIntegrationTest {

    @Autowired private ProfileService profileService;
    @Autowired private UserRepository userRepository;
    @Autowired private UserDetailsService userDetailsService;

    private static final String RIKA_EMAIL = "rikachess@gmail.com";

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
    void showUserData_returnsRikasProfile() {
        // when
        ProfileResponse response = profileService.showUserData();

        // then
        assertThat(response.email()).isEqualTo(RIKA_EMAIL);
        assertThat(response.username()).isEqualTo("rika");
        assertThat(response.balance()).isEqualByComparingTo("500.00");
    }

    @Test
    void updateProfile_persistsNewUsername_whenChanged() {
        // given
        var request = new ProfileUpdateDataRequest("rikaUpdated", null, null, null);

        // when
        profileService.updateProfile(request);

        // then
        assertThat(userRepository.findByEmail(RIKA_EMAIL))
                .isPresent()
                .get()
                .extracting(u -> u.getUsername())
                .isEqualTo("rikaUpdated");
    }

    @Test
    void updateProfile_persistsNewEmail_whenChanged() {
        // given
        var request = new ProfileUpdateDataRequest(null, "rika.new@gmail.com", null, null);

        // when
        profileService.updateProfile(request);

        // then
        assertThat(userRepository.existsByEmail("rika.new@gmail.com")).isTrue();
    }

    @Test
    void updateProfile_persistsNewPasswordHash_whenBothPasswordsProvided() {
        // given
        String oldHash = userRepository.findByEmail(RIKA_EMAIL).orElseThrow().getHashedPassword();
        var request = new ProfileUpdateDataRequest(null, null, "Test123456789*", "NewPassword1@bc");

        // when
        profileService.updateProfile(request);

        // then
        String newHash = userRepository.findByEmail(RIKA_EMAIL).orElseThrow().getHashedPassword();
        assertThat(newHash).isNotEqualTo(oldHash);
    }
}
