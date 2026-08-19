package br.com.fiap.application.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints de teste para validação do ambiente local.
 * ⚠️ Use apenas em desenvolvimento — não habilitar em produção.
 */
@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Endpoints de teste — uso exclusivo em desenvolvimento local")
public class TestController {

    private static final String EXCHANGE  = "video.events";
    private static final String ROUTING_KEY = "video.uploaded";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper   objectMapper;

    public TestController(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper   = objectMapper;
    }

    @PostMapping("/rabbitmq")
    @Operation(
        summary     = "Publicar mensagem de teste no RabbitMQ",
        description = "Publica um evento 'video.uploaded' de teste no exchange 'video.events' "
                    + "para validar a conectividade com o RabbitMQ provisionado pelo iac-terraform."
    )
    public ResponseEntity<Map<String, Object>> publishTestMessage(
            @RequestParam(value = "userId",   defaultValue = "user-test") String userId,
            @RequestParam(value = "filename", defaultValue = "test-video.mp4") String filename) {

        String videoId = UUID.randomUUID().toString();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("videoId",          videoId);
        payload.put("userId",           userId);
        payload.put("storageKey",       "videos/" + userId + "/" + videoId + "/" + filename);
        payload.put("originalFilename", filename);
        payload.put("fileSizeBytes",    1_048_576L);
        payload.put("mimeType",         "video/mp4");
        payload.put("timestamp",        Instant.now().toString());
        payload.put("testMessage",      true);

        try {
            String body = objectMapper.writeValueAsString(payload);
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize test event", e);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status",    "published");
        response.put("exchange",  EXCHANGE);
        response.put("routingKey", ROUTING_KEY);
        response.put("payload",   payload);

        return ResponseEntity.ok(response);
    }
}
