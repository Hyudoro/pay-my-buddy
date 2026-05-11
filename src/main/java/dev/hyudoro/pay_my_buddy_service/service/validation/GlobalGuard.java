package dev.hyudoro.pay_my_buddy_service.service.validation;

import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateDataRequest;
import dev.hyudoro.pay_my_buddy_service.exception.EmptyUpdateRequestException;
import dev.hyudoro.pay_my_buddy_service.exception.PasswordMissingException;

public class GlobalGuard {

    private GlobalGuard() {} //prevent instantiating.

    public static void check(ProfileUpdateDataRequest request) {
        boolean basicAbsent = isAbsent(request.username()) && isAbsent(request.email());
        boolean oldPasswordAbsent = isAbsent(request.oldPassword());
        boolean newPasswordAbsent = isAbsent(request.newPassword());
        boolean passwordAbsent = oldPasswordAbsent && newPasswordAbsent;
        if (basicAbsent && passwordAbsent) throw new EmptyUpdateRequestException("Request is empty.");
        if (oldPasswordAbsent != newPasswordAbsent) throw new PasswordMissingException("Both passwords must be provided together.");
    }

    public static boolean isAbsent(String value) {
        return value == null || value.isBlank();
    }
}
