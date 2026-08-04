package br.com.fiap.domain.unit.exceptions;

import br.com.fiap.domain.exceptions.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionsTest {

    @Test
    void videoNotFoundException_shouldContainVideoId() {
        VideoNotFoundException ex = new VideoNotFoundException("abc-123");
        assertThat(ex.getMessage()).contains("abc-123");
    }

    @Test
    void videoAccessDeniedException_shouldContainVideoId() {
        VideoAccessDeniedException ex = new VideoAccessDeniedException("abc-123");
        assertThat(ex.getMessage()).contains("abc-123");
    }

    @Test
    void videoValidationException_shouldContainMessage() {
        VideoValidationException ex = new VideoValidationException("invalid format");
        assertThat(ex.getMessage()).isEqualTo("invalid format");
    }

    @Test
    void videoStorageException_shouldContainMessageAndCause() {
        RuntimeException cause = new RuntimeException("disk full");
        VideoStorageException ex = new VideoStorageException("storage failed", cause);
        assertThat(ex.getMessage()).isEqualTo("storage failed");
        assertThat(ex.getCause()).isEqualTo(cause);
    }
}
