package com.example.backend.service;

import com.example.backend.dto.response.ApplicationListItemResponse;
import com.example.backend.dto.response.ApplicationResponse;
import com.example.backend.dto.response.ReviewResponse;
import com.example.backend.entity.Application;
import com.example.backend.enums.ApplicationStatus;
import com.example.backend.repository.ApplicationRepository;
import com.example.backend.repository.spec.ApplicationReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationQueryService {

    private final ApplicationRepository applicationRepo;
    public final ApplicationReviewRepository reviewRepo;

    public ApplicationQueryService(ApplicationRepository applicationRepo, ApplicationReviewRepository reviewRepo) {
        this.applicationRepo = applicationRepo;
        this.reviewRepo = reviewRepo;
    }

    public Page<ApplicationListItemResponse> list(ApplicationStatus status, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return applicationRepo.search(status, keyword, pageable)
                .map(a -> new ApplicationListItemResponse(a.getId(), a.getIntern().getId(), a.getPosition(), a.getAppliedAt(), a.getStatus()));
    }

    public Application getOrThrow(Long id){
        return applicationRepo.findById(id)
                .orElseThrow(() -> new com.example.backend.exception.NotFoundException("Application not found: " + id));
    }

    public ApplicationResponse detail(Long id, List<ReviewResponse> reviews) {
        Application a = getOrThrow(id);
        return new ApplicationResponse(
                a.getId(),
        a.getIntern().getId(),
        a.getPosition(),
        a.getAppliedAt(),
        a.getStatus(),
        a.getNote(),
        reviews
        );
    }
}
