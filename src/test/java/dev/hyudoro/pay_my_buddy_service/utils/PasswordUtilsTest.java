package dev.hyudoro.pay_my_buddy_service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PasswordUtilsTest {

    @Test
    void isValid_returnsFalse_whenNull() {
        assertThat(PasswordUtils.isValid(null)).isFalse();
    }

    @Test
    void isValid_returnsFalse_whenBlank() {
        assertThat(PasswordUtils.isValid("   ")).isFalse();
    }

    @Test
    void isValid_returnsFalse_whenTooShort() {
        assertThat(PasswordUtils.isValid("Short1!")).isFalse();
    }

    @Test
    void isValid_returnsFalse_whenNoUppercase() {
        assertThat(PasswordUtils.isValid("nouppercase1!")).isFalse();
    }

    @Test
    void isValid_returnsFalse_whenNoLowercase() {
        assertThat(PasswordUtils.isValid("NOLOWERCASE1!NOLOWER")).isFalse();
    }

    @Test
    void isValid_returnsFalse_whenNoDigit() {
        assertThat(PasswordUtils.isValid("NoDigitHere!!abcd")).isFalse();
    }

    @Test
    void isValid_returnsFalse_whenNoSpecialChar() {
        assertThat(PasswordUtils.isValid("NoSpecialChar1abcd")).isFalse();
    }

    @Test
    void isValid_returnsTrue_whenAllRequirementsMet() {
        assertThat(PasswordUtils.isValid("ValidPassword1!")).isTrue();
    }
}
