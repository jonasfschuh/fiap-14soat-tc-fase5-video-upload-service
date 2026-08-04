package br.com.fiap.application.adviser;

import br.com.fiap.domain.exceptions.VideoAccessDeniedException;
import br.com.fiap.domain.exceptions.VideoNotFoundException;
import br.com.fiap.domain.exceptions.VideoStorageException;
import br.com.fiap.domain.exceptions.VideoValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(VideoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(VideoNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(VideoAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(VideoAccessDeniedException ex) {
        return error(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(VideoValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(VideoValidationException ex) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(VideoStorageException.class)
    public ResponseEntity<Map<String, Object>> handleStorage(VideoStorageException ex) {
        log.error("Storage error", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Video storage failure: " + ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return error(HttpStatus.PAYLOAD_TOO_LARGE, "File exceeds maximum allowed size (500 MB)");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBeanValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return error(HttpStatus.UNPROCESSABLE_ENTITY, errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        ));
    }
}
