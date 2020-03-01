package blog.help42.springidempotencykeydemo.controller;

import blog.help42.springidempotencykeydemo.exception.ErrorInfo;
import blog.help42.springidempotencykeydemo.exception.IdempotencyKeyException;
import blog.help42.springidempotencykeydemo.utils.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class CommonExceptionHandler {
    @Autowired
    private RestUtils restUtils;

    @ExceptionHandler(IdempotencyKeyException.class)
    public ResponseEntity<ErrorInfo> handleIdempotencyKeyException(IdempotencyKeyException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .location(restUtils.getResourceLocation(e.getResourcePath(), e.getResourceId()))
                .body(new ErrorInfo("Idempotency key violation"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorInfo> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorInfo("Data integrity error"));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorInfo> handleNoSuchElementExceptionException(NoSuchElementException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorInfo(e.getMessage()));
    }
}
