package dev.hyudoro.pay_my_buddy_service.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String message){
         super(message);
     }
}
