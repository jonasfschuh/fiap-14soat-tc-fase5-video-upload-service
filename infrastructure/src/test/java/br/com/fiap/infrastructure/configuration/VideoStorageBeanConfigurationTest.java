package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.ports.out.VideoStoragePort;
import br.com.fiap.infrastructure.adapters.storage.LocalFileStorageAdapter;
import br.com.fiap.infrastructure.adapters.storage.S3StorageAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("VideoStorageBeanConfiguration - Unit Tests")
class VideoStorageBeanConfigurationTest {

    private final VideoStorageBeanConfiguration config = new VideoStorageBeanConfiguration();

    @Test
    @DisplayName("localFileStoragePort creates a LocalFileStorageAdapter")
    void localFileStoragePort_createsLocalAdapter() {
        VideoStoragePort port = config.localFileStoragePort("./test-uploads");

        assertThat(port).isNotNull().isInstanceOf(LocalFileStorageAdapter.class);
    }

    @Test
    @DisplayName("s3StoragePort creates a S3StorageAdapter")
    void s3StoragePort_createsS3Adapter() {
        S3Client s3Client = mock(S3Client.class);

        VideoStoragePort port = config.s3StoragePort(s3Client, "test-bucket");

        assertThat(port).isNotNull().isInstanceOf(S3StorageAdapter.class);
    }
}
