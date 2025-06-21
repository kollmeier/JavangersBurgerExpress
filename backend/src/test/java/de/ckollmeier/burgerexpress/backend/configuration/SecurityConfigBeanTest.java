package de.ckollmeier.burgerexpress.backend.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SecurityConfig Bean Tests")
class SecurityConfigBeanTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Password encoder should be BCryptPasswordEncoder")
    void passwordEncoderShouldBeBCryptPasswordEncoder() {
        // Verify that the password encoder is an instance of BCryptPasswordEncoder
        assertThat(passwordEncoder.getClass().getName()).contains("BCrypt");
    }

    @Test
    @DisplayName("Password encoder should encode passwords correctly")
    void passwordEncoderShouldEncodePasswordsCorrectly() {
        // Test that the password encoder can encode and verify passwords
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Encoded password should not be the same as raw password
        assertThat(encodedPassword).isNotEqualTo(rawPassword);

        // Password encoder should be able to verify the password
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("UserDetailsService should provide users with correct roles")
    void userDetailsServiceShouldProvideUsersWithCorrectRoles() {
        // Test that the UserDetailsService provides users with the expected roles
        UserDetails managerUser = userDetailsService.loadUserByUsername("manager");
        UserDetails kitchenUser = userDetailsService.loadUserByUsername("kitchen");
        UserDetails cashierUser = userDetailsService.loadUserByUsername("cashier");

        // Verify that users have the expected roles
        assertThat(managerUser.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_" + SecurityConfig.ROLE_MANAGER));
        assertThat(kitchenUser.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_" + SecurityConfig.ROLE_KITCHEN));
        assertThat(cashierUser.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_" + SecurityConfig.ROLE_CASHIER));
    }

    @Test
    @DisplayName("CSRF should be disabled")
    void csrfShouldBeDisabled() throws Exception {
        // Test that CSRF is disabled by sending a POST request without a CSRF token
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "manager")
                .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized()); // Should fail due to wrong credentials, not CSRF
    }

    @Test
    @DisplayName("HTTP Basic authentication should be configured")
    void httpBasicAuthenticationShouldBeConfigured() throws Exception {
        // Test that HTTP Basic authentication is configured
        mockMvc.perform(get("/api/dishes")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString("manager:wrongpassword".getBytes())))
                .andExpect(status().isUnauthorized()); // Should fail due to wrong credentials
    }

    @Test
    @DisplayName("Login success handler should return 200 OK with success message")
    void loginSuccessHandler_shouldReturn200WithSuccessMessage() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "manager")
                .param("password", "test-password"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"success\":true}"));
    }
}
