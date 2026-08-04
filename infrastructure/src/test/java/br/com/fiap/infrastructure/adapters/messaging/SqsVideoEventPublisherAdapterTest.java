package br.com.fiap.infrastructure.adapters.messaging;

import br.com.fiap.domain.model.Video;
import br.com.fiap.infrastructure.logging.SqsMessageLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SqsVideoEventPublisherAdapter - Unit Tests")
class SqsVideoEventPublisherAdapterTest {

    @Mock private SqsClient sqsClient;
    @Mock private SqsMessageLogger sqsMessageLogger;

    private static final String QUEUE_URL = "http://localhost:4566/000000000000/video-uploaded";
    private SqsVideoEventPublisherAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SqsVideoEventPublisherAdapter(sqsClient, new ObjectMapper(), sqsMessageLogger, QUEUE_URL);
    }

    @Test
    @DisplayName("publishVideoUploaded sends message to SQS and logs it")
    void publishVideoUploaded_sendsMessageToSqs() {
        Video video = Video.create("user-1", "test.mp4", 1024L, "video/mp4");
        video.setStorageKey("videos/user-1/uuid/test.mp4");
        when(sqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(SendMessageResponse.builder().build());

        adapter.publishVideoUploaded(video);

        verify(sqsClient).sendMessage(any(SendMessageRequest.class));
        verify(sqsMessageLogger).logMessageSent(eq(QUEUE_URL), anyString(), eq("VIDEO_UPLOADED"), isNull());
    }

    @Test
    @DisplayName("publishVideoUploaded re-throws when SQS client fails")
    void publishVideoUploaded_whenSqsThrows_propagatesException() {
        Video video = Video.create("user-1", "test.mp4", 1024L, "video/mp4");
        video.setStorageKey("videos/user-1/uuid/test.mp4");
        doThrow(new RuntimeException("SQS unavailable")).when(sqsClient).sendMessage(any(SendMessageRequest.class));

        assertThatThrownBy(() -> adapter.publishVideoUploaded(video))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("SQS unavailable");
    }
}
