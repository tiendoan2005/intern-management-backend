package com.example.backend.repository;

import com.example.backend.dto.statistics.ProgramCompletionRaw;
import com.example.backend.entity.Program;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatisticsRepository extends JpaRepository<Program, Long> {
    @Query("""
        SELECT
          p.id as programId,
          p.name as programName,
          COUNT(DISTINCT gm.intern.id) as totalInterns,
          COUNT(DISTINCT e.intern.id) as completedInterns
        FROM Program p
          LEFT JOIN ProgramGroup pg ON pg.program = p
          LEFT JOIN GroupMember gm ON gm.group = pg
          LEFT JOIN Evaluation e ON e.intern = gm.intern
                               AND e.period = 'FINAL'
                               AND e.score IS NOT NULL
        WHERE (:programId IS NULL OR p.id = :programId)
        GROUP BY p.id, p.name
        ORDER BY p.id DESC
    """)
    List<ProgramCompletionRaw> getProgramCompletion(@Param("programId") Long programId);
}
