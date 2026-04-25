package dev.hyudoro.pay_my_buddy_service.exception;

public class EmptyUpdateRequestException extends RuntimeException{
    public EmptyUpdateRequestException(String message){
        super(message);
    }
}
