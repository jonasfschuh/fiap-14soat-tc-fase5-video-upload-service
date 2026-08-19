package br.com.fiap.application.adapters;

import br.com.fiap.application.dtos.VideoStatusResponse;
import br.com.fiap.application.dtos.VideoUploadResponse;
import br.com.fiap.application.mappers.VideoMapper;
import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.in.FindVideoByIdInputPort;
import br.com.fiap.domain.ports.in.FindVideosByUserInputPort;
import br.com.fiap.domain.ports.in.UploadVideoInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@Tag(name = "Videos", description = "Video upload and status management")
public class VideoController {

    private final UploadVideoInputPort uploadVideo;
    private final FindVideosByUserInputPort findVideosByUser;
    private final FindVideoByIdInputPort findVideoById;

    public VideoController(UploadVideoInputPort uploadVideo,
                           FindVideosByUserInputPort findVideosByUser,
                           FindVideoByIdInputPort findVideoById) {
        this.uploadVideo = uploadVideo;
        this.findVideosByUser = findVideosByUser;
        this.findVideoById = findVideoById;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a video", description = "Uploads a video file and queues it for processing. Returns 202 Accepted immediately.")
    public ResponseEntity<VideoUploadResponse> upload(
            @RequestHeader("X-User-Id") String userId,
            @RequestPart("video") MultipartFile file) throws IOException {

        Video video = uploadVideo.execute(
                userId,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType(),
                file.getInputStream()
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(VideoMapper.toUploadResponse(video));
    }

    @GetMapping
    @Operation(summary = "List videos",
               description = "Returns videos uploaded by the user. If X-User-Id header is provided, filters by that user; otherwise returns all videos.")
    public ResponseEntity<List<VideoStatusResponse>> listByUser(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        List<VideoStatusResponse> videos = findVideosByUser.execute(userId)
                .stream()
                .map(VideoMapper::toStatusResponse)
                .toList();

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get video by ID", description = "Returns status and metadata for a specific video.")
    public ResponseEntity<VideoStatusResponse> getById(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID id) {

        Video video = findVideoById.execute(id, userId);
        return ResponseEntity.ok(VideoMapper.toStatusResponse(video));
    }
}
