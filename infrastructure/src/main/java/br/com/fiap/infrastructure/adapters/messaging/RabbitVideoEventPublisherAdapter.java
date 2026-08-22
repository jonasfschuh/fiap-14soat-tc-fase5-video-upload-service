package br.com.fiap.infrastructure.adapters.messaging;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.out.VideoEventPublisherPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/** Publica evento video-uploaded no RabbitMQ (exchange video.events). */
public class RabbitVideoEventPublisherAdapter implements VideoEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitVideoEventPublisherAdapter.class);
    private static final String EXCHANGE = "video.events";
    private static final String ROUTING_KEY = "video.uploaded";
    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

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
            payload.put("timestamp", ZonedDateTime.now(ZONE).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            String body = objectMapper.writeValueAsString(payload);
            String prettyBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
            log.info(">>> Payload enviado ao RabbitMQ — exchange [{}] routing-key [{}]:\n{}",
                    EXCHANGE, ROUTING_KEY, prettyBody);
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, body);
            log.info("[MESSAGING] Event published successfully videoId={} userId={} exchange={} routingKey={}",
                    video.getId(), video.getUserId(), EXCHANGE, ROUTING_KEY);
        } catch (JsonProcessingException e) {
            log.error("[MESSAGING] Failed to serialize event videoId={} userId={} error={}",
                    video.getId(), video.getUserId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish video event", e);
        } catch (Exception e) {
            log.error("[MESSAGING] Failed to publish event videoId={} userId={} exchange={} routingKey={} error={}",
                    video.getId(), video.getUserId(), EXCHANGE, ROUTING_KEY, e.getMessage(), e);
            throw e;
        }
    }
}
