package com.example.backend.dto.statistics;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramCompletionDTO {
    private Long programId;
    private String programName;

    private long totalInterns;
    private long completedInterns;

    private double completionRate; // 0..100
}
