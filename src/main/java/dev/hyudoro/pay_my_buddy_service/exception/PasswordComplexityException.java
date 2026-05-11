package dev.hyudoro.pay_my_buddy_service.exception;


public class PasswordComplexityException extends RuntimeException{
    public PasswordComplexityException(String message){
        super(message);
    }
}
