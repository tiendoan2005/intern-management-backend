package com.example.backend.dto.taskupdate;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CreateTaskUpdateRequest {

    @NotNull(message = "Progress percent is required")
    @Min(value = 0)
    @Max(value = 100)
    private Integer progressPercent;

    @NotBlank(message = "Content is required")
    @Size(max = 4000)
    private String content;

    // optional – URL file đính kèm
    private List<String> attachments;
}
