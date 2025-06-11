package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.configuration.SecurityConfig;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import de.ckollmeier.burgerexpress.backend.service.DisplayCategoryService;
import de.ckollmeier.burgerexpress.backend.service.FilesService;
import de.ckollmeier.burgerexpress.backend.service.ImagesService;
import de.ckollmeier.burgerexpress.backend.service.OrderableItemService;
import de.ckollmeier.burgerexpress.backend.service.SortableService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
    DisplayCategoryController.class,
    OrderableItemController.class,
    FilesController.class
})
@Import(SecurityConfig.class)
@DisplayName("Public Endpoints Tests")
class PublicEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DisplayCategoryService displayCategoryService;

    @MockitoBean
    private SortableService<DisplayCategory> displayCategorySortableService;

    @MockitoBean
    private OrderableItemService orderableItemService;

    @MockitoBean
    private FilesService filesService;

    @MockitoBean
    private ImagesService imagesService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Public endpoints should be accessible without authentication")
    @WithAnonymousUser
    void publicEndpointsShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Test access to public endpoints (permitAll)
        mockMvc.perform(get("/api/displayCategories"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/orderable-items"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/files"))
                .andExpect(status().isOk());
    }
}
