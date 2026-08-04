package br.com.fiap.infrastructure.adapters.storage;

import br.com.fiap.domain.exceptions.VideoStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("S3StorageAdapter - Unit Tests")
class S3StorageAdapterTest {

    @Mock private S3Client s3Client;

    private S3StorageAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new S3StorageAdapter(s3Client, "fiap-video-uploads");
    }

    @Test
    @DisplayName("store uploads to S3 and returns the storage key")
    void store_uploadsToS3AndReturnsKey() {
        InputStream stream = new ByteArrayInputStream("video".getBytes());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String key = adapter.store("videos/user-1/uuid/test.mp4", stream, 5L, "video/mp4");

        assertThat(key).isEqualTo("videos/user-1/uuid/test.mp4");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("store throws VideoStorageException when S3 client throws")
    void store_whenS3Fails_throwsVideoStorageException() {
        InputStream stream = new ByteArrayInputStream("video".getBytes());
        doThrow(new RuntimeException("S3 unavailable"))
                .when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        assertThatThrownBy(() -> adapter.store("videos/user-1/uuid/test.mp4", stream, 5L, "video/mp4"))
                .isInstanceOf(VideoStorageException.class)
                .hasMessageContaining("Failed to upload to S3");
    }
}
