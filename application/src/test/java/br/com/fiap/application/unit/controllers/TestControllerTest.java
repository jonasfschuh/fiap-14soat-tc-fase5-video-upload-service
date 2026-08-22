package br.com.fiap.application.unit.controllers;

import br.com.fiap.application.adapters.TestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("TestController - Unit Tests")
class TestControllerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private TestController controller;

    @BeforeEach
    void setUp() {
        controller = new TestController(rabbitTemplate, new ObjectMapper());
    }

    @Test
    @DisplayName("publishTestMessage publishes to RabbitMQ and returns 200 with payload")
    void publishTestMessage_success_returns200() {
        ResponseEntity<Map<String, Object>> response = controller.publishTestMessage("user-test", "test-video.mp4");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("status");
        assertThat(response.getBody()).containsEntry("status", "published");
        assertThat(response.getBody()).containsKey("payload");
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("publishTestMessage uses custom userId and filename in payload")
    void publishTestMessage_withCustomParams_includesThemInPayload() {
        ResponseEntity<Map<String, Object>> response = controller.publishTestMessage("user-123", "my-video.mp4");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) response.getBody().get("payload");
        assertThat(payload).containsEntry("userId", "user-123");
        assertThat(payload).containsEntry("originalFilename", "my-video.mp4");
    }

    @Test
    @DisplayName("publishTestMessage throws RuntimeException when RabbitMQ fails")
    void publishTestMessage_whenRabbitFails_throwsRuntimeException() {
        doThrow(new RuntimeException("broker down"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());

        assertThatThrownBy(() -> controller.publishTestMessage("user-test", "test.mp4"))
                .isInstanceOf(RuntimeException.class);
    }
}
