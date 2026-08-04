package br.com.fiap.domain.usecases;

import br.com.fiap.domain.exceptions.VideoValidationException;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.in.UploadVideoInputPort;
import br.com.fiap.domain.ports.out.VideoEventPublisherPort;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import br.com.fiap.domain.ports.out.VideoStoragePort;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class UploadVideoUseCase implements UploadVideoInputPort {

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "video/mp4", "video/avi", "video/quicktime",
            "video/x-matroska", "video/webm", "video/x-msvideo",
            "video/x-ms-wmv", "video/x-flv"
    );

    private static final long MAX_FILE_SIZE = 500L * 1024 * 1024; // 500 MB

    private final VideoRepositoryPort repository;
    private final VideoStoragePort storage;
    private final VideoEventPublisherPort eventPublisher;

    public UploadVideoUseCase(VideoRepositoryPort repository,
                               VideoStoragePort storage,
                               VideoEventPublisherPort eventPublisher) {
        this.repository = repository;
        this.storage = storage;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Video execute(String userId, String originalFilename, Long fileSizeBytes,
                         String mimeType, InputStream inputStream) {
        validate(userId, originalFilename, fileSizeBytes, mimeType);

        Video video = Video.create(userId, originalFilename, fileSizeBytes, mimeType);

        String storageKey = buildStorageKey(userId, video.getId(), originalFilename);
        storage.store(storageKey, inputStream, fileSizeBytes, mimeType);
        video.setStorageKey(storageKey);

        Video saved = repository.save(video);

        eventPublisher.publishVideoUploaded(saved);

        return saved;
    }

    private void validate(String userId, String originalFilename, Long fileSizeBytes, String mimeType) {
        if (userId == null || userId.isBlank()) {
            throw new VideoValidationException("userId is required");
        }
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new VideoValidationException("originalFilename is required");
        }
        if (fileSizeBytes == null || fileSizeBytes <= 0) {
            throw new VideoValidationException("fileSizeBytes must be greater than zero");
        }
        if (fileSizeBytes > MAX_FILE_SIZE) {
            throw new VideoValidationException("File size exceeds maximum allowed (500 MB)");
        }
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase())) {
            throw new VideoValidationException("Unsupported video format: " + mimeType +
                    ". Allowed: mp4, avi, mov, mkv, webm");
        }
    }

    private String buildStorageKey(String userId, UUID videoId, String originalFilename) {
        return "videos/" + userId + "/" + videoId + "/" + originalFilename;
    }
}
