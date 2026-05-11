package dev.hyudoro.pay_my_buddy_service.service.validation;


import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateDataRequest;
import dev.hyudoro.pay_my_buddy_service.entity.User;
import dev.hyudoro.pay_my_buddy_service.exception.AlreadySelfGivenAttributeException;
import dev.hyudoro.pay_my_buddy_service.exception.EmailValidityException;
import dev.hyudoro.pay_my_buddy_service.exception.UsernameTooLongException;
import dev.hyudoro.pay_my_buddy_service.utils.EmailUtils;

public class BasicInfoValidator{

    private BasicInfoValidator(){}

    public static void validate(ProfileUpdateDataRequest request, User user){
        if(!GlobalGuard.isAbsent(request.email())){
            if(!EmailUtils.isValid(request.email())) throw new EmailValidityException("email is not valid.");
            if(request.email().equals(user.getEmail())) throw new AlreadySelfGivenAttributeException("cannot update with the same email.");
        }
            if (!GlobalGuard.isAbsent(request.username())) {
                if (request.username().length() > 50)
                    throw new UsernameTooLongException("username length exceeded");
                if (user.getUsername().equals(request.username()))
                    throw new AlreadySelfGivenAttributeException("cannot update with the same username.");
            }
    }
}
