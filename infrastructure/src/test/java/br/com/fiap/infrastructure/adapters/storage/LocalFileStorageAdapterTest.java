package br.com.fiap.infrastructure.adapters.storage;

import br.com.fiap.domain.exceptions.VideoStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LocalFileStorageAdapter - Unit Tests")
class LocalFileStorageAdapterTest {

    @TempDir
    Path tempDir;

    private LocalFileStorageAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new LocalFileStorageAdapter(tempDir.toString());
    }

    @Test
    @DisplayName("store writes file to local directory and returns key")
    void store_writesFileToLocalPath_andReturnsKey() throws IOException {
        byte[] content = "video-content".getBytes();
        InputStream stream = new ByteArrayInputStream(content);

        String key = adapter.store("videos/user-1/uuid/test.mp4", stream, content.length, "video/mp4");

        assertThat(key).isEqualTo("videos/user-1/uuid/test.mp4");
        Path stored = tempDir.resolve("videos/user-1/uuid/test.mp4");
        assertThat(Files.exists(stored)).isTrue();
        assertThat(Files.readAllBytes(stored)).isEqualTo(content);
    }

    @Test
    @DisplayName("store creates nested directories automatically")
    void store_createsNestedDirectories() {
        InputStream stream = new ByteArrayInputStream("data".getBytes());

        adapter.store("a/b/c/d/file.mp4", stream, 4L, "video/mp4");

        assertThat(Files.exists(tempDir.resolve("a/b/c/d/file.mp4"))).isTrue();
    }

    @Test
    @DisplayName("store throws VideoStorageException when stream read fails")
    void store_whenInputStreamThrows_throwsVideoStorageException() {
        InputStream failingStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("simulated disk failure");
            }
        };

        assertThatThrownBy(() -> adapter.store("videos/user/uuid/test.mp4", failingStream, 100L, "video/mp4"))
                .isInstanceOf(VideoStorageException.class)
                .hasMessageContaining("Failed to store file locally");
    }
}
