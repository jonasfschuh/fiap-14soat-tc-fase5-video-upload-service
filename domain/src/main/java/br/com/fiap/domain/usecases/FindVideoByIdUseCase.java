package br.com.fiap.domain.usecases;

import br.com.fiap.domain.exceptions.VideoAccessDeniedException;
import br.com.fiap.domain.exceptions.VideoNotFoundException;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.in.FindVideoByIdInputPort;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class FindVideoByIdUseCase implements FindVideoByIdInputPort {

    private static final Logger log = LoggerFactory.getLogger(FindVideoByIdUseCase.class);

    private final VideoRepositoryPort repository;

    public FindVideoByIdUseCase(VideoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Video execute(UUID videoId, String userId) {
        log.info("[QUERY] Finding video videoId={} userId={}", videoId, userId);

        Video video = repository.findById(videoId)
                .orElseThrow(() -> {
                    log.warn("[QUERY] Video not found videoId={} userId={}", videoId, userId);
                    return new VideoNotFoundException(videoId.toString());
                });

        if (!video.getUserId().equals(userId)) {
            log.warn("[QUERY] Access denied videoId={} requestedUserId={} ownerUserId={}",
                    videoId, userId, video.getUserId());
            throw new VideoAccessDeniedException(videoId.toString());
        }

        log.info("[QUERY] Video found videoId={} userId={} status={}", videoId, userId, video.getStatus());
        return video;
    }
}
