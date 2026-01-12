package com.example.backend.dto.task;

import com.example.backend.enums.TaskStatus;
import java.time.LocalDateTime;

public record TaskDTO(
        Long id,
        Long groupId,
        String groupName,
        String title,
        String description,
        LocalDateTime dueDate,
        TaskStatus status,
        Long createdById,
        String createdByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
