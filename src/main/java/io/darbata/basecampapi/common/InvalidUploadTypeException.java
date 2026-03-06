package io.darbata.basecampapi.common;

public class InvalidUploadTypeException extends RuntimeException {
    public InvalidUploadTypeException(String message) {
        super(message);
    }
}