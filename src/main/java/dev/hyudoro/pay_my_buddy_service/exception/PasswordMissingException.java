package dev.hyudoro.pay_my_buddy_service.exception;

public class PasswordMissingException extends RuntimeException{
    public PasswordMissingException(String message){
        super(message);
    }

}
