package com.example.backend.repository;

import com.example.backend.entity.WeeklyReport;
import com.example.backend.enums.WeeklyReportStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;

public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {

    boolean existsByIntern_IdAndWeekStart(Long internId, LocalDate weekStart);

    @Query("""
        select wr from WeeklyReport wr
        where wr.intern.id = :internId
          and (:status is null or wr.status = :status)
          and (:from is null or wr.weekStart >= :from)
          and (:to is null or wr.weekEnd <= :to)
        order by wr.weekStart desc
    """)
    Page<WeeklyReport> searchMyReports(@Param("internId") Long internId,
                                       @Param("status") WeeklyReportStatus status,
                                       @Param("from") LocalDate from,
                                       @Param("to") LocalDate to,
                                       Pageable pageable);

    @Query("""
        select wr from WeeklyReport wr
        where wr.group.id in :groupIds
          and (:status is null or wr.status = :status)
        order by wr.weekStart desc
    """)
    Page<WeeklyReport> searchMentorReports(@Param("groupIds") Collection<Long> groupIds,
                                           @Param("status") WeeklyReportStatus status,
                                           Pageable pageable);
}
