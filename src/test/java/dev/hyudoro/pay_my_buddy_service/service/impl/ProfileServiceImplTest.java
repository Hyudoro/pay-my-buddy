package dev.hyudoro.pay_my_buddy_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import dev.hyudoro.pay_my_buddy_service.dto.ProfileResponse;
import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateDataRequest;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.EmailAlreadyExistsException;
import dev.hyudoro.pay_my_buddy_service.exception.EmptyUpdateRequestException;
import dev.hyudoro.pay_my_buddy_service.exception.PasswordMissingException;
import dev.hyudoro.pay_my_buddy_service.exception.UserNotFoundException;
import dev.hyudoro.pay_my_buddy_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserDetailsService userDetailsService;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private static final String AUTHENTICATED_EMAIL = "john@mail.com";

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

    private User buildUser(String username, String email, String hashedPassword) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setHashedPassword(hashedPassword);
        return user;
    }


    @Test
    void showUserData_returnsProfileResponse_whenUserExists() {
        // given
        User user = buildUser("john", AUTHENTICATED_EMAIL, "hash");
        ReflectionTestUtils.setField(user, "balance", BigDecimal.TEN);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.of(2024, 1, 1, 0, 0));
        given(userRepository.findByEmail(AUTHENTICATED_EMAIL)).willReturn(Optional.of(user));

        // when
        ProfileResponse response = profileService.showUserData();

        // then
        assertThat(response.username()).isEqualTo("john");
        assertThat(response.email()).isEqualTo(AUTHENTICATED_EMAIL);
        assertThat(response.balance()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(response.createdAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
    }

    @Test
    void showUserData_throwsUserNotFound_whenUserMissing() {
        // given
        given(userRepository.findByEmail(AUTHENTICATED_EMAIL)).willReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> profileService.showUserData())
                .isInstanceOf(UserNotFoundException.class);
    }


    @Test
    void updateProfile_updatesUsername_whenOnlyUsernameProvided() {
        // given
        User user = buildUser("oldName", AUTHENTICATED_EMAIL, "hash");
        given(userRepository.findByEmailForUpdate(AUTHENTICATED_EMAIL)).willReturn(Optional.of(user));
        var request = new ProfileUpdateDataRequest("newName", null, null, null);

        // when
        profileService.updateProfile(request);

        // then
        assertThat(user.getUsername()).isEqualTo("newName");
        then(userRepository).should().findByEmailForUpdate(AUTHENTICATED_EMAIL);
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void updateProfile_updatesEmailAndRefreshesSecurityContext_whenOnlyEmailProvided() {
        // given
        User user = buildUser("john", AUTHENTICATED_EMAIL, "hash");
        given(userRepository.findByEmailForUpdate(AUTHENTICATED_EMAIL)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("new@mail.com")).willReturn(false);
        UserDetails updatedDetails = mock(UserDetails.class);
        given(updatedDetails.getPassword()).willReturn("hash");
        given(updatedDetails.getAuthorities()).willReturn(Collections.emptyList());
        given(userDetailsService.loadUserByUsername("new@mail.com")).willReturn(updatedDetails);
        var request = new ProfileUpdateDataRequest(null, "new@mail.com", null, null);

        // when
        profileService.updateProfile(request);

        // then
        then(userRepository).should().saveAndFlush(user);
        then(userDetailsService).should().loadUserByUsername("new@mail.com");
    }

    @Test
    void updateProfile_throwsEmailAlreadyExists_whenNewEmailAlreadyTaken() {
        // given
        User user = buildUser("john", AUTHENTICATED_EMAIL, "hash");
        given(userRepository.findByEmailForUpdate(AUTHENTICATED_EMAIL)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("taken@mail.com")).willReturn(true);
        var request = new ProfileUpdateDataRequest(null, "taken@mail.com", null, null);

        // when/then
        assertThatThrownBy(() -> profileService.updateProfile(request))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void updateProfile_updatesPasswordHash_whenBothPasswordsProvided() {
        // given
        User user = buildUser("john", AUTHENTICATED_EMAIL, "oldHash");
        given(userRepository.findByEmailForUpdate(AUTHENTICATED_EMAIL)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("OldPassword1@", "oldHash")).willReturn(true);
        given(passwordEncoder.encode("NewPassword1!")).willReturn("newHash");
        var request = new ProfileUpdateDataRequest(null, null, "OldPassword1@", "NewPassword1!");

        // when
        profileService.updateProfile(request);

        // then
        assertThat(user.getHashedPassword()).isEqualTo("newHash");
        then(passwordEncoder).should().encode("NewPassword1!");
    }

    @Test
    void updateProfile_throwsEmptyUpdateRequest_whenAllFieldsAbsent() {
        // given
        User user = buildUser("john", AUTHENTICATED_EMAIL, "hash");
        given(userRepository.findByEmailForUpdate(AUTHENTICATED_EMAIL)).willReturn(Optional.of(user));
        var request = new ProfileUpdateDataRequest(null, null, null, null);

        // when/then
        assertThatThrownBy(() -> profileService.updateProfile(request))
                .isInstanceOf(EmptyUpdateRequestException.class);
    }

    @Test
    void updateProfile_throwsPasswordMissing_whenOnlyOnePasswordFieldProvided() {
        // given
        User user = buildUser("john", AUTHENTICATED_EMAIL, "hash");
        given(userRepository.findByEmailForUpdate(AUTHENTICATED_EMAIL)).willReturn(Optional.of(user));
        var request = new ProfileUpdateDataRequest(null, null, null, "NewPassword1!");

        // when/then
        assertThatThrownBy(() -> profileService.updateProfile(request))
                .isInstanceOf(PasswordMissingException.class);
    }
}
