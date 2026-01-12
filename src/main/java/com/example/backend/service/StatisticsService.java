package com.example.backend.service;

import com.example.backend.dto.statistics.InternCountByMajorDTO;
import com.example.backend.dto.statistics.InternCountByUniversityDTO;
import com.example.backend.dto.statistics.InternCountByUniversityMajorDTO;
import com.example.backend.repository.InternProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final InternProfileRepository internProfileRepository;

    public List<InternCountByUniversityDTO> internsByUniversity(LocalDate from, LocalDate to, String keyword) {
        validateRange(from, to);
        return internProfileRepository.countInternsByUniversity(from, to, normalizeKeyword(keyword));
    }

    public List<InternCountByMajorDTO> internsByMajor(LocalDate from, LocalDate to, String keyword) {
        validateRange(from, to);
        return internProfileRepository.countInternsByMajor(from, to, normalizeKeyword(keyword));
    }

    public List<InternCountByUniversityMajorDTO> internsByUniversityMajor(LocalDate from, LocalDate to, String keyword) {
        validateRange(from, to);
        return internProfileRepository.countInternsByUniversityMajor(from, to, normalizeKeyword(keyword));
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("Invalid date range: 'from' must be <= 'to'");
        }
    }

    private String normalizeKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;
        return keyword.trim();
    }
}
