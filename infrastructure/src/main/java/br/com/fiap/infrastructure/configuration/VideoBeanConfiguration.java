package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.ports.in.FindVideoByIdInputPort;
import br.com.fiap.domain.ports.in.FindVideosByUserInputPort;
import br.com.fiap.domain.ports.in.UploadVideoInputPort;
import br.com.fiap.domain.ports.out.VideoEventPublisherPort;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import br.com.fiap.domain.ports.out.VideoStoragePort;
import br.com.fiap.domain.usecases.FindVideoByIdUseCase;
import br.com.fiap.domain.usecases.FindVideosByUserUseCase;
import br.com.fiap.domain.usecases.UploadVideoUseCase;
import br.com.fiap.infrastructure.adapters.repositories.VideoJpaRepository;
import br.com.fiap.infrastructure.adapters.repositories.VideoRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class VideoBeanConfiguration {

    @Bean
    public VideoRepositoryPort videoRepositoryPort(VideoJpaRepository jpaRepository) {
        return new VideoRepositoryImpl(jpaRepository);
    }

    @Bean
    public UploadVideoInputPort uploadVideoInputPort(VideoRepositoryPort repository,
                                                      VideoStoragePort storage,
                                                      VideoEventPublisherPort eventPublisher) {
        return new UploadVideoUseCase(repository, storage, eventPublisher);
    }

    @Bean
    public FindVideosByUserInputPort findVideosByUserInputPort(VideoRepositoryPort repository) {
        return new FindVideosByUserUseCase(repository);
    }

    @Bean
    public FindVideoByIdInputPort findVideoByIdInputPort(VideoRepositoryPort repository) {
        return new FindVideoByIdUseCase(repository);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
