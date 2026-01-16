package com.example.backend.repository;

import com.example.backend.entity.InternProfile;
import com.example.backend.dto.statistics.InternCountByUniversityDTO;
import com.example.backend.dto.statistics.InternCountByMajorDTO;
import com.example.backend.dto.statistics.InternCountByUniversityMajorDTO;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InternProfileRepository
        extends JpaRepository<InternProfile, Long>, JpaSpecificationExecutor<InternProfile> {

    /* =========================
       EXISTING METHODS
       ========================= */

    @Override
    @EntityGraph(attributePaths = {"user"})
    Page<InternProfile> findAll(Specification<InternProfile> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"user"})
    Optional<InternProfile> findById(Long id);

    @EntityGraph(attributePaths = {"user"})
    Optional<InternProfile> findByUser_Id(Long userId);

    @EntityGraph(attributePaths = {"user"})
    Optional<InternProfile> findByUser_Email(String email);

    @Query("select distinct ip.university from InternProfile ip where ip.university is not null")
    List<String> findAllUniversities();

    @Query("select distinct ip.major from InternProfile ip where ip.major is not null")
    List<String> findAllMajors();

    /* =========================
       WEEKLY REPORT SUPPORT (NEW)
       Dùng cho submit weekly report: query nhẹ hơn, không kéo user
       ========================= */

    /**
     * Query nhẹ hơn (không EntityGraph) để tránh join bảng user khi không cần.
     */
    @Query("select ip from InternProfile ip where ip.user.id = :userId")
    Optional<InternProfile> findByUserIdLight(@Param("userId") Long userId);

    /**
     * Cực nhẹ: chỉ lấy internProfileId từ userId.
     * Dùng tốt khi chỉ cần set relation InternProfile cho WeeklyReport.
     */
    @Query("select ip.id from InternProfile ip where ip.user.id = :userId")
    Optional<Long> getIdByUserId(@Param("userId") Long userId);

    /* =========================
       STATISTICS SECTION (NEW)
       User Story:
       HR xem số lượng thực tập sinh theo trường/ngành
       ========================= */

    @Query("""
        SELECT new com.example.backend.dto.statistics.InternCountByUniversityDTO(
            CASE
                WHEN ip.university IS NULL OR TRIM(ip.university) = '' THEN 'UNKNOWN'
                ELSE ip.university
            END,
            COUNT(ip.id)
        )
        FROM InternProfile ip
        WHERE (:from IS NULL OR ip.startDate >= :from)
          AND (:to IS NULL OR ip.endDate <= :to)
          AND (
                :keyword IS NULL
                OR LOWER(
                    CASE
                        WHEN ip.university IS NULL OR TRIM(ip.university) = '' THEN 'UNKNOWN'
                        ELSE ip.university
                    END
                ) LIKE CONCAT('%', LOWER(:keyword), '%')
          )
        GROUP BY
            CASE
                WHEN ip.university IS NULL OR TRIM(ip.university) = '' THEN 'UNKNOWN'
                ELSE ip.university
            END
        ORDER BY COUNT(ip.id) DESC
    """)
    List<InternCountByUniversityDTO> countInternsByUniversity(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("keyword") String keyword
    );

    @Query("""
        SELECT new com.example.backend.dto.statistics.InternCountByMajorDTO(
            CASE
                WHEN ip.major IS NULL OR TRIM(ip.major) = '' THEN 'UNKNOWN'
                ELSE ip.major
            END,
            COUNT(ip.id)
        )
        FROM InternProfile ip
        WHERE (:from IS NULL OR ip.startDate >= :from)
          AND (:to IS NULL OR ip.endDate <= :to)
          AND (
                :keyword IS NULL
                OR LOWER(
                    CASE
                        WHEN ip.major IS NULL OR TRIM(ip.major) = '' THEN 'UNKNOWN'
                        ELSE ip.major
                    END
                ) LIKE CONCAT('%', LOWER(:keyword), '%')
          )
        GROUP BY
            CASE
                WHEN ip.major IS NULL OR TRIM(ip.major) = '' THEN 'UNKNOWN'
                ELSE ip.major
            END
        ORDER BY COUNT(ip.id) DESC
    """)
    List<InternCountByMajorDTO> countInternsByMajor(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("keyword") String keyword
    );

    @Query("""
        SELECT new com.example.backend.dto.statistics.InternCountByUniversityMajorDTO(
            CASE
                WHEN ip.university IS NULL OR TRIM(ip.university) = '' THEN 'UNKNOWN'
                ELSE ip.university
            END,
            CASE
                WHEN ip.major IS NULL OR TRIM(ip.major) = '' THEN 'UNKNOWN'
                ELSE ip.major
            END,
            COUNT(ip.id)
        )
        FROM InternProfile ip
        WHERE (:from IS NULL OR ip.startDate >= :from)
          AND (:to IS NULL OR ip.endDate <= :to)
          AND (
                :keyword IS NULL
                OR LOWER(
                    CASE
                        WHEN ip.university IS NULL OR TRIM(ip.university) = '' THEN 'UNKNOWN'
                        ELSE ip.university
                    END
                ) LIKE CONCAT('%', LOWER(:keyword), '%')
                OR LOWER(
                    CASE
                        WHEN ip.major IS NULL OR TRIM(ip.major) = '' THEN 'UNKNOWN'
                        ELSE ip.major
                    END
                ) LIKE CONCAT('%', LOWER(:keyword), '%')
          )
        GROUP BY
            CASE
                WHEN ip.university IS NULL OR TRIM(ip.university) = '' THEN 'UNKNOWN'
                ELSE ip.university
            END,
            CASE
                WHEN ip.major IS NULL OR TRIM(ip.major) = '' THEN 'UNKNOWN'
                ELSE ip.major
            END
        ORDER BY COUNT(ip.id) DESC
    """)
    List<InternCountByUniversityMajorDTO> countInternsByUniversityMajor(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("keyword") String keyword
    );
}
