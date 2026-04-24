package dev.hyudoro.pay_my_buddy_service.exception;

public class ConnectionNotFoundException extends RuntimeException{

    public ConnectionNotFoundException(String message){
        super(message);
    }

}
