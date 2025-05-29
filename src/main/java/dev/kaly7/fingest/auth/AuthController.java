package dev.kaly7.fingest.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for authentication related endpoints
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SecurityUtils securityUtils;
    
    public AuthController(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    /**
     * Endpoint to get user info
     * @param principal the authenticated principal
     * @return a map with user information
     */
    @GetMapping("/userinfo")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal Jwt principal) {
        Map<String, Object> userInfo = new HashMap<>();
        
        if (principal != null) {
            userInfo.put("username", principal.getClaimAsString("preferred_username"));
            userInfo.put("name", principal.getClaimAsString("name"));
            userInfo.put("email", principal.getClaimAsString("email"));
            userInfo.put("roles", securityUtils.hasRole("ADMIN") ? "ADMIN" : "USER");
        }
        
        return userInfo;
    }
    
    /**
     * Simple health check endpoint that requires authentication
     * @return a status message
     */
    @GetMapping("/status")
    public Map<String, String> getStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> status = new HashMap<>();
        
        status.put("status", "UP");
        status.put("authenticated", String.valueOf(authentication.isAuthenticated()));
        status.put("username", authentication.getName());
        
        return status;
    }
}
