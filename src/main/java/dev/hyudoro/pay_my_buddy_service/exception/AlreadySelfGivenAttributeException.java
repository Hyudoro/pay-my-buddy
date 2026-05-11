package dev.hyudoro.pay_my_buddy_service.exception;

public class AlreadySelfGivenAttributeException extends RuntimeException{
    public AlreadySelfGivenAttributeException(String message){
        super(message);
    }
}
