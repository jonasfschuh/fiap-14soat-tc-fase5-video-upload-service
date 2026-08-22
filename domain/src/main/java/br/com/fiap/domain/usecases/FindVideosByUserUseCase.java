package br.com.fiap.domain.usecases;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.in.FindVideosByUserInputPort;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FindVideosByUserUseCase implements FindVideosByUserInputPort {

    private static final Logger log = LoggerFactory.getLogger(FindVideosByUserUseCase.class);

    private final VideoRepositoryPort repository;

    public FindVideosByUserUseCase(VideoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<Video> execute(String userId) {
        List<Video> videos;
        if (userId == null || userId.isBlank()) {
            log.info("[QUERY] Listing all videos userId=ALL");
            videos = repository.findAll();
        } else {
            log.info("[QUERY] Listing videos userId={}", userId);
            videos = repository.findByUserId(userId);
        }
        log.info("[QUERY] Videos listed userId={} count={}", userId == null ? "ALL" : userId, videos.size());
        return videos;
    }
}
