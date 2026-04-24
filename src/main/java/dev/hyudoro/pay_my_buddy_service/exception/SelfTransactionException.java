package dev.hyudoro.pay_my_buddy_service.exception;

public class SelfTransactionException extends RuntimeException {
 public SelfTransactionException(String message){
        super(message);
    }
}
