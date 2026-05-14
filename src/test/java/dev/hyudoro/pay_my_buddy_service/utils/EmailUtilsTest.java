package dev.hyudoro.pay_my_buddy_service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmailUtilsTest {

    @Test
    void isValid_returnsFalse_whenNull() {
        assertThat(EmailUtils.isValid(null)).isFalse();
    }

    @Test
    void isValid_returnsFalse_whenBlank() {
        assertThat(EmailUtils.isValid("   ")).isFalse();
    }

    @Test
    void isValid_returnsFalse_whenMissingAtSign() {
        assertThat(EmailUtils.isValid("notanemail.com")).isFalse();
    }

    @Test
    void isValid_returnsFalse_whenMissingDomain() {
        assertThat(EmailUtils.isValid("user@")).isFalse();
    }

    @Test
    void isValid_returnsFalse_whenMissingDot() {
        assertThat(EmailUtils.isValid("user@domaincom")).isFalse();
    }

    @Test
    void isValid_returnsTrue_whenWellFormed() {
        assertThat(EmailUtils.isValid("user@domain.com")).isTrue();
    }
}
