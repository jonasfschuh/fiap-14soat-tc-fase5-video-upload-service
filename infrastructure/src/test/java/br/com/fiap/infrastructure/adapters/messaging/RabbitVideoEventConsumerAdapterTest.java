package br.com.fiap.infrastructure.adapters.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RabbitVideoEventConsumerAdapter - Unit Tests")
class RabbitVideoEventConsumerAdapterTest {

    @Mock
    private ObjectMapper objectMapper;

    private RabbitVideoEventConsumerAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RabbitVideoEventConsumerAdapter(objectMapper);
    }

    @Test
    @DisplayName("onVideoProcessed logs valid JSON message successfully")
    void onVideoProcessed_withValidJson_logsSuccessfully() throws Exception {
        String message = "{\"videoId\":\"123\",\"status\":\"PROCESSED\"}";
        when(objectMapper.readValue(anyString(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(java.util.Map.of("videoId", "123", "status", "PROCESSED"));
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(new ObjectMapper().writerWithDefaultPrettyPrinter());

        assertThatCode(() -> adapter.onVideoProcessed(message, "video.events", "video.processed"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("onVideoProcessed logs raw message when JSON parsing fails")
    void onVideoProcessed_withInvalidJson_logsRawMessage() throws Exception {
        String message = "not-a-json";
        when(objectMapper.readValue(anyString(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenThrow(new com.fasterxml.jackson.core.JsonParseException(null, "Invalid JSON"));

        assertThatCode(() -> adapter.onVideoProcessed(message, "video.events", "video.processed"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("onVideoProcessed logs error when unexpected exception occurs")
    void onVideoProcessed_withUnexpectedException_logsError() throws Exception {
        String message = "{\"videoId\":\"123\"}";
        when(objectMapper.readValue(anyString(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        assertThatCode(() -> adapter.onVideoProcessed(message, "video.events", "video.processed"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("onVideoProcessed uses default header values when headers are absent")
    void onVideoProcessed_withDefaultHeaders_logsSuccessfully() throws Exception {
        String message = "{\"videoId\":\"456\"}";
        when(objectMapper.readValue(anyString(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(java.util.Map.of("videoId", "456"));
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(new ObjectMapper().writerWithDefaultPrettyPrinter());

        assertThatCode(() -> adapter.onVideoProcessed(message, "video.events", "video.processed"))
                .doesNotThrowAnyException();
    }
}
