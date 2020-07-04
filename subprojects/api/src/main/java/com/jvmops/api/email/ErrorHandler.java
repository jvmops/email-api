package com.jvmops.api.email;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private Clock clock;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        ErrorMessage errorMessage = getErrorMessage(message, status, (ServletWebRequest) request);
        return handleExceptionInternal(ex, errorMessage, headers, status, request);
    }

    @ExceptionHandler(EmailMessageNotFound.class)
    protected ResponseEntity<Object> handleEmailMessageNotFound(EmailMessageNotFound ex, WebRequest request) {
        ErrorMessage errorMessage = getErrorMessage(ex.getMessage(), HttpStatus.NOT_FOUND, (ServletWebRequest) request);
        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    private ErrorMessage getErrorMessage(String message, HttpStatus status, ServletWebRequest request) {
        return ErrorMessage.builder()
                .message(message)
                .statusCode(status.value())
                .error(status.getReasonPhrase())
                .path(request.getRequest().getServletPath())
                .timestamp(OffsetDateTime.now(clock))
                .build();
    }

    @Getter
    public static class EmailMessageNotFound extends RuntimeException {
        private final ObjectId id;

        public EmailMessageNotFound(ObjectId id) {
            super(String.format("Email message not found. Unknown id: %s", id));
            this.id = id;
        }
    }
}
