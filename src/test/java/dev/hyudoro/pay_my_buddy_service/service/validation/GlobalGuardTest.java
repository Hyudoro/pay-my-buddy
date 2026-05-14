package dev.hyudoro.pay_my_buddy_service.service.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateDataRequest;
import dev.hyudoro.pay_my_buddy_service.exception.EmptyUpdateRequestException;
import dev.hyudoro.pay_my_buddy_service.exception.PasswordMissingException;

class GlobalGuardTest {

    @Test
    void check_throwsEmptyUpdateRequest_whenAllFieldsAbsent() {
        var request = new ProfileUpdateDataRequest(null, null, null, null);

        assertThatThrownBy(() -> GlobalGuard.check(request))
                .isInstanceOf(EmptyUpdateRequestException.class);
    }

    @Test
    void check_throwsPasswordMissing_whenOnlyNewPasswordProvided() {
        var request = new ProfileUpdateDataRequest(null, null, null, "NewPassword1!");

        assertThatThrownBy(() -> GlobalGuard.check(request))
                .isInstanceOf(PasswordMissingException.class);
    }

    @Test
    void check_throwsPasswordMissing_whenOnlyOldPasswordProvided() {
        var request = new ProfileUpdateDataRequest(null, null, "OldPassword1@", null);

        assertThatThrownBy(() -> GlobalGuard.check(request))
                .isInstanceOf(PasswordMissingException.class);
    }

    @Test
    void check_passes_whenOnlyUsernameProvided() {
        var request = new ProfileUpdateDataRequest("newUsername", null, null, null);

        assertThatCode(() -> GlobalGuard.check(request))
                .doesNotThrowAnyException();
    }

    @Test
    void check_passes_whenBothPasswordsProvided() {
        var request = new ProfileUpdateDataRequest(null, null, "OldPassword1@", "NewPassword1!");

        assertThatCode(() -> GlobalGuard.check(request))
                .doesNotThrowAnyException();
    }

    @Test
    void isAbsent_returnsTrue_whenNull() {
        assertThat(GlobalGuard.isAbsent(null)).isTrue();
    }

    @Test
    void isAbsent_returnsTrue_whenBlank() {
        assertThat(GlobalGuard.isAbsent("   ")).isTrue();
    }

    @Test
    void isAbsent_returnsFalse_whenValuePresent() {
        assertThat(GlobalGuard.isAbsent("hello")).isFalse();
    }
}
