package com.example.backend.dto.response;

import java.time.LocalDateTime;

public record InternDocumentResponse(
        Long id,
        Long internId,
        com.example.backend.enums.DocumentType type,
        String fileUrl,
        com.example.backend.enums.DocumentStatus status,
        LocalDateTime uploadedAt,
        Long reviewedById,
        LocalDateTime reviewedAt,
        String reviewNote
) {
}
