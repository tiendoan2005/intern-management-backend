package com.example.backend.service;

import com.example.backend.dto.weeklyreport.*;
import com.example.backend.entity.*;
import com.example.backend.enums.WeeklyReportStatus;
import com.example.backend.mapper.WeeklyReportMapper;
import com.example.backend.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeeklyReportService {

    private final WeeklyReportRepository reportRepo;
    private final InternProfileRepository internRepo;
    private final MentorRepository mentorRepo;
    private final GroupMemberRepository groupMemberRepo;
    private final UserRepository userRepo; // nếu bạn đã có sẵn
    private final ProgramGroupRepository programGroupRepo;

    public WeeklyReportService(WeeklyReportRepository reportRepo,
                               InternProfileRepository internRepo,
                               MentorRepository mentorRepo,
                               GroupMemberRepository groupMemberRepo,
                               UserRepository userRepo, ProgramGroupRepository programGroupRepo) {
        this.reportRepo = reportRepo;
        this.internRepo = internRepo;
        this.mentorRepo = mentorRepo;
        this.groupMemberRepo = groupMemberRepo;
        this.userRepo = userRepo;
        this.programGroupRepo = programGroupRepo;
    }

    @Transactional
    public WeeklyReportResponse submit(Long currentUserId, WeeklyReportCreateRequest req) {
        if (req.weekStart().isAfter(req.weekEnd())) {
            throw new IllegalArgumentException("weekStart must be <= weekEnd");
        }

        InternProfile intern = internRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new IllegalStateException("Intern profile not found"));

        if (reportRepo.existsByIntern_IdAndWeekStart(intern.getId(), req.weekStart())) {
            // bạn có thể map sang 409 Conflict trong GlobalExceptionHandler
            throw new IllegalStateException("Weekly report already submitted for this week");
        }

        WeeklyReport wr = new WeeklyReport();
        wr.setIntern(intern);

        // Auto set group theo membership hiện tại (nếu có)
        groupMemberRepo.findActiveGroupIdOfIntern(intern.getId()).ifPresent(groupId -> {
            ProgramGroup g = new ProgramGroup();
            g.setId(groupId); // set proxy by id (không cần fetch full)
            wr.setGroup(g);
        });

        wr.setWeekStart(req.weekStart());
        wr.setWeekEnd(req.weekEnd());
        wr.setTitle(req.title());
        wr.setSummary(req.summary());
        wr.setAchievements(req.achievements());
        wr.setChallenges(req.challenges());
        wr.setNextWeekPlan(req.nextWeekPlan());
        wr.setAttachmentUrl(req.attachmentUrl());
        wr.setStatus(WeeklyReportStatus.SUBMITTED);

        return WeeklyReportMapper.toResponse(reportRepo.save(wr));
    }

    @Transactional(readOnly = true)
    public Page<WeeklyReportResponse> myList(Long currentUserId,
                                             WeeklyReportStatus status,
                                             LocalDate from,
                                             LocalDate to,
                                             Pageable pageable) {
        InternProfile intern = internRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new IllegalStateException("Intern profile not found"));

        return reportRepo.searchMyReports(intern.getId(), status, from, to, pageable)
                .map(WeeklyReportMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public WeeklyReportResponse myDetail(Long currentUserId, Long reportId) {
        InternProfile intern = internRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new IllegalStateException("Intern profile not found"));

        WeeklyReport wr = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        if (!wr.getIntern().getId().equals(intern.getId())) {
            throw new SecurityException("Forbidden");
        }
        return WeeklyReportMapper.toResponse(wr);
    }

    @Transactional(readOnly = true)
    public Page<WeeklyReportResponse> mentorList(Long mentorUserId,
                                                 Long groupId,
                                                 WeeklyReportStatus status,
                                                 Pageable pageable) {
        Mentor mentor = mentorRepo.findByUser_Id(mentorUserId)
                .orElseThrow(() -> new IllegalStateException("Mentor profile not found"));

        List<Long> groupIds = programGroupRepo.findGroupIdsByMentorId(mentor.getId());
        if (groupId != null) {
            if (!groupIds.contains(groupId)) throw new SecurityException("Forbidden");
            groupIds = List.of(groupId);
        }

        if (groupIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return reportRepo.searchMentorReports(groupIds, status, pageable)
                .map(WeeklyReportMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public WeeklyReportResponse mentorDetail(Long mentorUserId, Long reportId) {
        Mentor mentor = mentorRepo.findByUser_Id(mentorUserId)
                .orElseThrow(() -> new IllegalStateException("Mentor profile not found"));

        WeeklyReport wr = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        Long groupId = wr.getGroup() != null ? wr.getGroup().getId() : null;
        if (groupId == null) throw new SecurityException("Forbidden");

        List<Long> groupIds = programGroupRepo.findGroupIdsByMentorId(mentor.getId());
        if (!groupIds.contains(groupId)) throw new SecurityException("Forbidden");

        return WeeklyReportMapper.toResponse(wr);
    }

    @Transactional
    public WeeklyReportResponse review(Long mentorUserId, Long reportId, WeeklyReportReviewRequest req) {
        Mentor mentor = mentorRepo.findByUser_Id(mentorUserId)
                .orElseThrow(() -> new IllegalStateException("Mentor profile not found"));

        WeeklyReport wr = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        Long groupId = wr.getGroup() != null ? wr.getGroup().getId() : null;
        if (groupId == null) throw new SecurityException("Forbidden");

        List<Long> groupIds = programGroupRepo.findGroupIdsByMentorId(mentor.getId());
        if (!groupIds.contains(groupId)) throw new SecurityException("Forbidden");

        User mentorUser = userRepo.findById(mentorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor user not found"));

        wr.setMentorFeedback(req.mentorFeedback());
        wr.setMentorRating(req.mentorRating());
        wr.setStatus(req.status());
        wr.setReviewedBy(mentorUser);
        wr.setReviewedAt(LocalDateTime.now());

        return WeeklyReportMapper.toResponse(reportRepo.save(wr));
    }
}
