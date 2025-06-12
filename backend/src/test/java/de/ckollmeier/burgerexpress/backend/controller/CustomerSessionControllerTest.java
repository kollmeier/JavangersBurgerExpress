package de.ckollmeier.burgerexpress.backend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CustomerSessionController")
@WithMockUser
class CustomerSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private de.ckollmeier.burgerexpress.backend.service.CustomerSessionService customerSessionService;

    @Nested
    @DisplayName("GET /api/customer-sessions")
    class GetCustomerSessionTests {

        @Test
        @DisplayName("returns null when no session exists")
        void should_returnNull_whenNoSessionExists() throws Exception {
            // Given
            MockHttpSession session = new MockHttpSession();

            // When & Then
            mockMvc.perform(get("/api/customer-sessions")
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("returns the existing customer session")
        void should_returnExistingCustomerSession_whenOneExists() throws Exception {
            // Given
            MockHttpSession session = new MockHttpSession();

            // Create a CustomerSession and set it in the session
            java.time.Instant now = java.time.Instant.now();
            de.ckollmeier.burgerexpress.backend.model.CustomerSession customerSession = 
                new de.ckollmeier.burgerexpress.backend.model.CustomerSession(
                    now,
                    now.plusSeconds(300)
                );
            session.setAttribute("customerSession", customerSession);

            // Create a CustomerSessionDTO to be returned by the service
            de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO expectedDTO = 
                new de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO(
                    "2023-01-01 12:00:00",
                    "2023-01-01 12:05:00",
                    300L,
                    false
                );

            // Use Mockito to spy on the service
            de.ckollmeier.burgerexpress.backend.service.CustomerSessionService spyService = 
                org.mockito.Mockito.spy(customerSessionService);

            // Mock the getCustomerSession method to return the expected DTO
            org.mockito.Mockito.doReturn(expectedDTO)
                .when(spyService)
                .getCustomerSession(org.mockito.ArgumentMatchers.any());

            // Replace the service in the controller with the spy
            de.ckollmeier.burgerexpress.backend.controller.CustomerSessionController controller = 
                new de.ckollmeier.burgerexpress.backend.controller.CustomerSessionController(spyService);

            // Create a MockMvc instance with the controller
            MockMvc mockMvcWithSpy = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .standaloneSetup(controller)
                .build();

            // When & Then
            mockMvcWithSpy.perform(get("/api/customer-sessions")
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.createdAt").value("2023-01-01 12:00:00"))
                    .andExpect(jsonPath("$.expiresAt").value("2023-01-01 12:05:00"))
                    .andExpect(jsonPath("$.expiresInSeconds").value(300))
                    .andExpect(jsonPath("$.expired").value(false));

            // Verify that the service method was called
            org.mockito.Mockito.verify(spyService).getCustomerSession(session);
        }
    }

    @Nested
    @DisplayName("POST /api/customer-sessions")
    class CreateCustomerSessionTests {

        @Test
        @DisplayName("creates a new customer session")
        void should_createNewCustomerSession() throws Exception {
            // Given
            MockHttpSession session = new MockHttpSession();

            // When & Then
            mockMvc.perform(post("/api/customer-sessions")
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                    .andExpect(jsonPath("$.expiresInSeconds").isNumber())
                    .andExpect(jsonPath("$.expired").value(false));
        }
    }

    @Nested
    @DisplayName("PUT /api/customer-sessions")
    class RenewCustomerSessionTests {

        @Test
        @DisplayName("renews an existing customer session")
        void should_renewExistingCustomerSession() throws Exception {
            // Given
            MockHttpSession session = new MockHttpSession();

            // Create a CustomerSession and set it in the session
            java.time.Instant now = java.time.Instant.now();
            de.ckollmeier.burgerexpress.backend.model.CustomerSession customerSession = 
                new de.ckollmeier.burgerexpress.backend.model.CustomerSession(
                    now,
                    now.plusSeconds(300)
                );
            session.setAttribute("customerSession", customerSession);

            // Create a CustomerSessionDTO to be returned by the service
            de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO expectedDTO = 
                new de.ckollmeier.burgerexpress.backend.dto.CustomerSessionDTO(
                    "2023-01-01 12:00:00",
                    "2023-01-01 12:05:00",
                    300L,
                    false
                );

            // Use Mockito to spy on the service
            de.ckollmeier.burgerexpress.backend.service.CustomerSessionService spyService = 
                org.mockito.Mockito.spy(customerSessionService);

            // Mock the renewCustomerSession method to return the expected DTO
            org.mockito.Mockito.doReturn(expectedDTO)
                .when(spyService)
                .renewCustomerSession(org.mockito.ArgumentMatchers.any());

            // Replace the service in the controller with the spy
            de.ckollmeier.burgerexpress.backend.controller.CustomerSessionController controller = 
                new de.ckollmeier.burgerexpress.backend.controller.CustomerSessionController(spyService);

            // Create a MockMvc instance with the controller
            MockMvc mockMvcWithSpy = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .standaloneSetup(controller)
                .build();

            // When & Then
            mockMvcWithSpy.perform(put("/api/customer-sessions")
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.createdAt").value("2023-01-01 12:00:00"))
                    .andExpect(jsonPath("$.expiresAt").value("2023-01-01 12:05:00"))
                    .andExpect(jsonPath("$.expiresInSeconds").value(300))
                    .andExpect(jsonPath("$.expired").value(false));

            // Verify that the service method was called
            org.mockito.Mockito.verify(spyService).renewCustomerSession(session);
        }

        @Test
        @DisplayName("returns 404 when no session exists")
        void should_return404_whenNoSessionExists() throws Exception {
            // Given
            MockHttpSession session = new MockHttpSession();

            // When & Then
            mockMvc.perform(put("/api/customer-sessions")
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/customer-sessions")
    class RemoveCustomerSessionTests {

        @Test
        @DisplayName("removes the customer session")
        void should_removeCustomerSession() throws Exception {
            // Given
            MockHttpSession session = new MockHttpSession();

            // First create a session
            mockMvc.perform(post("/api/customer-sessions")
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());

            // When & Then
            mockMvc.perform(delete("/api/customer-sessions")
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verify session is removed by trying to renew it
            mockMvc.perform(put("/api/customer-sessions")
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }
}
