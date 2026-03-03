package io.darbata.basecampapi.common;

import io.darbata.basecampapi.github.NoGithubTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class HttpAdvice {

    @ExceptionHandler(NoGithubTokenException.class)
    public ResponseEntity<ApiErrorResponse> noGithubTokenException(NoGithubTokenException e) {
        ApiErrorResponse error = new ApiErrorResponse(
                ApiError.GITHUB_LINK_REQUIRED.name(),
                e.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

}