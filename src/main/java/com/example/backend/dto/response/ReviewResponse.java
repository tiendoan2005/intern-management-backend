package com.example.backend.dto.response;

import com.example.backend.enums.ReviewDecision;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long reviewerId,
        ReviewDecision decision,
        String comment,
        LocalDateTime decidedAt
) {
}
