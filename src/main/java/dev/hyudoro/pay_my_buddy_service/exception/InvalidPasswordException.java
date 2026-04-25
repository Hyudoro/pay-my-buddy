package dev.hyudoro.pay_my_buddy_service.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message){
        super(message);
    }
}
