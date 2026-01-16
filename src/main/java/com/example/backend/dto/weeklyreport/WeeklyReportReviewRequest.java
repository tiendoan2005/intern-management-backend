package com.example.backend.dto.weeklyreport;

import com.example.backend.enums.WeeklyReportStatus;
import jakarta.validation.constraints.*;

public record WeeklyReportReviewRequest(
        @NotBlank @Size(max = 4000) String mentorFeedback,
        @Min(1) @Max(5) Integer mentorRating,
        @NotNull WeeklyReportStatus status
) {}
