package dev.hyudoro.pay_my_buddy_service.service.validation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateDataRequest;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.AlreadySelfGivenAttributeException;
import dev.hyudoro.pay_my_buddy_service.exception.EmailValidityException;
import dev.hyudoro.pay_my_buddy_service.exception.UsernameTooLongException;

class BasicInfoValidatorTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("oldName");
        user.setEmail("old@mail.com");
    }

    @Test
    void validate_throwsEmailValidity_whenEmailFormatInvalid() {
        var request = new ProfileUpdateDataRequest(null, "notanemail", null, null);

        assertThatThrownBy(() -> BasicInfoValidator.validate(request, user))
                .isInstanceOf(EmailValidityException.class);
    }

    @Test
    void validate_throwsSelfGiven_whenEmailSameAsCurrent() {
        var request = new ProfileUpdateDataRequest(null, "old@mail.com", null, null);

        assertThatThrownBy(() -> BasicInfoValidator.validate(request, user))
                .isInstanceOf(AlreadySelfGivenAttributeException.class);
    }

    @Test
    void validate_throwsUsernameTooLong_whenUsernameExceeds50Chars() {
        var request = new ProfileUpdateDataRequest("a".repeat(51), null, null, null);

        assertThatThrownBy(() -> BasicInfoValidator.validate(request, user))
                .isInstanceOf(UsernameTooLongException.class);
    }

    @Test
    void validate_throwsSelfGiven_whenUsernameSameAsCurrent() {
        var request = new ProfileUpdateDataRequest("oldName", null, null, null);

        assertThatThrownBy(() -> BasicInfoValidator.validate(request, user))
                .isInstanceOf(AlreadySelfGivenAttributeException.class);
    }

    @Test
    void validate_passes_whenEmailAndUsernameAreNewAndValid() {
        var request = new ProfileUpdateDataRequest("newName", "new@mail.com", null, null);

        assertThatCode(() -> BasicInfoValidator.validate(request, user))
                .doesNotThrowAnyException();
    }
}
