package dev.hyudoro.pay_my_buddy_service.exception;

public class SelfConnectionException extends RuntimeException {
    public SelfConnectionException(String message){
        super(message);
    }
}
