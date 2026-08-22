package br.com.fiap.infrastructure.adapters.storage;

import br.com.fiap.domain.exceptions.VideoStorageException;
import br.com.fiap.domain.ports.out.VideoStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class LocalFileStorageAdapter implements VideoStoragePort {

    private static final Logger log = LoggerFactory.getLogger(LocalFileStorageAdapter.class);

    private final String basePath;

    public LocalFileStorageAdapter(@Value("${app.storage.local.path:./uploads}") String basePath) {
        this.basePath = basePath;
    }

    @Override
    public String store(String key, InputStream stream, long sizeBytes, String mimeType) {
        log.info("[STORAGE] Storing file key={} sizeBytes={} sizeMB={} mimeType={}",
                key, sizeBytes, String.format("%.2f", sizeBytes / (1024.0 * 1024.0)), mimeType);
        long startMs = System.currentTimeMillis();
        try {
            Path target = Paths.get(basePath, key);
            Files.createDirectories(target.getParent());
            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
            long durationMs = System.currentTimeMillis() - startMs;
            log.info("[STORAGE] File stored successfully key={} sizeBytes={} sizeMB={} durationMs={} path={}",
                    key, sizeBytes, String.format("%.2f", sizeBytes / (1024.0 * 1024.0)),
                    durationMs, target.toAbsolutePath());
            return key;
        } catch (IOException e) {
            long durationMs = System.currentTimeMillis() - startMs;
            log.error("[STORAGE] Failed to store file key={} sizeBytes={} durationMs={} error={}",
                    key, sizeBytes, durationMs, e.getMessage(), e);
            throw new VideoStorageException("Failed to store file locally: " + key, e);
        }
    }
}
