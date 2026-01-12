package com.example.backend.dto.task;

import com.example.backend.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTaskStatusRequest (
        @NotNull TaskStatus status,
        @Size(max = 500) String note
) {
}
