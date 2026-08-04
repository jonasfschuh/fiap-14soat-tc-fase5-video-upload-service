package br.com.fiap.domain.exceptions;

public class VideoValidationException extends RuntimeException {
    public VideoValidationException(String message) {
        super(message);
    }
}
