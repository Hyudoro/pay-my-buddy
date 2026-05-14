package dev.hyudoro.pay_my_buddy_service.service.validation;

import org.springframework.security.crypto.password.PasswordEncoder;
import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateDataRequest;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.AlreadySelfGivenAttributeException;
import dev.hyudoro.pay_my_buddy_service.exception.InvalidPasswordException;
import dev.hyudoro.pay_my_buddy_service.exception.PasswordComplexityException;
import dev.hyudoro.pay_my_buddy_service.utils.PasswordUtils;

public class PasswordValidator{

    private PasswordValidator(){}

    public static void validate(ProfileUpdateDataRequest request, User user, PasswordEncoder passwordEncoder){

        if(!PasswordUtils.isValid(request.newPassword()))
            throw new PasswordComplexityException("At least 1 uppercase, 1 lowercase, 1 special characters, 1 number, a length of 12 needed.");


        if(!passwordEncoder.matches(request.oldPassword(),user.getHashedPassword())){
            throw new InvalidPasswordException("user's password mismatch.");
        }

        if(request.oldPassword().equals(request.newPassword()))
            throw new AlreadySelfGivenAttributeException("cannot update with the same password.");
    }
}
