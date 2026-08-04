package br.com.fiap.domain.ports.out;

import br.com.fiap.domain.model.Video;

public interface VideoEventPublisherPort {
    void publishVideoUploaded(Video video);
}
