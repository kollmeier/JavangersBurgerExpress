package de.ckollmeier.burgerexpress.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ckollmeier.burgerexpress.backend.dto.DisplayCategoryInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.SortedInputDTO;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import de.ckollmeier.burgerexpress.backend.repository.DisplayCategoryRepository;
import de.ckollmeier.burgerexpress.backend.repository.DisplayItemRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("DisplayCategoryController")
@WithMockUser(roles = {"MANAGER"})
class DisplayCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DisplayCategoryRepository displayCategoryRepository;

    @Autowired
    private DisplayItemRepository displayItemCategoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private DisplayCategory category1;
    private DisplayCategory category2;
    private DisplayCategory category3;

    private DisplayItem item1;
    private DisplayItem item2;

    @BeforeEach
    void setUp() {
        displayCategoryRepository.deleteAll();

        category1 = DisplayCategory.builder()
            .name("Burger")
            .description("Burger und Getränke")
            .imageUrl( "https://example.com/burger.jpg")
            .position(1)
            .build();

        category2 = DisplayCategory.builder()
            .name("Pizza")
            .description("Pizza und Getränke")
            .position(2)
            .build();

        category3 = DisplayCategory.builder()
            .name("Drinks")
            .description("Getränke")
            .position(3)
            .build();

        category1 = displayCategoryRepository.save(category1);
        category2 = displayCategoryRepository.save(category2);
        category3 = displayCategoryRepository.save(category3);

        item1 = DisplayItem.builder()
                .name("Pizza Margherita")
                .description("Margherita mit Tomatensoße")
                .categoryId(new ObjectId(category2.getId()))
                .build();

        item2 = DisplayItem.builder()
                .name("Pizza Salami")
                .description("Pizza Salami mit Tomatensoße")
                .categoryId(new ObjectId(category2.getId()))
                .build();

        item1 = displayItemCategoryRepository.save(item1);
        item2 = displayItemCategoryRepository.save(item2);
    }

    @AfterEach
    void tearDown() {
        displayCategoryRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/displayCategories gibt alle Kategorien zurück")
    void should_returnAllDisplayCategories_WhenGetAllDisplayCategories() throws Exception {
        mockMvc.perform(get("/api/displayCategories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[?(@.name=='Burger')]").exists())
                .andExpect(jsonPath("$[?(@.name=='Pizza')]").exists())
                .andExpect(jsonPath("$[?(@.name=='Pizza')].displayItems.length()").value(2))
                .andExpect(jsonPath("$[?(@.name=='Pizza')].displayItems[0].name").value("Pizza Margherita"))
                .andExpect(jsonPath("$[?(@.name=='Pizza')].displayItems[1].name").value("Pizza Salami"))
                .andExpect(jsonPath("$[?(@.name=='Drinks')]").exists());
    }

    @Test
    @DisplayName("POST /api/displayCategories fügt eine neue Kategorie hinzu und gibt sie zurück")
    void should_addDisplayCategoryAndReturnIt_WhenValidInputGiven() throws Exception {
        DisplayCategoryInputDTO inputDTO = new DisplayCategoryInputDTO(
                "Salate",
                "Salat und Getränke",
                "",
                true
        );

        mockMvc.perform(post("/api/displayCategories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Salate"))
                .andExpect(jsonPath("$.description").value("Salat und Getränke"));

        Assertions.assertTrue(displayCategoryRepository.findAll().stream().anyMatch(c -> "Salate".equals(c.getName())));
    }

    @Test
    @DisplayName("POST /api/displayCategories should add a new category and return it first in the list when retrieving all categories")
    void should_returnNewlyAddedCategoryFirstInList_WhenAddingCategoryWithSamePosition() throws Exception {
        // Given
        // Create a new category with the same position as an existing category
        DisplayCategoryInputDTO inputDTO = new DisplayCategoryInputDTO(
                "New Test Category",
                "Test Description",
                "https://example.com/test.jpg",
                true
        );
        String inputJson = objectMapper.writeValueAsString(inputDTO);

        // When - Add the new category
        String responseJson = mockMvc.perform(post("/api/displayCategories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the ID of the newly created category
        String createdId = objectMapper.readTree(responseJson).get("id").asText();

        // Then - Get all categories and verify the newly added category appears first
        mockMvc.perform(get("/api/displayCategories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(4))) // Now we have 4 categories
                .andExpect(jsonPath("$[0].id").value(createdId)); // The newly added category should be first

        // Verify that the new category has position 0 (default) and appears first due to createdAt ordering
        DisplayCategory newCategory = displayCategoryRepository.findById(createdId).orElseThrow();

        // Find another category with the same position
        displayCategoryRepository.findAll().stream()
                .filter(c -> c.getPosition() == newCategory.getPosition() && !c.getId().equals(createdId))
                .findFirst().ifPresent(
                        existingCategoryWithSamePosition -> Assertions.assertTrue(
                                newCategory.getCreatedAt()
                                .isAfter(existingCategoryWithSamePosition.getCreatedAt()))
                );

    }

    @Nested
    @DisplayName("PUT /{displayCategoryId}")
    class UpdateDisplayCategoryTests {

        @Test
        @DisplayName("aktualisiert eine Kategorie und gibt das aktualisierte Objekt zurück")
        void should_updateDisplayCategory_WhenCategoryExists() throws Exception {
            DisplayCategoryInputDTO updated = new DisplayCategoryInputDTO(
                    "Burger Updated",
                    null,
                    "",
                    false
            );
            String categoryIdToUpdate = category1.getId();
            mockMvc.perform(put("/api/displayCategories/" + categoryIdToUpdate)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Burger Updated"))
                    .andExpect(jsonPath("$.description").value("Burger und Getränke"));

            DisplayCategory updatedCategory = displayCategoryRepository.findById(categoryIdToUpdate).orElseThrow();
            Assertions.assertEquals("Burger Updated", updatedCategory.getName());
            Assertions.assertEquals("Burger und Getränke", updatedCategory.getDescription());
            Assertions.assertEquals("", updatedCategory.getImageUrl());
            Assertions.assertFalse(updatedCategory.isPublished());
        }

        @Test
        @DisplayName("gibt 404 zurück, wenn die zu aktualisierende Kategorie nicht existiert")
        void should_return404_WhenUpdatingNonExistingCategory() throws Exception {
            // Arrange
            String nonExistentId = "non-existent-id";
            DisplayCategoryInputDTO updateDto = new DisplayCategoryInputDTO(
                    "NichtGefunden",
                    "",
                    "",
                    true
            );

            // Act & Assert
            mockMvc.perform(
                    put("/api/displayCategories/" + nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto))
            )
            .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /{displayCategoryId}")
    class DeleteDisplayCategoryTests {

        @Test
        @DisplayName("löscht eine Kategorie und gibt HTTP 204 zurück")
        void should_removeDisplayCategory_WhenCategoryExists() throws Exception {
            mockMvc.perform(delete("/api/displayCategories/" + category2.getId()))
                    .andExpect(status().isNoContent());

            Assertions.assertFalse(displayCategoryRepository.findById(category2.getId()).isPresent());
        }

        @Test
        @DisplayName("gibt 404 zurück, wenn die zu löschende Kategorie nicht existiert")
        void should_return404_WhenDeletingNonExistingCategory() throws Exception {
            // Arrange
            String nonExistentId = "non-existent-id";

            // Act & Assert
            mockMvc.perform(delete("/api/displayCategories/" + nonExistentId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /positions")
    class UpdateDisplayCategoryPositionsTests {

        @Test
        @DisplayName("aktualisiert Positionen aller DisplayCategories und gibt neue Liste zurück")
        void should_updateDisplayCategoryPositions_WhenAllCategoriesExist() throws Exception {
            List<SortedInputDTO> sorted = Arrays.asList(
                    new SortedInputDTO(1, category3.getId(), null),
                    new SortedInputDTO(2, category1.getId(), null),
                    new SortedInputDTO(3, category2.getId(), null)
            );

            mockMvc.perform(put("/api/displayCategories/positions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sorted)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].id").value(category3.getId()))
                    .andExpect(jsonPath("$[1].id").value(category1.getId()))
                    .andExpect(jsonPath("$[2].id").value(category2.getId()));
        }
    }
}
