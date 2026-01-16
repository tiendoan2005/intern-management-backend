package com.example.backend.dto.weeklyreport;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record WeeklyReportCreateRequest(
        @NotNull LocalDate weekStart,
        @NotNull LocalDate weekEnd,
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 4000) String summary,
        @Size(max = 4000) String achievements,
        @Size(max = 4000) String challenges,
        @Size(max = 4000) String nextWeekPlan,
        @Size(max = 1000) String attachmentUrl
) {}
