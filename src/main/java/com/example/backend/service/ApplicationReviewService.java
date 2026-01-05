package com.example.backend.service;

import com.example.backend.dto.request.ReviewRequest;
import com.example.backend.dto.response.ReviewResponse;
import com.example.backend.entity.Application;
import com.example.backend.entity.ApplicationReview;
import com.example.backend.entity.User;
import com.example.backend.enums.ApplicationStatus;
import com.example.backend.enums.ReviewDecision;
import com.example.backend.exception.BadRequestException;
import com.example.backend.repository.ApplicationRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.spec.ApplicationReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationReviewService {

    private final ApplicationRepository applicationRepo;
    private final ApplicationReviewRepository reviewRepo;
    private final UserRepository userRepo;
    public ApplicationReviewService(ApplicationRepository applicationRepo, ApplicationReviewRepository reviewRepo, UserRepository userRepo) {
        this.applicationRepo = applicationRepo;
        this.reviewRepo = reviewRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public ReviewResponse review(Long applicationId, Long reviewerId, ReviewRequest req) {
        Application app = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new com.example.backend.exception.NotFoundException("Application not found: " + applicationId));

        // Rule: chỉ review khi SUBMITTED
        if (app.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new BadRequestException("Only SUBMITTED applications can be reviewed. Current status=" + app.getStatus());
        }

        // Rule: mỗi application chỉ có 1 quyết định cuối (tuỳ yêu cầu có thể bỏ)
        if (reviewRepo.existsByApplicationId(applicationId)) {
            throw new BadRequestException("Application already reviewed.");
        }

        User reviewer = userRepo.findById(reviewerId)
                .orElseThrow(() ->
                        new com.example.backend.exception.NotFoundException(
                                "Reviewer not found: " + reviewerId
                        )
                );


        ApplicationReview review = new ApplicationReview();
        review.setApplication(app);
        review.setReviewer(reviewer);
        review.setDecision(req.decision());
        review.setComment(req.comment());
        review.setDecidedAt(LocalDateTime.now());

        review = reviewRepo.save(review);

        if (req.decision() == ReviewDecision.APPROVE) {
            app.setStatus(ApplicationStatus.APPROVED);
        } else {
            app.setStatus(ApplicationStatus.REJECTED);
        }
        applicationRepo.save(app);

        return new ReviewResponse(review.getId(), review.getReviewer().getId(), review.getDecision(), review.getComment(), review.getDecidedAt());
    }

    public List<ReviewResponse> listReviews(Long applicationId) {
        return List.of();
    }
}
