package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.ports.out.VideoStoragePort;
import br.com.fiap.infrastructure.adapters.storage.LocalFileStorageAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VideoStorageBeanConfiguration - Unit Tests")
class VideoStorageBeanConfigurationTest {

    private final VideoStorageBeanConfiguration config = new VideoStorageBeanConfiguration();

    @Test
    @DisplayName("localFileStoragePort creates a LocalFileStorageAdapter")
    void localFileStoragePort_createsLocalAdapter() {
        VideoStoragePort port = config.localFileStoragePort("./test-uploads");

        assertThat(port).isNotNull().isInstanceOf(LocalFileStorageAdapter.class);
    }
}
