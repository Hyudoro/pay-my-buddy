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
        return buildErrorResponse(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS",error.getMessage());
    }

    @ExceptionHandler(SelfConnectionException.class)
    public ResponseEntity<ErrorResponse> handle(SelfConnectionException error){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "USER_ADD_HIMSELF",error.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(UserNotFoundException error){
        return buildErrorResponse(HttpStatus.NOT_FOUND,"USER_NOT_FOUND",error.getMessage());
    }

    @ExceptionHandler(ConnectionAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handle(ConnectionAlreadyExistsException error){
        return buildErrorResponse(HttpStatus.CONFLICT,"ALREADY_CONNECTED",error.getMessage());
    }

    @ExceptionHandler(ConnectionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(ConnectionNotFoundException error){
        return buildErrorResponse(HttpStatus.NOT_FOUND, "CONNECTION_NOT_FOUND", error.getMessage());
    }

    @ExceptionHandler(SenderInsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handle(SenderInsufficientBalanceException error){
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_BALANCE", error.getMessage());
    }

    @ExceptionHandler(SelfTransactionException.class)
    public ResponseEntity<ErrorResponse> handle(SelfTransactionException error){
        return buildErrorResponse(HttpStatus.BAD_REQUEST,"USER_SELF_TRANSACTION", error.getMessage());
    }



    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String errorTag, String message){
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(),errorTag,message));
    }

}
