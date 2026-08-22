package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.ports.out.VideoEventPublisherPort;
import br.com.fiap.infrastructure.adapters.messaging.RabbitVideoEventPublisherAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("RabbitMqConfiguration - Unit Tests")
class RabbitMqConfigurationTest {

    private final RabbitMqConfiguration config = new RabbitMqConfiguration();

    @Test
    @DisplayName("videoEventsExchange creates durable topic exchange")
    void videoEventsExchange_createsDurableTopicExchange() {
        TopicExchange exchange = config.videoEventsExchange();

        assertThat(exchange.getName()).isEqualTo(RabbitMqConfiguration.EXCHANGE_VIDEO_EVENTS);
        assertThat(exchange.isDurable()).isTrue();
    }

    @Test
    @DisplayName("videoUploadedDlq creates durable queue")
    void videoUploadedDlq_createsDurableQueue() {
        Queue queue = config.videoUploadedDlq();

        assertThat(queue.getName()).isEqualTo(RabbitMqConfiguration.QUEUE_VIDEO_UPLOADED_DLQ);
        assertThat(queue.isDurable()).isTrue();
    }

    @Test
    @DisplayName("videoUploadedQueue creates durable queue with DLQ arguments")
    void videoUploadedQueue_createsDurableQueueWithDlq() {
        Queue queue = config.videoUploadedQueue();

        assertThat(queue.getName()).isEqualTo(RabbitMqConfiguration.QUEUE_VIDEO_UPLOADED);
        assertThat(queue.isDurable()).isTrue();
        assertThat(queue.getArguments())
                .containsEntry("x-dead-letter-exchange", "")
                .containsEntry("x-dead-letter-routing-key", RabbitMqConfiguration.QUEUE_VIDEO_UPLOADED_DLQ);
    }

    @Test
    @DisplayName("videoUploadedBinding binds queue to exchange with routing key")
    void videoUploadedBinding_bindsQueueToExchange() {
        Queue queue = config.videoUploadedQueue();
        TopicExchange exchange = config.videoEventsExchange();

        Binding binding = config.videoUploadedBinding(queue, exchange);

        assertThat(binding.getExchange()).isEqualTo(RabbitMqConfiguration.EXCHANGE_VIDEO_EVENTS);
        assertThat(binding.getDestination()).isEqualTo(RabbitMqConfiguration.QUEUE_VIDEO_UPLOADED);
        assertThat(binding.getRoutingKey()).isEqualTo("video.uploaded");
    }

    @Test
    @DisplayName("messageConverter creates Jackson converter")
    void messageConverter_createsJacksonConverter() {
        Jackson2JsonMessageConverter converter = config.messageConverter();

        assertThat(converter).isNotNull();
    }

    @Test
    @DisplayName("videoEventPublisherPort returns Rabbit adapter")
    void videoEventPublisherPort_returnsRabbitAdapter() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        ObjectMapper objectMapper = new ObjectMapper();

        VideoEventPublisherPort port = config.videoEventPublisherPort(rabbitTemplate, objectMapper);

        assertThat(port).isNotNull().isInstanceOf(RabbitVideoEventPublisherAdapter.class);
    }

    @Test
    @DisplayName("videoProcessedDlq creates durable queue")
    void videoProcessedDlq_createsDurableQueue() {
        Queue queue = config.videoProcessedDlq();

        assertThat(queue.getName()).isEqualTo(RabbitMqConfiguration.QUEUE_VIDEO_PROCESSED_DLQ);
        assertThat(queue.isDurable()).isTrue();
    }

    @Test
    @DisplayName("videoProcessedQueue creates durable queue with DLQ arguments")
    void videoProcessedQueue_createsDurableQueueWithDlq() {
        Queue queue = config.videoProcessedQueue();

        assertThat(queue.getName()).isEqualTo(RabbitMqConfiguration.QUEUE_VIDEO_PROCESSED);
        assertThat(queue.isDurable()).isTrue();
        assertThat(queue.getArguments())
                .containsEntry("x-dead-letter-exchange", RabbitMqConfiguration.EXCHANGE_VIDEO_EVENTS)
                .containsEntry("x-dead-letter-routing-key", RabbitMqConfiguration.QUEUE_VIDEO_PROCESSED_DLQ);
    }

    @Test
    @DisplayName("videoProcessedBinding binds queue to exchange with routing key")
    void videoProcessedBinding_bindsQueueToExchange() {
        Queue queue = config.videoProcessedQueue();
        TopicExchange exchange = config.videoEventsExchange();

        Binding binding = config.videoProcessedBinding(queue, exchange);

        assertThat(binding.getExchange()).isEqualTo(RabbitMqConfiguration.EXCHANGE_VIDEO_EVENTS);
        assertThat(binding.getDestination()).isEqualTo(RabbitMqConfiguration.QUEUE_VIDEO_PROCESSED);
        assertThat(binding.getRoutingKey()).isEqualTo("video.processed");
    }
}
