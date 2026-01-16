// dto/TaskUpdateDTO.java
package com.example.backend.dto.taskupdate;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateDTO {

    private Long id;
    private Long taskId;

    private Long internId;
    private String internName;
    private String internEmail;

    private Integer progressPercent;
    private String content;
    private List<String> attachments;

    private String mentorFeedback;
    private LocalDateTime feedbackAt;
    private LocalDateTime createdAt;
}
