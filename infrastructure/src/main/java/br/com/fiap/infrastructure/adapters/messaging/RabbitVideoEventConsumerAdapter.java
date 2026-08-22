package br.com.fiap.infrastructure.adapters.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Consome eventos recebidos via RabbitMQ e registra o payload em formato JSON formatado. */
@Component
public class RabbitVideoEventConsumerAdapter {

    private static final Logger log = LoggerFactory.getLogger(RabbitVideoEventConsumerAdapter.class);

    private final ObjectMapper objectMapper;

    public RabbitVideoEventConsumerAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "#{@videoProcessedQueue.name}")
    public void onVideoProcessed(
            String message,
            @Header(value = "amqp_receivedExchange", required = false, defaultValue = "video.events") String exchange,
            @Header(value = "amqp_receivedRoutingKey", required = false, defaultValue = "video.processed") String routingKey) {
        try {
            Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {});
            String prettyBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
            log.info("<<< Payload recebido do RabbitMQ — exchange [{}] routing-key [{}]:\n{}",
                    exchange, routingKey, prettyBody);
        } catch (JsonProcessingException e) {
            log.warn("<<< Payload recebido do RabbitMQ — exchange [{}] routing-key [{}] (raw):\n{}",
                    exchange, routingKey, message);
        } catch (Exception e) {
            log.error("[MESSAGING] Erro ao processar mensagem recebida exchange={} routingKey={} error={}",
                    exchange, routingKey, e.getMessage(), e);
        }
    }
}
