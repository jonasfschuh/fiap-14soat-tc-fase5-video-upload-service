package br.com.fiap.application.unit.mappers;

import br.com.fiap.application.dtos.VideoStatusResponse;
import br.com.fiap.application.dtos.VideoUploadResponse;
import br.com.fiap.application.mappers.VideoMapper;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.model.VideoStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VideoMapperTest {

    @Test
    void toUploadResponse_shouldMapCorrectly() {
        Video video = Video.create("user-1", "video.mp4", 1024L, "video/mp4");
        VideoUploadResponse response = VideoMapper.toUploadResponse(video);

        assertThat(response.videoId()).isEqualTo(video.getId());
        assertThat(response.status()).isEqualTo(VideoStatus.PENDING.name());
        assertThat(response.originalFilename()).isEqualTo("video.mp4");
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void toStatusResponse_shouldMapAllFields() {
        Video video = Video.create("user-1", "video.mp4", 2048L, "video/mp4");
        video.setStorageKey("videos/user-1/uuid/video.mp4");
        VideoStatusResponse response = VideoMapper.toStatusResponse(video);

        assertThat(response.videoId()).isEqualTo(video.getId());
        assertThat(response.userId()).isEqualTo("user-1");
        assertThat(response.fileSizeBytes()).isEqualTo(2048L);
        assertThat(response.storageKey()).isEqualTo("videos/user-1/uuid/video.mp4");
    }
}
