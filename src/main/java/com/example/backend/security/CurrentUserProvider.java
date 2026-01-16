package com.example.backend.security;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;

    private Authentication requireAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Unauthenticated");
        }
        return auth;
    }

    /** subject = email (đúng theo CustomUserDetailsService của bạn) */
    public String requireEmail() {
        return requireAuth().getName().trim().toLowerCase();
    }

    /** Load full User từ DB theo email */
    public User requireUser() {
        String email = requireEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
    }

    /** NEW: lấy userId hiện tại (dùng nhiều trong service) */
    public Long requireUserId() {
        return requireUser().getId();
    }

    /** Optional: check role (không throw) */
    public boolean hasRole(String roleNameOrCode) {
        User u = requireUser();
        if (u.getRoles() == null) return false;

        // hỗ trợ cả "HR" và "ROLE_HR"
        String expected = roleNameOrCode.trim().toUpperCase();
        String expectedNoPrefix = expected.startsWith("ROLE_") ? expected.substring(5) : expected;

        return u.getRoles().stream().anyMatch(r -> {
            String code = (r.getCode() != null ? r.getCode() : "").trim().toUpperCase();
            String name = (r.getName() != null ? r.getName() : "").trim().toUpperCase();

            String codeNoPrefix = code.startsWith("ROLE_") ? code.substring(5) : code;

            return expected.equals(code) ||
                    expected.equals("ROLE_" + codeNoPrefix) ||
                    expectedNoPrefix.equals(codeNoPrefix) ||
                    expectedNoPrefix.equals(name);
        });
    }

    /** Optional: chặn role tại service */
    public void requireRole(String roleNameOrCode) {
        if (!hasRole(roleNameOrCode)) {
            throw new AccessDeniedException("Required role: " + roleNameOrCode);
        }
    }

    /** NEW: chặn 1 trong nhiều role */
    public void requireAnyRole(String... roles) {
        if (roles == null || roles.length == 0) {
            throw new IllegalArgumentException("roles must not be empty");
        }
        boolean ok = Arrays.stream(roles).anyMatch(this::hasRole);
        if (!ok) {
            throw new AccessDeniedException("Required any role: " + String.join(", ", roles));
        }
    }

    public Long getCurrentUserId() {
        return requireUserId();
    }
}
