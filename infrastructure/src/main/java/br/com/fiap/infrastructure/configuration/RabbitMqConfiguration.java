package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.ports.out.VideoEventPublisherPort;
import br.com.fiap.infrastructure.adapters.messaging.RabbitVideoEventPublisherAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuração do RabbitMQ: exchanges, filas, bindings e beans. */
@Configuration
public class RabbitMqConfiguration {

    public static final String EXCHANGE_VIDEO_EVENTS = "video.events";
    public static final String QUEUE_VIDEO_UPLOADED = "video-uploaded";
    public static final String QUEUE_VIDEO_UPLOADED_DLQ = "video-uploaded-dlq";
    public static final String ROUTING_VIDEO_UPLOADED_DLQ = "video.uploaded.dlq";
    public static final String QUEUE_VIDEO_PROCESSED = "video-processed";
    public static final String QUEUE_VIDEO_PROCESSED_DLQ = "video-processed-dlq";
    public static final String ROUTING_VIDEO_PROCESSED_DLQ = "video.processed.dlq";

    @Bean
    public TopicExchange videoEventsExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_VIDEO_EVENTS).durable(true).build();
    }

    @Bean
    public Queue videoUploadedDlq() {
        return QueueBuilder.durable(QUEUE_VIDEO_UPLOADED_DLQ).build();
    }

    @Bean
    public Queue videoUploadedQueue() {
        return QueueBuilder.durable(QUEUE_VIDEO_UPLOADED)
                .withArgument("x-dead-letter-exchange", EXCHANGE_VIDEO_EVENTS)
                .withArgument("x-dead-letter-routing-key", ROUTING_VIDEO_UPLOADED_DLQ)
                .build();
    }

    @Bean
    public Binding videoUploadedBinding(Queue videoUploadedQueue, TopicExchange videoEventsExchange) {
        return BindingBuilder.bind(videoUploadedQueue).to(videoEventsExchange).with("video.uploaded");
    }

    @Bean
    public Binding videoUploadedDlqBinding(Queue videoUploadedDlq, TopicExchange videoEventsExchange) {
        return BindingBuilder.bind(videoUploadedDlq).to(videoEventsExchange).with(ROUTING_VIDEO_UPLOADED_DLQ);
    }

    @Bean
    public Queue videoProcessedDlq() {
        return QueueBuilder.durable(QUEUE_VIDEO_PROCESSED_DLQ).build();
    }

    @Bean
    public Queue videoProcessedQueue() {
        return QueueBuilder.durable(QUEUE_VIDEO_PROCESSED)
                .withArgument("x-dead-letter-exchange", EXCHANGE_VIDEO_EVENTS)
                .withArgument("x-dead-letter-routing-key", ROUTING_VIDEO_PROCESSED_DLQ)
                .build();
    }

    @Bean
    public Binding videoProcessedBinding(Queue videoProcessedQueue, TopicExchange videoEventsExchange) {
        return BindingBuilder.bind(videoProcessedQueue).to(videoEventsExchange).with("video.processed");
    }

    @Bean
    public Binding videoProcessedDlqBinding(Queue videoProcessedDlq, TopicExchange videoEventsExchange) {
        return BindingBuilder.bind(videoProcessedDlq).to(videoEventsExchange).with(ROUTING_VIDEO_PROCESSED_DLQ);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public VideoEventPublisherPort videoEventPublisherPort(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper,
            @org.springframework.beans.factory.annotation.Value("${app.storage.local.path:./uploads}") String storageBasePath) {
        return new RabbitVideoEventPublisherAdapter(rabbitTemplate, objectMapper, storageBasePath);
    }
}
