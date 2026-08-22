package br.com.fiap.domain.usecases;

import br.com.fiap.domain.exceptions.VideoValidationException;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.in.UploadVideoInputPort;
import br.com.fiap.domain.ports.out.VideoEventPublisherPort;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import br.com.fiap.domain.ports.out.VideoStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class UploadVideoUseCase implements UploadVideoInputPort {

    private static final Logger log = LoggerFactory.getLogger(UploadVideoUseCase.class);

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
        log.info("[UPLOAD] Starting upload userId={} filename={} sizeBytes={} sizeMB={} mimeType={}",
                userId, originalFilename, fileSizeBytes,
                fileSizeBytes != null ? String.format("%.2f", fileSizeBytes / (1024.0 * 1024.0)) : "unknown",
                mimeType);

        long startMs = System.currentTimeMillis();

        try {
            validate(userId, originalFilename, fileSizeBytes, mimeType);
        } catch (VideoValidationException ex) {
            log.warn("[UPLOAD] Validation failed userId={} filename={} sizeBytes={} mimeType={} reason={}",
                    userId, originalFilename, fileSizeBytes, mimeType, ex.getMessage());
            throw ex;
        }

        Video video = Video.create(userId, originalFilename, fileSizeBytes, mimeType);
        log.debug("[UPLOAD] Video entity created videoId={} userId={}", video.getId(), userId);

        String storageKey = buildStorageKey(userId, video.getId(), originalFilename);
        storage.store(storageKey, inputStream, fileSizeBytes, mimeType);
        video.setStorageKey(storageKey);
        log.info("[UPLOAD] Storage completed videoId={} userId={} storageKey={}", video.getId(), userId, storageKey);

        Video saved = repository.save(video);
        log.info("[UPLOAD] Video persisted videoId={} userId={} status={}", saved.getId(), userId, saved.getStatus());

        eventPublisher.publishVideoUploaded(saved);
        log.info("[UPLOAD] Event dispatched videoId={} userId={}", saved.getId(), userId);

        long durationMs = System.currentTimeMillis() - startMs;
        log.info("[UPLOAD] Completed videoId={} userId={} filename={} sizeBytes={} sizeMB={} durationMs={}",
                saved.getId(), userId, originalFilename, fileSizeBytes,
                String.format("%.2f", fileSizeBytes / (1024.0 * 1024.0)), durationMs);

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
