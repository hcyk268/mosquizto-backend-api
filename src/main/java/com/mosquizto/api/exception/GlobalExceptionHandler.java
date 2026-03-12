package com.mosquizto.api.exception;

import com.mosquizto.api.dto.response.ErrorResponseException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponseException handleValidationException(Exception e, WebRequest request) {

        log.error("Validation exception: {}", e.getMessage());

        ErrorResponseException errorResponse = new ErrorResponseException();

        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        String message = e.getMessage();
        if (e instanceof MethodArgumentNotValidException) {
            int start = message.lastIndexOf("[") + 1;
            int end = message.lastIndexOf("]") - 1;
            message = message.substring(start, end);
            errorResponse.setError("Invalid Payload");
            errorResponse.setMessage(message);
        } else {
            errorResponse.setError("Invalid Data");
            errorResponse.setMessage(message);
        }

        return errorResponse;
    }


    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponseException handleInvalidTokenException(InvalidTokenException e, WebRequest request) {
        log.error("Invalid token: {}", e.getMessage());

        ErrorResponseException errorResponse = new ErrorResponseException();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(UNAUTHORIZED.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError("Unauthorized");
        errorResponse.setMessage(e.getMessage());

        return errorResponse;
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponseException handleExpiredJwtException(ExpiredJwtException e, WebRequest request) {
        log.error("Expired JWT token: {}", e.getMessage());

        ErrorResponseException errorResponse = new ErrorResponseException();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(UNAUTHORIZED.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError("Unauthorized");
        errorResponse.setMessage("Token has expired");

        return errorResponse;
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponseException handleJwtException(JwtException e, WebRequest request) {
        log.error("JWT exception: {}", e.getMessage());

        ErrorResponseException errorResponse = new ErrorResponseException();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(UNAUTHORIZED.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setError("Unauthorized");
        errorResponse.setMessage("Invalid token");

        return errorResponse;
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponseException handleInvalidDataException(InvalidDataException e, WebRequest request) {

        ErrorResponseException exception = new ErrorResponseException();
        exception.setTimestamp(new Date());
        exception.setStatus(BAD_REQUEST.value());
        exception.setPath(request.getDescription(false).replace("uri=", ""));
        exception.setError("Invalid Payload");
        exception.setMessage(e.getMessage());

        return exception;
    }

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorResponseException handleDisabledException(DisabledException e, WebRequest request) {
        ErrorResponseException err = new ErrorResponseException();
        err.setTimestamp(new Date());
        err.setStatus(FORBIDDEN.value());
        err.setPath(request.getDescription(false).replace("uri=", ""));
        err.setError("Forbidden");
        err.setMessage("Account is not activated. Please verify your email.");
        return err;
    }


}
