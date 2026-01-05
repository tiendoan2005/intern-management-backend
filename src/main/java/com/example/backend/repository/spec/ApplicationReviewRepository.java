package com.example.backend.repository.spec;

import com.example.backend.entity.ApplicationReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationReviewRepository extends JpaRepository<ApplicationReview, Long> {
    boolean existsByApplicationId(Long applicationId);
}
