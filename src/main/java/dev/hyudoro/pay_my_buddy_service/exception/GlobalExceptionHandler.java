package dev.hyudoro.pay_my_buddy_service.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.hyudoro.pay_my_buddy_service.dto.ErrorResponse;



@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handle(EmailAlreadyExistsException error){
      String msg = error.getMessage();
      ErrorResponse response = new ErrorResponse(409, "EMAIL_ALREADY_EXISTS",msg);
      return ResponseEntity.status(HttpStatus.CONFLICT).body(response) ;
    }
}
