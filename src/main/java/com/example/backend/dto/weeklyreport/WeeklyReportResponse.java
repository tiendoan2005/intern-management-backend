package com.example.backend.dto.weeklyreport;

import com.example.backend.enums.WeeklyReportStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WeeklyReportResponse(
        Long id,
        Long internId,
        Long groupId,
        LocalDate weekStart,
        LocalDate weekEnd,
        String title,
        String summary,
        String achievements,
        String challenges,
        String nextWeekPlan,
        String attachmentUrl,
        WeeklyReportStatus status,
        String mentorFeedback,
        Integer mentorRating,
        Long reviewedBy,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt
) {}
