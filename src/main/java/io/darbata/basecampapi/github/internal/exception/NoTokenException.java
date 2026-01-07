package io.darbata.basecampapi.github.internal.exception;

public class NoTokenException extends RuntimeException {
    public NoTokenException(String message) {
        super(message);
    }
}
