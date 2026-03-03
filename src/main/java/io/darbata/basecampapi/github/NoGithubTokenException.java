package io.darbata.basecampapi.github;

public class NoGithubTokenException extends RuntimeException {
    public NoGithubTokenException(String message) {
        super(message);
    }
}