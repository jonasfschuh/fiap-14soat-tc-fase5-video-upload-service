package br.com.fiap.application.unit.adviser;

import br.com.fiap.application.adviser.GlobalExceptionHandler;
import br.com.fiap.domain.exceptions.VideoAccessDeniedException;
import br.com.fiap.domain.exceptions.VideoNotFoundException;
import br.com.fiap.domain.exceptions.VideoStorageException;
import br.com.fiap.domain.exceptions.VideoValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler - Unit Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturn404ForVideoNotFound() {
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(new VideoNotFoundException("abc"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn403ForAccessDenied() {
        ResponseEntity<Map<String, Object>> response = handler.handleAccessDenied(new VideoAccessDeniedException("abc"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldReturn422ForValidation() {
        ResponseEntity<Map<String, Object>> response = handler.handleValidation(new VideoValidationException("bad format"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    @DisplayName("handleStorage returns 500 with storage failure message")
    void shouldReturn500ForStorageException() {
        ResponseEntity<Map<String, Object>> response = handler.handleStorage(new VideoStorageException("disk full", new RuntimeException()));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message").toString()).contains("disk full");
    }

    @Test
    @DisplayName("handleMaxUploadSize returns 413")
    void shouldReturn413ForMaxUploadSizeExceeded() {
        ResponseEntity<Map<String, Object>> response = handler.handleMaxUploadSize(new MaxUploadSizeExceededException(500L));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        assertThat(response.getBody().get("message").toString()).contains("500 MB");
    }

    @Test
    @DisplayName("handleGeneric returns 500 for unexpected exceptions")
    void shouldReturn500ForGenericException() {
        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(new RuntimeException("unexpected"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().get("message")).isEqualTo("Internal server error");
    }
}
