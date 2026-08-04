package br.com.fiap.domain.exceptions;

public class VideoStorageException extends RuntimeException {
    public VideoStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
