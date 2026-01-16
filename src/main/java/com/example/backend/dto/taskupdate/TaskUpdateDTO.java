package com.example.backend.dto.taskupdate;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskUpdateDTO {
    private Long id;
    private Long taskId;

    private Long internId;
    private String internName;

    private Integer progressPercent;
    private String content;

    private LocalDateTime createdAt;
}
