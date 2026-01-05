package com.example.backend.dto.response;

import com.example.backend.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private Long id;
    private Long internId;
    private String position;
    private LocalDateTime appliedAt;
    private ApplicationStatus status;
    private String note;
    List<ReviewResponse> reviews;

}
