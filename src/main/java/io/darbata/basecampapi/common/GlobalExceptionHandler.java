package io.darbata.basecampapi.common;

import io.darbata.basecampapi.cloud.InvalidPresignRequestException;
import io.darbata.basecampapi.github.*;
import io.darbata.basecampapi.projects.AssignmentIssueException;
import org.junit.jupiter.api.Order;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AssignmentIssueException.class)
    public ResponseEntity<AssignmentIssueException> assignmentIssueException(AssignmentIssueException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex);
    }

    @ExceptionHandler(NoTokenException.class)
    public ResponseEntity<Object> handleNoTokenException(NoTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidUploadTypeException.class)
    public ResponseEntity<Object> handleInvalidUploadTypeException(InvalidUploadTypeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPresignRequestException.class)
    public ResponseEntity<Object> handleInvalidPresignRequestException(InvalidPresignRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ExpiredAccessTokenException.class)
    public ResponseEntity<Object> handleExpiredAccessTokenException(ExpiredAccessTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<Object> handleTokenGenerationException(TokenGenerationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(NoGithubTokenException.class)
    public ResponseEntity<Object> handleNoGithubTokenException(NoGithubTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(GithubCodeTokenExchangeException.class)
    public ResponseEntity<Object> handleGithubCodeTokenExchangeException(GithubCodeTokenExchangeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<Object> handleExpiredRefreshTokenException(ExpiredRefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @Order(Ordered.LOWEST_PRECEDENCE)
    public ResponseEntity<Object> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}