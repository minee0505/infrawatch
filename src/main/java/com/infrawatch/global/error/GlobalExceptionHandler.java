package com.infrawatch.global.error;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 컨트롤러마다 try-catch 를 반복하지 않도록 예외 처리를 한 곳으로 모았다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("business exception: {} - {}", errorCode.getCode(), e.getMessage());
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getStatus())
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, detail));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        String detail = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getStatus())
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, detail));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String detail = e.getName() + " 값이 올바르지 않습니다.";
        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getStatus())
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, detail));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        log.warn("access denied: {}", e.getMessage());
        return ResponseEntity.status(ErrorCode.ACCESS_DENIED.getStatus())
                .body(ErrorResponse.of(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("unhandled exception", e);
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getMessage()));
    }
}
