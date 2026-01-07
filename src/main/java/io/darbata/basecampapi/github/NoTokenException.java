package io.darbata.basecampapi.github;

public class NoTokenException extends RuntimeException {
    public NoTokenException(String message) {
        super(message);
    }
}
