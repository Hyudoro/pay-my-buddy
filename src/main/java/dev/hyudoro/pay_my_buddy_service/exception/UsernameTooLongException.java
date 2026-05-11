package dev.hyudoro.pay_my_buddy_service.exception;

public class UsernameTooLongException extends RuntimeException{
    public UsernameTooLongException(String message){
        super(message);
    }
}
