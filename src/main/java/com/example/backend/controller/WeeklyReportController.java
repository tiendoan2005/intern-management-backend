package com.example.backend.controller;

import com.example.backend.dto.weeklyreport.*;
import com.example.backend.enums.WeeklyReportStatus;
import com.example.backend.security.CurrentUserProvider;
import com.example.backend.service.WeeklyReportService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports/weekly")
public class WeeklyReportController {

    private final WeeklyReportService service;
    private final CurrentUserProvider currentUser;

    public WeeklyReportController(WeeklyReportService service, CurrentUserProvider currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @PostMapping
    @PreAuthorize("hasRole('INTERN')")
    public WeeklyReportResponse submit(@Valid @RequestBody WeeklyReportCreateRequest req) {
        return service.submit(currentUser.getCurrentUserId(), req);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('INTERN')")
    public Page<WeeklyReportResponse> myList(
            @RequestParam(required = false) WeeklyReportStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.myList(
                currentUser.getCurrentUserId(),
                status, from, to,
                PageRequest.of(page, size)
        );
    }

    @GetMapping("/my/{id}")
    @PreAuthorize("hasRole('INTERN')")
    public WeeklyReportResponse myDetail(@PathVariable Long id) {
        return service.myDetail(currentUser.getCurrentUserId(), id);
    }
}
