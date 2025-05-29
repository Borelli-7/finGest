package dev.kaly7.fingest.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper class for extracting user information from the security context
 */
@Component
public class SecurityUtils {

    /**
     * Get the authenticated username from the security context
     * @return the username
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
    
    /**
     * Check if the current user has a specific role
     * @param role the role to check
     * @return true if the user has the role
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + role));
        }
        return false;
    }
    
    /**
     * Check if a request path is a system path
     * @param request the HTTP request
     * @return true if the path is a system path
     */
    public boolean isSystemPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/swagger-ui/") || 
               path.contains("/v3/api-docs") || 
               path.contains("/swagger-ui.html");
    }
}
