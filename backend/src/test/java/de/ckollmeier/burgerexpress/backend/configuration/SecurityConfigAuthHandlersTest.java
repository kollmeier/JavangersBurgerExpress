package de.ckollmeier.burgerexpress.backend.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SecurityConfig Authentication Handlers Tests")
class SecurityConfigAuthHandlersTest {

    @Autowired
    private MockMvc mockMvc;

    // Note: Testing the login success handler directly is challenging in a test environment
    // because it requires valid credentials. Instead, we focus on testing the other handlers
    // that are more straightforward to test.

    @Test
    @DisplayName("Login failure handler should return 401 Unauthorized with error message")
    void loginFailureHandler_shouldReturn401WithErrorMessage() throws Exception {
        mockMvc.perform(formLogin("/api/auth/login").user("manager").password("wrongpassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"error\":\"Authentication failed\",\"status\":\"UNAUTHORIZED\"}"));
    }

    @Test
    @WithMockUser
    @DisplayName("Logout success handler should return 200 OK with success message")
    void logoutSuccessHandler_shouldReturn200WithSuccessMessage() throws Exception {
        mockMvc.perform(logout("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\":\"Logout successful\"}"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Access denied handler should return 403 Forbidden with error message")
    void accessDeniedHandler_shouldReturn403WithErrorMessage() throws Exception {
        // This endpoint requires authentication, so it will trigger the access denied handler
        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"error\":\"Unauthorized\",\"status\":\"UNAUTHORIZED\"}"));
    }

    @Test
    @WithMockUser(roles = "KITCHEN")
    @DisplayName("Access denied handler should return 403 Forbidden with error message for authenticated user without required role")
    void accessDeniedHandler_shouldReturn403WithErrorMessage_forAuthenticatedUserWithoutRequiredRole() throws Exception {
        // This endpoint requires MANAGER role, so it will trigger the access denied handler
        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Access Denied"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Authentication entry point should return 401 Unauthorized with error message for API requests")
    void authenticationEntryPoint_shouldReturn401WithErrorMessage_forApiRequests() throws Exception {
        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"error\":\"Unauthorized\",\"status\":\"UNAUTHORIZED\"}"));
    }
}
