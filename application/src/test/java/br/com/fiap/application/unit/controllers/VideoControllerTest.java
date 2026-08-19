package br.com.fiap.application.unit.controllers;

import br.com.fiap.application.adapters.VideoController;
import br.com.fiap.application.adviser.GlobalExceptionHandler;
import br.com.fiap.domain.exceptions.VideoNotFoundException;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.in.FindVideoByIdInputPort;
import br.com.fiap.domain.ports.in.FindVideosByUserInputPort;
import br.com.fiap.domain.ports.in.UploadVideoInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {

    @Mock private UploadVideoInputPort uploadVideo;
    @Mock private FindVideosByUserInputPort findVideosByUser;
    @Mock private FindVideoByIdInputPort findVideoById;

    @Test
    void shouldReturn202OnUpload() throws Exception {
        VideoController controller = new VideoController(uploadVideo, findVideosByUser, findVideoById);
        Video video = Video.create("user-1", "test.mp4", 1024L, "video/mp4");
        when(uploadVideo.execute(anyString(), anyString(), any(), anyString(), any())).thenReturn(video);

        MockMultipartFile file = new MockMultipartFile("video", "test.mp4", "video/mp4", "content".getBytes());
        ResponseEntity<?> response = controller.upload("user-1", file);

        assertThat(response.getStatusCode().value()).isEqualTo(202);
    }

    @Test
    void shouldReturn200OnListByUser() {
        VideoController controller = new VideoController(uploadVideo, findVideosByUser, findVideoById);
        Video video = Video.create("user-1", "test.mp4", 1024L, "video/mp4");
        when(findVideosByUser.execute("user-1")).thenReturn(List.of(video));

        ResponseEntity<?> response = controller.listByUser("user-1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void shouldReturn200WithAllVideosWhenUserIdIsNull() {
        VideoController controller = new VideoController(uploadVideo, findVideosByUser, findVideoById);
        Video video = Video.create("user-1", "test.mp4", 1024L, "video/mp4");
        when(findVideosByUser.execute(null)).thenReturn(List.of(video));

        ResponseEntity<?> response = controller.listByUser(null);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }
}
