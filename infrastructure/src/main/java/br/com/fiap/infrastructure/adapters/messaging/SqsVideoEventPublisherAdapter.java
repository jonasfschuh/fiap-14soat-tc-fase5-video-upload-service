package br.com.fiap.infrastructure.adapters.messaging;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.out.VideoEventPublisherPort;
import br.com.fiap.infrastructure.logging.SqsMessageLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class SqsVideoEventPublisherAdapter implements VideoEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(SqsVideoEventPublisherAdapter.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final SqsMessageLogger sqsMessageLogger;
    private final String queueUrl;

    public SqsVideoEventPublisherAdapter(SqsClient sqsClient,
                                          ObjectMapper objectMapper,
                                          SqsMessageLogger sqsMessageLogger,
                                          String queueUrl) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.sqsMessageLogger = sqsMessageLogger;
        this.queueUrl = queueUrl;
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

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(body)
                    .build();

            sqsClient.sendMessage(request);
            sqsMessageLogger.logMessageSent(queueUrl, body, "VIDEO_UPLOADED", null);
            log.info("[SQS] Published video-uploaded event for videoId={}", video.getId());

        } catch (JsonProcessingException e) {
            log.error("[SQS] Failed to serialize video-uploaded event for videoId={}", video.getId(), e);
            throw new RuntimeException("Failed to publish video event", e);
        }
    }
}
