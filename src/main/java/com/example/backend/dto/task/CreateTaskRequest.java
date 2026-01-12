package com.example.backend.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateTaskRequest (
        @NotNull Long groupId,
        @NotBlank @Size(max = 255) String title,
        @Size(max = 4000) String description,
        LocalDateTime dueDate
){
}
