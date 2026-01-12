package com.example.backend.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class RoleUtils {

    private RoleUtils() {
        // utility class
    }

    public static boolean hasRole(Authentication auth, String roleCode) {
        if (auth == null || auth.getAuthorities() == null) return false;

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority ->
                        authority.equals(roleCode) ||
                                authority.equals("ROLE_" + roleCode)
                );
    }

    public static boolean isAdmin(Authentication auth) {
        return hasRole(auth, "ADMIN");
    }

    public static boolean isHr(Authentication auth) {
        return hasRole(auth, "HR");
    }

    public static boolean isMentor(Authentication auth) {
        return hasRole(auth, "MENTOR");
    }

    public static boolean isIntern(Authentication auth) {
        return hasRole(auth, "INTERN");
    }

    public static boolean isAdminOrHr(Authentication auth) {
        return isAdmin(auth) || isHr(auth);
    }

    public static boolean isMentorOrAdminHr(Authentication auth) {
        return isMentor(auth) || isAdminOrHr(auth);
    }
}
