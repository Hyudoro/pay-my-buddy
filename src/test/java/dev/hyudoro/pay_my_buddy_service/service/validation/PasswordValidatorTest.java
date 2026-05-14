package dev.hyudoro.pay_my_buddy_service.service.validation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateDataRequest;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.AlreadySelfGivenAttributeException;
import dev.hyudoro.pay_my_buddy_service.exception.InvalidPasswordException;
import dev.hyudoro.pay_my_buddy_service.exception.PasswordComplexityException;

class PasswordValidatorTest {

    private static final String STORED_HASH = "storedHash";

    private User user;
    private PasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setHashedPassword(STORED_HASH);
        encoder = mock(PasswordEncoder.class);
    }

    @Test
    void validate_throwsPasswordComplexity_whenNewPasswordTooWeak() {
        var request = new ProfileUpdateDataRequest(null, null, "OldPassword1@", "weak");

        assertThatThrownBy(() -> PasswordValidator.validate(request, user, encoder))
                .isInstanceOf(PasswordComplexityException.class);
    }

    @Test
    void validate_throwsInvalidPassword_whenOldPasswordDoesNotMatch() {
        var request = new ProfileUpdateDataRequest(null, null, "WrongOld1@abc", "NewPassword1!");
        given(encoder.matches("WrongOld1@abc", STORED_HASH)).willReturn(false);

        assertThatThrownBy(() -> PasswordValidator.validate(request, user, encoder))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void validate_throwsSelfGiven_whenOldAndNewPasswordAreIdentical() {
        var request = new ProfileUpdateDataRequest(null, null, "SamePassword1!", "SamePassword1!");
        given(encoder.matches("SamePassword1!", STORED_HASH)).willReturn(true);

        assertThatThrownBy(() -> PasswordValidator.validate(request, user, encoder))
                .isInstanceOf(AlreadySelfGivenAttributeException.class);
    }

    @Test
    void validate_passes_whenAllConditionsMet() {
        var request = new ProfileUpdateDataRequest(null, null, "OldPassword1@", "NewPassword1!");
        given(encoder.matches("OldPassword1@", STORED_HASH)).willReturn(true);

        assertThatCode(() -> PasswordValidator.validate(request, user, encoder))
                .doesNotThrowAnyException();
    }
}
