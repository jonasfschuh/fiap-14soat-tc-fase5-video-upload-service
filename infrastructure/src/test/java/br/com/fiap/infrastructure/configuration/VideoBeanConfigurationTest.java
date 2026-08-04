package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.ports.in.FindVideoByIdInputPort;
import br.com.fiap.domain.ports.in.FindVideosByUserInputPort;
import br.com.fiap.domain.ports.in.UploadVideoInputPort;
import br.com.fiap.domain.ports.out.VideoEventPublisherPort;
import br.com.fiap.domain.ports.out.VideoRepositoryPort;
import br.com.fiap.domain.ports.out.VideoStoragePort;
import br.com.fiap.infrastructure.adapters.repositories.VideoJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class VideoBeanConfigurationTest {

    @Mock private VideoJpaRepository jpaRepository;
    @Mock private VideoStoragePort storagePort;
    @Mock private VideoEventPublisherPort eventPublisherPort;

    @Test
    void shouldCreateAllBeans() {
        VideoBeanConfiguration config = new VideoBeanConfiguration();

        VideoRepositoryPort repo = config.videoRepositoryPort(jpaRepository);
        assertThat(repo).isNotNull();

        UploadVideoInputPort uploadPort = config.uploadVideoInputPort(repo, storagePort, eventPublisherPort);
        assertThat(uploadPort).isNotNull();

        FindVideosByUserInputPort findByUser = config.findVideosByUserInputPort(repo);
        assertThat(findByUser).isNotNull();

        FindVideoByIdInputPort findById = config.findVideoByIdInputPort(repo);
        assertThat(findById).isNotNull();
    }
}
