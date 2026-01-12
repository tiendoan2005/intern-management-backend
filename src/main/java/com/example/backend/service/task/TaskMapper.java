package com.example.backend.service.task;

import com.example.backend.dto.task.TaskDTO;
import com.example.backend.entity.Task;

public class TaskMapper {
    public static TaskDTO toDTO(Task t) {
        return new TaskDTO(
                t.getId(),
                t.getGroup().getId(),
                t.getGroup().getName(),
                t.getTitle(),
                t.getDescription(),
                t.getDueDate(),
                t.getStatus(),
                t.getCreatedBy() != null ? t.getCreatedBy().getId() : null,
                t.getCreatedBy() != null ? t.getCreatedBy().getFullName() : null,
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
