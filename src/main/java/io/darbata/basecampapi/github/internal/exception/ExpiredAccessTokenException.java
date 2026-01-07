package io.darbata.basecampapi.github.internal.exception;

public class ExpiredAccessTokenException extends RuntimeException {
    public ExpiredAccessTokenException(String message) {
        super(message);
    }
}
