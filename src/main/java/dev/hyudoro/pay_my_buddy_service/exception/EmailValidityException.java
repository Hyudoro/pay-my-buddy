package dev.hyudoro.pay_my_buddy_service.exception;

public class EmailValidityException extends RuntimeException{
    public EmailValidityException(String message){
        super(message);
    }
}
