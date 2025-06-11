package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.configuration.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("GET /api/auth/user should return authenticated=false for anonymous user")
    @WithAnonymousUser
    void getCurrentUser_shouldReturnNotAuthenticated_whenUserIsAnonymous() throws Exception {
        mockMvc.perform(get("/api/auth/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.authorities").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/auth/user should return user info for authenticated user with MANAGER role")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void getCurrentUser_shouldReturnUserInfo_whenUserIsAuthenticatedWithManagerRole() throws Exception {
        mockMvc.perform(get("/api/auth/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.username").value("manager"))
                .andExpect(jsonPath("$.authorities", hasSize(1)))
                .andExpect(jsonPath("$.authorities[0].authority").value("ROLE_MANAGER"));
    }

    @Test
    @DisplayName("GET /api/auth/user should return user info for authenticated user with KITCHEN role")
    @WithMockUser(username = "kitchen", roles = {"KITCHEN"})
    void getCurrentUser_shouldReturnUserInfo_whenUserIsAuthenticatedWithKitchenRole() throws Exception {
        mockMvc.perform(get("/api/auth/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.username").value("kitchen"))
                .andExpect(jsonPath("$.authorities", hasSize(1)))
                .andExpect(jsonPath("$.authorities[0].authority").value("ROLE_KITCHEN"));
    }

    @Test
    @DisplayName("GET /api/auth/user should return user info for authenticated user with CASHIER role")
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    void getCurrentUser_shouldReturnUserInfo_whenUserIsAuthenticatedWithCashierRole() throws Exception {
        mockMvc.perform(get("/api/auth/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.username").value("cashier"))
                .andExpect(jsonPath("$.authorities", hasSize(1)))
                .andExpect(jsonPath("$.authorities[0].authority").value("ROLE_CASHIER"));
    }

    @Test
    @DisplayName("GET /api/auth/user should return user info for authenticated user with multiple roles")
    @WithMockUser(username = "admin", roles = {"MANAGER", "KITCHEN", "CASHIER"})
    void getCurrentUser_shouldReturnUserInfo_whenUserIsAuthenticatedWithMultipleRoles() throws Exception {
        mockMvc.perform(get("/api/auth/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.authorities", hasSize(3)))
                .andExpect(jsonPath("$.authorities[?(@.authority=='ROLE_MANAGER')]").exists())
                .andExpect(jsonPath("$.authorities[?(@.authority=='ROLE_KITCHEN')]").exists())
                .andExpect(jsonPath("$.authorities[?(@.authority=='ROLE_CASHIER')]").exists());
    }
}
