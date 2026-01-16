package com.example.backend.mapper;

import com.example.backend.dto.weeklyreport.WeeklyReportResponse;
import com.example.backend.entity.WeeklyReport;

public final class WeeklyReportMapper {
    private WeeklyReportMapper() {}

    public static WeeklyReportResponse toResponse(WeeklyReport wr) {
        return new WeeklyReportResponse(
                wr.getId(),
                wr.getIntern().getId(),
                wr.getGroup() != null ? wr.getGroup().getId() : null,
                wr.getWeekStart(),
                wr.getWeekEnd(),
                wr.getTitle(),
                wr.getSummary(),
                wr.getAchievements(),
                wr.getChallenges(),
                wr.getNextWeekPlan(),
                wr.getAttachmentUrl(),
                wr.getStatus(),
                wr.getMentorFeedback(),
                wr.getMentorRating(),
                wr.getReviewedBy() != null ? wr.getReviewedBy().getId() : null,
                wr.getReviewedAt(),
                wr.getCreatedAt()
        );
    }
}
