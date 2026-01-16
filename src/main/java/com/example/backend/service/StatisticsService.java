package com.example.backend.service;

import com.example.backend.dto.statistics.InternCountByMajorDTO;
import com.example.backend.dto.statistics.InternCountByUniversityDTO;
import com.example.backend.dto.statistics.InternCountByUniversityMajorDTO;
import com.example.backend.dto.statistics.ProgramCompletionDTO;
import com.example.backend.dto.statistics.ProgramCompletionRaw;
import com.example.backend.repository.InternProfileRepository;
import com.example.backend.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final InternProfileRepository internProfileRepository;

    // ✅ thêm repo thống kê completion rate
    private final StatisticsRepository statisticsRepository;

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

    // =========================================================
    // ✅ NEW: Program Completion Rate
    // Rule: Completed = có Evaluation(period='FINAL' && score != null)
    // =========================================================
    public List<ProgramCompletionDTO> programCompletion(Long programId) {
        List<ProgramCompletionRaw> raws = statisticsRepository.getProgramCompletion(programId);

        return raws.stream().map(r -> {
            long total = r.getTotalInterns() == null ? 0 : r.getTotalInterns();
            long completed = r.getCompletedInterns() == null ? 0 : r.getCompletedInterns();
            double rate = total == 0 ? 0.0 : (completed * 100.0 / total);
            double rounded = Math.round(rate * 100.0) / 100.0;

            return ProgramCompletionDTO.builder()
                    .programId(r.getProgramId())
                    .programName(r.getProgramName())
                    .totalInterns(total)
                    .completedInterns(completed)
                    .completionRate(rounded)
                    .build();
        }).toList();
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
