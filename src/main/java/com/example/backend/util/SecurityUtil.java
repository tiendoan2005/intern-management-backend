package com.example.backend.util;

import com.example.backend.entity.User;

public final class SecurityUtil {
    private SecurityUtil() {}

    public static boolean hasRole(User user, String roleCode) {
        if (user == null || user.getRoles() == null) return false;
        String target = roleCode.trim().toUpperCase();
        return user.getRoles().stream()
                .anyMatch(r -> r.getCode() != null && r.getCode().trim().toUpperCase().equals(target));
    }
}
