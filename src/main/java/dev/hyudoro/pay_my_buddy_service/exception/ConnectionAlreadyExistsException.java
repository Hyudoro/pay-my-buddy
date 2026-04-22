package dev.hyudoro.pay_my_buddy_service.exception;

public class ConnectionAlreadyExistsException extends RuntimeException {
    public ConnectionAlreadyExistsException (String message){
        super(message);
    }
}
