package br.com.fiap.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record VideoUploadResponse(
        UUID videoId,
        String status,
        String originalFilename,
        LocalDateTime createdAt
) {}
