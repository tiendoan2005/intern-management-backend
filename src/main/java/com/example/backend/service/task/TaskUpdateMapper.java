package com.example.backend.service.task;

import com.example.backend.dto.task.TaskUpdateDTO;
import com.example.backend.entity.TaskUpdate;

public class TaskUpdateMapper {
    public static TaskUpdateDTO toDTO(TaskUpdate u) {
        return new TaskUpdateDTO(
                u.getId(),
                u.getTask().getId(),
                u.getIntern().getId(),
                u.getIntern().getUser().getFullName(),
                u.getProgressPercent(),
                u.getContent(),
                u.getCreatedAt()
        );
    }
}
