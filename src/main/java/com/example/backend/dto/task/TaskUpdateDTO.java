package com.example.backend.dto.task;

import java.time.LocalDateTime;

public record TaskUpdateDTO(
        Long id,
        Long taskId,
        Long internId,
        String internName,
        Integer progressPercent,
        String content,
        LocalDateTime createdAt
) {}