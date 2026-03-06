package io.darbata.basecampapi.cloud;

public class InvalidPresignRequestException extends RuntimeException {
    public InvalidPresignRequestException(String message) {
        super(message);
    }
}