    package com.example.backend.controller;

    import com.example.backend.dto.request.ApplicationRequest;
    import com.example.backend.dto.request.ReviewRequest;
    import com.example.backend.dto.response.ApplicationListItemResponse;
    import com.example.backend.dto.response.ApplicationResponse;
    import com.example.backend.dto.response.ReviewResponse;
    import com.example.backend.enums.ApplicationStatus;
    import com.example.backend.service.ApplicationQueryService;
    import com.example.backend.service.ApplicationReviewService;
    import com.example.backend.service.ApplicationService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.Authentication;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/applications")
    @PreAuthorize("hasRole('HR')")
    public class ApplicationController {

        private final ApplicationService applicationService;
        private final ApplicationQueryService queryService;
        private final ApplicationReviewService reviewService;

        public ApplicationController(ApplicationService applicationService,ApplicationQueryService queryService, ApplicationReviewService reviewService) {
            this.queryService = queryService;
            this.applicationService = applicationService;
            this.reviewService = reviewService;
        }

        @GetMapping
        public Page<ApplicationListItemResponse> list(
                @RequestParam(required = false)ApplicationStatus status,
                @RequestParam(required = false) String keyword,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size
                ) {
            return queryService.list(status, keyword, page, size);
        }

        @GetMapping("/{id}")
        public ApplicationResponse detail(@PathVariable Long id) {
            List<ReviewResponse> reviews = reviewService.listReviews(id);
            return queryService.detail(id, reviews);
        }

        @PostMapping("/{id}/review")
        public ReviewResponse review(@PathVariable Long id, @Valid @RequestBody ReviewRequest req, Authentication auth) {
            Long reviewerId = 1L;
            return reviewService.review(id, reviewerId, req);
        }
        @PostMapping
        public ResponseEntity<ApplicationResponse> submitApplication(@Valid @RequestBody ApplicationRequest req) {
            ApplicationResponse resp = applicationService.submitApplication(req);
            return ResponseEntity.ok(resp);
        }

        @GetMapping("/my")
        public ResponseEntity<List<ApplicationResponse>> getMyApplications() {
            List<ApplicationResponse> list = applicationService.getMyApplications();
            return ResponseEntity.ok(list);
        }
    }
