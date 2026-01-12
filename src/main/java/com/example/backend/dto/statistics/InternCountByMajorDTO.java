package com.example.backend.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InternCountByMajorDTO {
    private String major;
    private Long totalInterns;
}
