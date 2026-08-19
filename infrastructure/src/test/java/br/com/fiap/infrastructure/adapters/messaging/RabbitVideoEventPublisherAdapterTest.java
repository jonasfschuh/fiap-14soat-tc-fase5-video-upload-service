package br.com.fiap.infrastructure.adapters.messaging;

import br.com.fiap.domain.model.Video;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RabbitVideoEventPublisherAdapter - Unit Tests")
class RabbitVideoEventPublisherAdapterTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitVideoEventPublisherAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RabbitVideoEventPublisherAdapter(rabbitTemplate, new ObjectMapper());
    }

    @Test
    @DisplayName("publishVideoUploaded sends message to RabbitMQ")
    void publishVideoUploaded_sendsMessageToRabbitMq() {
        Video video = Video.create("user-1", "test.mp4", 1024L, "video/mp4");
        video.setStorageKey("videos/user-1/uuid/test.mp4");

        adapter.publishVideoUploaded(video);

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("publishVideoUploaded re-throws when RabbitMQ client fails")
    void publishVideoUploaded_whenRabbitThrows_propagatesException() {
        Video video = Video.create("user-1", "test.mp4", 1024L, "video/mp4");
        video.setStorageKey("videos/user-1/uuid/test.mp4");
        doThrow(new RuntimeException("RabbitMQ unavailable"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());

        assertThatThrownBy(() -> adapter.publishVideoUploaded(video))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("RabbitMQ unavailable");
    }
}
