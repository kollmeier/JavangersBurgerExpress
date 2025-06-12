package de.ckollmeier.burgerexpress.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for authentication-related endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Get the current user information.
     *
     * @param authentication the current authentication
     * @return the current user information
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        Map<String, Object> userInfo = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            userInfo.put("username", authentication.getName());
            userInfo.put("authenticated", true);
            userInfo.put("authorities", authentication.getAuthorities());
        } else {
            userInfo.put("authenticated", false);
        }

        return ResponseEntity.ok(userInfo);
    }
}
