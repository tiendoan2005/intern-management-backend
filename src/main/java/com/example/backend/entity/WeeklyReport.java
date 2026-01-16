package com.example.backend.entity;

import com.example.backend.enums.WeeklyReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "weekly_reports",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_weekly_reports_intern_week",
                columnNames = {"intern_id", "week_start"}
        )
)
public class WeeklyReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "intern_id", nullable = false)
    private InternProfile intern;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private ProgramGroup group;

    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @Column(name = "week_end", nullable = false)
    private LocalDate weekEnd;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String summary;


    @Lob
    @Column(columnDefinition = "TEXT")
    private String achievements;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String challenges;

    @Lob
    @Column(name = "next_week_plan", columnDefinition = "TEXT")
    private String nextWeekPlan;

    @Column(name = "attachment_url", length = 1000)
    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WeeklyReportStatus status = WeeklyReportStatus.SUBMITTED;

    // Review fields
    @Lob
    @Column(name = "mentor_feedback", columnDefinition = "TEXT")
    private String mentorFeedback;

    @Column(name = "mentor_rating")
    private Integer mentorRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}
