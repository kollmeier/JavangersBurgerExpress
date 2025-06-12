package de.ckollmeier.burgerexpress.backend.configuration;

import de.ckollmeier.burgerexpress.backend.controller.DisplayCategoryController;
import de.ckollmeier.burgerexpress.backend.controller.DishesController;
import de.ckollmeier.burgerexpress.backend.controller.FilesController;
import de.ckollmeier.burgerexpress.backend.controller.MenusController;
import de.ckollmeier.burgerexpress.backend.controller.OrderableItemController;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.service.DishService;
import de.ckollmeier.burgerexpress.backend.service.DisplayCategoryService;
import de.ckollmeier.burgerexpress.backend.service.FilesService;
import de.ckollmeier.burgerexpress.backend.service.ImagesService;
import de.ckollmeier.burgerexpress.backend.service.MenuService;
import de.ckollmeier.burgerexpress.backend.service.OrderableItemService;
import de.ckollmeier.burgerexpress.backend.service.SortableService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
    DisplayCategoryController.class,
    DishesController.class,
    MenusController.class,
    FilesController.class,
    OrderableItemController.class
})
@Import(SecurityConfig.class)
@DisplayName("Security Configuration Tests")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock services for DisplayCategoryController
    @MockitoBean
    private DisplayCategoryService displayCategoryService;

    @MockitoBean
    private SortableService<DisplayCategory> displayCategorySortableService;

    // Mock services for DishesController
    @MockitoBean
    private DishService dishService;

    @MockitoBean
    private SortableService<Dish> dishSortableService;

    // Mock services for MenusController
    @MockitoBean
    private MenuService menuService;

    @MockitoBean
    private SortableService<Menu> menuSortableService;

    // Mock services for FilesController
    @MockitoBean
    private FilesService filesService;

    @MockitoBean
    private ImagesService imagesService;

    // Mock services for OrderableItemController
    @MockitoBean
    private OrderableItemService orderableItemService;

    // Mock UserDetailsService for SecurityConfig
    @MockitoBean
    private UserDetailsService userDetailsService;

    // Public endpoints test moved to PublicEndpointsTest

    @Test
    @DisplayName("Protected endpoints should require authentication")
    @WithAnonymousUser
    void protectedEndpointsShouldRequireAuthentication() throws Exception {
        // Test access to protected endpoints without authentication
        mockMvc.perform(post("/api/displayCategories"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/menus"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Manager-only endpoints should be accessible with MANAGER role")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void managerEndpointsShouldBeAccessibleWithManagerRole() throws Exception {
        // Test access to manager-only endpoints with MANAGER role
        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/menus"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Manager-only endpoints should be inaccessible with other roles")
    @WithMockUser(username = "kitchen", roles = {"KITCHEN"})
    void managerEndpointsShouldBeInaccessibleWithOtherRoles() throws Exception {
        // Test access to manager-only endpoints with KITCHEN role
        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isForbidden()); // Expect 403 Forbidden due to AccessDeniedException

        mockMvc.perform(get("/api/menus"))
                .andExpect(status().isForbidden()); // Expect 403 Forbidden due to AccessDeniedException
    }

    @Test
    @DisplayName("File upload should be accessible only with MANAGER role")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void fileUploadShouldBeAccessibleWithManagerRole() throws Exception {
        // Create a mock multipart file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // Perform the multipart request
        mockMvc.perform(multipart("/api/files/upload")
                .file(file))
                .andExpect(status().isOk()); // Expect 200 OK
    }

    @Test
    @DisplayName("File upload should be inaccessible with other roles")
    @WithMockUser(username = "kitchen", roles = {"KITCHEN"})
    void fileUploadShouldBeInaccessibleWithOtherRoles() throws Exception {
        // Create a mock multipart file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // Perform the multipart request
        mockMvc.perform(multipart("/api/files/upload")
                .file(file))
                .andExpect(status().isForbidden()); // Expect 403 Forbidden due to AccessDeniedException
    }
}
