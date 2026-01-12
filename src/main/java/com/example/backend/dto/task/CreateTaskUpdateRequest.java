// dto/request/CreateTaskUpdateRequest.java
package com.example.backend.dto.task;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskUpdateRequest {

    @NotNull
    @Min(0)
    @Max(100)
    private Integer progressPercent;

    @NotBlank
    @Size(max = 4000)
    private String content;

    private List<String> attachments;
}
