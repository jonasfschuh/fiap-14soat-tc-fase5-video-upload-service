package br.com.fiap.infrastructure.adapters.messaging;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.out.VideoEventPublisherPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/** Publica evento video-uploaded no RabbitMQ (exchange video.events). */
public class RabbitVideoEventPublisherAdapter implements VideoEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitVideoEventPublisherAdapter.class);
    private static final String EXCHANGE = "video.events";
    private static final String ROUTING_KEY = "video.uploaded";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitVideoEventPublisherAdapter(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishVideoUploaded(Video video) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("videoId", video.getId().toString());
            payload.put("userId", video.getUserId());
            payload.put("storageKey", video.getStorageKey());
            payload.put("originalFilename", video.getOriginalFilename());
            payload.put("fileSizeBytes", video.getFileSizeBytes());
            payload.put("mimeType", video.getMimeType());
            payload.put("timestamp", Instant.now().toString());
            String body = objectMapper.writeValueAsString(payload);
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, body);
            log.info("[RabbitMQ] Published video-uploaded event for videoId={}", video.getId());
        } catch (JsonProcessingException e) {
            log.error("[RabbitMQ] Failed to serialize video-uploaded event for videoId={}", video.getId(), e);
            throw new RuntimeException("Failed to publish video event", e);
        }
    }
}
