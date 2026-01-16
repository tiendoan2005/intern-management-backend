package com.example.backend.controller;

import com.example.backend.dto.statistics.InternCountByMajorDTO;
import com.example.backend.dto.statistics.InternCountByUniversityDTO;
import com.example.backend.dto.statistics.InternCountByUniversityMajorDTO;
import com.example.backend.dto.statistics.ProgramCompletionDTO;
import com.example.backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics/interns")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/university")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public List<InternCountByUniversityDTO> byUniversity(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String keyword
    ) {
        return statisticsService.internsByUniversity(from, to, keyword);
    }

    @GetMapping("/major")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public List<InternCountByMajorDTO> byMajor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String keyword
    ) {
        return statisticsService.internsByMajor(from, to, keyword);
    }

    @GetMapping("/university-major")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public List<InternCountByUniversityMajorDTO> byUniversityMajor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String keyword
    ) {
        return statisticsService.internsByUniversityMajor(from, to, keyword);
    }

    // =========================================================
    // ✅ NEW: Program Completion Rate
    // GET /api/statistics/interns/program-completion?programId=1
    // programId optional
    // =========================================================
    @GetMapping("/program-completion")
    @PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
    public List<ProgramCompletionDTO> programCompletion(
            @RequestParam(required = false) Long programId
    ) {
        return statisticsService.programCompletion(programId);
    }
}
