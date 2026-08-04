package br.com.fiap.domain.ports.in;

import br.com.fiap.domain.model.Video;

import java.io.InputStream;

public interface UploadVideoInputPort {
    Video execute(String userId, String originalFilename, Long fileSizeBytes, String mimeType, InputStream inputStream);
}
