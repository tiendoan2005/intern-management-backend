package com.example.backend.dto.taskupdate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTaskUpdateRequest {
    @NotNull
    @Min(0)
    @Max(100)
    private Integer progressPercent;

    @Size(max = 4000)
    private String content;
}
