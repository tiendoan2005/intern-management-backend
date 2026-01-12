package com.example.backend.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InternCountByUniversityDTO {
    private String university;
    private Long totalInterns;
}
