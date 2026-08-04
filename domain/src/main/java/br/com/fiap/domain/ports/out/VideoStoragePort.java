package br.com.fiap.domain.ports.out;

import java.io.InputStream;

public interface VideoStoragePort {
    /**
     * Stores video content and returns the storage key.
     *
     * @param key       storage key / path
     * @param stream    video input stream
     * @param sizeBytes content length in bytes
     * @param mimeType  MIME type (e.g. video/mp4)
     * @return the storage key used
     */
    String store(String key, InputStream stream, long sizeBytes, String mimeType);
}
