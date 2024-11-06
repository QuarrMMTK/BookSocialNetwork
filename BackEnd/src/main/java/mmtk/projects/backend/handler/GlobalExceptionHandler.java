package mmtk.projects.backend.handler;

import jakarta.mail.MessagingException;
import mmtk.projects.backend.exception.OperationNotPermittedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static mmtk.projects.backend.handler.BusinessErrorCode.*;
import static org.springframework.http.HttpStatus.*;

/**
 * Author : Min Myat Thu Kha
 * Created At : 03/11/2024, Nov ,14, 24
 * Project Name : BookSocialNetwork
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp){
        return ResponseEntity.status(UNAUTHORIZED).body(ExceptionResponse.builder()
                .businessErrorCode(ACCOUNT_LOCKED.getCode())
                .businessExceptionDescription(ACCOUNT_LOCKED.getDescription())
                .error(exp.getMessage())
                .build());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp){
        return ResponseEntity.status(UNAUTHORIZED).body(ExceptionResponse.builder()
                .businessErrorCode(ACCOUNT_DISABLE.getCode())
                .businessExceptionDescription(ACCOUNT_DISABLE.getDescription())
                .error(exp.getMessage())
                .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp){
        return ResponseEntity.status(UNAUTHORIZED).body(ExceptionResponse.builder()
                .businessErrorCode(BAD_CREDENTIALS.getCode())
                .businessExceptionDescription(BAD_CREDENTIALS.getDescription())
                .error(BAD_CREDENTIALS.getDescription())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp){
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors().forEach(error -> {
            var errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return ResponseEntity.status(BAD_REQUEST).body(ExceptionResponse.builder()
                .validationErrors(errors)
                .build());
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp){
        return ResponseEntity.status(BAD_REQUEST).body(ExceptionResponse.builder()
                .error(exp.getMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp){
//        log the exception
        exp.printStackTrace();
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ExceptionResponse.builder()
                .businessExceptionDescription("Internal error, contact the admin")
                .error(exp.getMessage())
                .build());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp){
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ExceptionResponse.builder()
                .error(exp.getMessage())
                .build());
    }
}
