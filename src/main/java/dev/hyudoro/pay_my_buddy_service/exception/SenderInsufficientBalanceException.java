package dev.hyudoro.pay_my_buddy_service.exception;

public class SenderInsufficientBalanceException extends RuntimeException{

    public SenderInsufficientBalanceException(String message){
        super(message);
    }
}
