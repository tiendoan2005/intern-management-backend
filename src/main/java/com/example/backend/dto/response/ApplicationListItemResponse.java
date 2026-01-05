package com.example.backend.dto.response;

import com.example.backend.enums.ApplicationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ApplicationListItemResponse(
        Long id,
        Long internId,
        String position,
        LocalDateTime appliedAt,
        ApplicationStatus status
) {
}
