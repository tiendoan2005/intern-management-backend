package com.example.backend.controller;

import com.example.backend.dto.weeklyreport.*;
import com.example.backend.enums.WeeklyReportStatus;
import com.example.backend.security.CurrentUserProvider;
import com.example.backend.service.WeeklyReportService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports/weekly")
public class WeeklyReportMentorController {

    private final WeeklyReportService service;
    private final CurrentUserProvider currentUser;

    public WeeklyReportMentorController(WeeklyReportService service, CurrentUserProvider currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @GetMapping("/mentor")
    @PreAuthorize("hasRole('MENTOR')")
    public Page<WeeklyReportResponse> mentorList(
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) WeeklyReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.mentorList(
                currentUser.getCurrentUserId(),
                groupId,
                status,
                PageRequest.of(page, size)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MENTOR')")
    public WeeklyReportResponse mentorDetail(@PathVariable Long id) {
        return service.mentorDetail(currentUser.getCurrentUserId(), id);
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasRole('MENTOR')")
    public WeeklyReportResponse review(@PathVariable Long id,
                                       @Valid @RequestBody WeeklyReportReviewRequest req) {
        return service.review(currentUser.getCurrentUserId(), id, req);
    }
}
