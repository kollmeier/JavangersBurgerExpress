package de.ckollmeier.burgerexpress.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ckollmeier.burgerexpress.backend.dto.DisplayItemInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.SortedInputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.repository.DisplayCategoryRepository;
import de.ckollmeier.burgerexpress.backend.repository.DisplayItemRepository;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("DisplayItemController")
class DisplayItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DisplayItemRepository displayItemRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private DisplayCategoryRepository displayCategoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private DisplayCategory category;
    private DisplayItem item1;
    private DisplayItem item2;
    private String orderableItemId;

    @BeforeEach
    void setUp() {
        displayItemRepository.deleteAll();
        displayCategoryRepository.deleteAll();

        category = DisplayCategory.builder()
                .name("Test Category")
                .description("Test Category Description")
                .position(1)
                .build();

        category = displayCategoryRepository.save(category);

        item1 = DisplayItem.builder()
                .name("Test Item 1")
                .description("Test Item 1 Description")
                .categoryId(new ObjectId(category.getId()))
                .position(1)
                .build();

        item2 = DisplayItem.builder()
                .name("Test Item 2")
                .description("Test Item 2 Description")
                .categoryId(new ObjectId(category.getId()))
                .position(2)
                .build();

        item1 = displayItemRepository.save(item1);
        item2 = displayItemRepository.save(item2);

        // Add some orderable items to the category
        for (int i = 0; i < 5; i++) {
            Dish orderableItem = Dish.builder()
                    .name("test-orderable-item-" + i)
                    .type(DishType.MAIN)
                    .price(BigDecimal.TEN)
                    .build();

            orderableItem = dishRepository.save(orderableItem);

            // Store the ID of the first OrderableItem for use in tests
            if (i == 1) {
                orderableItemId = orderableItem.getId();
            }
        }
    }

    @AfterEach
    void tearDown() {
        displayItemRepository.deleteAll();
        displayCategoryRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/displayItems gibt alle Display Items zurück")
    void should_returnAllDisplayItems_WhenGetAllDisplayItems() throws Exception {
        mockMvc.perform(get("/api/displayItems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[?(@.name=='Test Item 1')]").exists())
                .andExpect(jsonPath("$[?(@.name=='Test Item 2')]").exists());
    }

    @Test
    @DisplayName("POST /api/displayItems fügt ein neues Display Item hinzu und gibt es zurück")
    void should_addDisplayItemAndReturnIt_WhenValidInputGiven() throws Exception {
        DisplayItemInputDTO inputDTO = new DisplayItemInputDTO(
                "New Test Item",
                "New Test Item Description",
                false,
                null,
                List.of(orderableItemId),
                true,
                category.getId()
        );

        mockMvc.perform(post("/api/displayItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Test Item"))
                .andExpect(jsonPath("$.description").value("New Test Item Description"))
                .andExpect(jsonPath("$.published").value(true));

        Assertions.assertTrue(displayItemRepository.findAll().stream().anyMatch(i -> "New Test Item".equals(i.getName())));
    }

    @Nested
    @DisplayName("PUT /{displayItemId}")
    class UpdateDisplayItemTests {

        @Test
        @DisplayName("aktualisiert ein Display Item und gibt das aktualisierte Objekt zurück")
        void should_updateDisplayItem_WhenItemExists() throws Exception {
            DisplayItemInputDTO updated = new DisplayItemInputDTO(
                    "Updated Test Item",
                    "Updated Test Item Description",
                    false,
                    null,
                    List.of(orderableItemId),
                    true,
                    category.getId()
            );
            String itemIdToUpdate = item1.getId();
            mockMvc.perform(put("/api/displayItems/" + itemIdToUpdate)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Test Item"))
                    .andExpect(jsonPath("$.description").value("Updated Test Item Description"))
                    .andExpect(jsonPath("$.published").value(true));

            DisplayItem updatedItem = displayItemRepository.findById(itemIdToUpdate).orElseThrow();
            Assertions.assertEquals("Updated Test Item", updatedItem.getName());
            Assertions.assertEquals("Updated Test Item Description", updatedItem.getDescription());
            Assertions.assertTrue(updatedItem.isPublished());
        }

        @Test
        @DisplayName("gibt 404 zurück, wenn das zu aktualisierende Display Item nicht existiert")
        void should_return404_WhenUpdatingNonExistingItem() throws Exception {
            String nonExistentId = "non-existent-id";
            DisplayItemInputDTO updateDto = new DisplayItemInputDTO(
                    "Non-existent Item",
                    "This item doesn't exist",
                    false,
                    null,
                    List.of("test-orderable-item-id"),
                    true,
                    category.getId()
            );

            mockMvc.perform(
                    put("/api/displayItems/" + nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto))
            )
            .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /{displayItemId}")
    class DeleteDisplayItemTests {

        @Test
        @DisplayName("löscht ein Display Item und gibt HTTP 204 zurück")
        void should_removeDisplayItem_WhenItemExists() throws Exception {
            mockMvc.perform(delete("/api/displayItems/" + item2.getId()))
                    .andExpect(status().isNoContent());

            Assertions.assertFalse(displayItemRepository.findById(item2.getId()).isPresent());
        }

        @Test
        @DisplayName("gibt 404 zurück, wenn das zu löschende Display Item nicht existiert")
        void should_return404_WhenDeletingNonExistingItem() throws Exception {
            String nonExistentId = "non-existent-id";

            mockMvc.perform(delete("/api/displayItems/" + nonExistentId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /positions")
    class UpdateDisplayItemPositionsTests {

        @Test
        @DisplayName("aktualisiert Positionen aller Display Items und gibt neue Liste zurück")
        void should_updateDisplayItemPositions_WhenAllItemsExist() throws Exception {
            List<SortedInputDTO> sorted = Arrays.asList(
                    new SortedInputDTO(1, item2.getId()),
                    new SortedInputDTO(2, item1.getId())
            );

            mockMvc.perform(put("/api/displayItems/positions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sorted)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(item2.getId()))
                    .andExpect(jsonPath("$[1].id").value(item1.getId()));
        }
    }
}
