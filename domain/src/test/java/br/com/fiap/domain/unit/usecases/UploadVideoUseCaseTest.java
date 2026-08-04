package br.com.fiap.domain.unit.usecases;

import br.com.fiap.domain.exceptions.VideoValidationException;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;
import br.com.fiap.domain.ports.out.VideoEventPublisherPort;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import br.com.fiap.domain.ports.out.VideoStoragePort;
import br.com.fiap.domain.usecases.UploadVideoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadVideoUseCaseTest {

    @Mock private VideoRepositoryPort repository;
    @Mock private VideoStoragePort storage;
    @Mock private VideoEventPublisherPort eventPublisher;

    private UploadVideoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UploadVideoUseCase(repository, storage, eventPublisher);
    }

    @Test
    void shouldUploadVideoSuccessfully() {
        InputStream stream = new ByteArrayInputStream("video-content".getBytes());
        when(storage.store(anyString(), any(), anyLong(), anyString())).thenReturn("videos/user/uuid/video.mp4");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Video result = useCase.execute("user-123", "video.mp4", 1024L, "video/mp4", stream);

        assertThat(result.getStatus()).isEqualTo(VideoStatus.PENDING);
        assertThat(result.getUserId()).isEqualTo("user-123");
        verify(storage).store(anyString(), any(), anyLong(), anyString());
        verify(repository).save(any());
        verify(eventPublisher).publishVideoUploaded(any());
    }

    @Test
    void shouldRejectUnsupportedMimeType() {
        InputStream stream = new ByteArrayInputStream(new byte[0]);
        assertThatThrownBy(() -> useCase.execute("user-123", "file.txt", 1024L, "text/plain", stream))
                .isInstanceOf(VideoValidationException.class)
                .hasMessageContaining("Unsupported video format");
    }

    @Test
    void shouldRejectFileTooLarge() {
        InputStream stream = new ByteArrayInputStream(new byte[0]);
        long tooBig = 600L * 1024 * 1024;
        assertThatThrownBy(() -> useCase.execute("user-123", "video.mp4", tooBig, "video/mp4", stream))
                .isInstanceOf(VideoValidationException.class)
                .hasMessageContaining("500 MB");
    }

    @Test
    void shouldRejectBlankOriginalFilename() {
        InputStream stream = new ByteArrayInputStream(new byte[0]);
        assertThatThrownBy(() -> useCase.execute("user-123", "  ", 1024L, "video/mp4", stream))
                .isInstanceOf(VideoValidationException.class)
                .hasMessageContaining("originalFilename");
    }

    @Test
    void shouldRejectNonPositiveFileSize() {
        InputStream stream = new ByteArrayInputStream(new byte[0]);
        assertThatThrownBy(() -> useCase.execute("user-123", "video.mp4", 0L, "video/mp4", stream))
                .isInstanceOf(VideoValidationException.class)
                .hasMessageContaining("fileSizeBytes");
    }
}
