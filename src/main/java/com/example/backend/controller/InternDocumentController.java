package com.example.backend.controller;

import com.example.backend.dto.response.InternDocumentResponse;
import com.example.backend.entity.User;
import com.example.backend.exception.ApiException;
import com.example.backend.enums.DocumentType;
import com.example.backend.repository.InternDocumentRepository;
import com.example.backend.repository.InternProfileRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.InternDocumentService;
import com.example.backend.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InternDocumentController {

    private final InternDocumentService documentService;
    private final UserRepository userRepository;
    private final InternDocumentRepository internDocumentRepository;
    private final InternProfileRepository internProfileRepository;
    private final StorageService storage;

    // ========= INTERN: LIST MY DOCUMENTS =========
    // Merge ý tưởng từ controller mẫu: GET /api/intern/documents
    @GetMapping("/intern/documents")
    @PreAuthorize("hasRole('INTERN')") // ✅ chặn luôn ở tầng security
    public ResponseEntity<List<InternDocumentResponse>> getMyDocuments() {
        User user = getAuthenticatedUserOrThrow();
        Long userId = user.getId();

        var ip = internProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Intern profile not found for user '" + user.getEmail() + "' (userId=" + userId + "). Please contact HR."));

        Long internId = ip.getId();
        List<InternDocumentResponse> docs = documentService.getMyDocuments(internId);
        return ResponseEntity.ok(docs);
    }

    // ========= HR/ADMIN: LIST DOCUMENTS OF AN INTERN =========
    @GetMapping("/hr/interns/{id}/documents")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<List<InternDocumentResponse>> getInternDocumentsForHr(@PathVariable("id") Long internId) {
        List<InternDocumentResponse> docs = documentService.getDocumentsOfIntern(internId);
        return ResponseEntity.ok(docs);
    }

    // ========= INTERN: UPLOAD MY DOCUMENT =========
    // Merge ý tưởng từ controller mẫu: POST /api/intern/documents/upload
    @PostMapping(path = { "/intern/documents", "/intern/documents/upload", "/documents/upload" })
    @PreAuthorize("hasRole('INTERN')") // ✅ đúng như story
    public ResponseEntity<InternDocumentResponse> uploadMyDocument(
            @RequestParam("type") DocumentType type,
            @RequestParam("file") MultipartFile file
    ) {
        User user = getAuthenticatedUserOrThrow();
        Long userId = user.getId();

        var ip = internProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Intern profile not found for user '" + user.getEmail() + "' (userId=" + userId + "). Please contact HR."));

        InternDocumentResponse resp = documentService.uploadForIntern(ip.getId(), type, file);
        return ResponseEntity.ok(resp);
    }

    // ========= DOWNLOAD DOCUMENT =========
    // Dùng chung cho Intern (file của mình) + HR (theo rule bạn viết: HR chỉ tải PDF)
    @GetMapping("/documents/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable("id") Long documentId) {
        User user = getAuthenticatedUserOrThrow();
        Long userId = user.getId();

        boolean isHrOrAdmin = userRepository.existsByIdAndRoleCode(userId, "HR")
                || userRepository.existsByIdAndRoleCode(userId, "ADMIN");

        var doc = internDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Document not found: " + documentId));

        String fileUrl = doc.getFileUrl();
        String filename = (fileUrl != null && fileUrl.contains("/"))
                ? fileUrl.substring(fileUrl.lastIndexOf('/') + 1)
                : "file";

        boolean isPdf = filename.toLowerCase().endsWith(".pdf");
        if (isHrOrAdmin && !isPdf) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "HR/Admin can download only PDF files");
        }

        Resource resource = storage.loadFileAsResource(fileUrl);

        try {
            long size = resource.contentLength();
            MediaType contentType = isPdf ? MediaType.APPLICATION_PDF : MediaType.APPLICATION_OCTET_STREAM;

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentLength(size)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to prepare file for download");
        }
    }

    // alias: /api/documents/download/{id}
    @GetMapping("/documents/download/{id}")
    public ResponseEntity<Resource> downloadDocumentAlias(@PathVariable("id") Long documentId) {
        return downloadDocument(documentId);
    }

    // ========= STATUS =========
    @GetMapping("/documents/{id}/status")
    public ResponseEntity<?> getDocumentStatus(@PathVariable("id") Long documentId) {
        var doc = internDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Document not found: " + documentId));

        return ResponseEntity.ok(java.util.Map.of(
                "id", doc.getId(),
                "status", doc.getStatus(),
                "reviewedById", doc.getReviewedBy() != null ? doc.getReviewedBy().getId() : null,
                "reviewedAt", doc.getReviewedAt(),
                "reviewNote", doc.getReviewNote()
        ));
    }

    // ========= HR APPROVE / REJECT =========
    @PostMapping("/hr/documents/{id}/approve")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<InternDocumentResponse> approveDocument(@PathVariable("id") Long documentId) {
        User user = getAuthenticatedUserOrThrow();
        InternDocumentResponse resp = documentService.approve(documentId, user.getId());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/hr/documents/{id}/reject")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<InternDocumentResponse> rejectDocument(
            @PathVariable("id") Long documentId,
            @RequestParam(value = "note", required = false) String note
    ) {
        User user = getAuthenticatedUserOrThrow();
        InternDocumentResponse resp = documentService.reject(documentId, user.getId(), note);
        return ResponseEntity.ok(resp);
    }

    // legacy PUT endpoints
    @PutMapping("/documents/{id}/approve")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<InternDocumentResponse> approveDocumentPut(
            @PathVariable("id") Long documentId,
            @RequestParam(value = "hrUserId", required = false) Long hrUserId
    ) {
        Long actingHrId = (hrUserId != null) ? hrUserId : getAuthenticatedUserOrThrow().getId();
        InternDocumentResponse resp = documentService.approve(documentId, actingHrId);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/documents/{id}/reject")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<InternDocumentResponse> rejectDocumentPut(
            @PathVariable("id") Long documentId,
            @RequestParam(value = "hrUserId", required = false) Long hrUserId,
            @RequestParam(value = "note", required = false) String note
    ) {
        Long actingHrId = (hrUserId != null) ? hrUserId : getAuthenticatedUserOrThrow().getId();
        InternDocumentResponse resp = documentService.reject(documentId, actingHrId, note);
        return ResponseEntity.ok(resp);
    }

    // ========= HELPER =========
    private User getAuthenticatedUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        String principalName = auth.getName(); // email
        return userRepository.findByEmail(principalName)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found: " + principalName));
    }
}
