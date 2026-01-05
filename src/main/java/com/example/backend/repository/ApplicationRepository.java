package com.example.backend.repository;

import com.example.backend.entity.Application;
import com.example.backend.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByIntern_Id(Long internId);
    @Query("SELECT a FROM Application a " +
            "WHERE (:status IS NULL OR a.status = :status) " +
            "AND (:keyword IS NULL OR :keyword = '' OR LOWER(a.position) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY a.appliedAt DESC")
    Page<Application> search(@Param("status") ApplicationStatus status,
                             @Param("keyword") String keyword,
                             Pageable pageable);
}
