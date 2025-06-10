package de.ckollmeier.burgerexpress.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ckollmeier.burgerexpress.backend.dto.DishOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.SortedInputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.model.SizeInLiterAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.repository.MenuRepository;
import de.ckollmeier.burgerexpress.backend.types.AdditionalInformationType;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest // Lädt den gesamten Spring-Kontext
@AutoConfigureMockMvc // Konfiguriert MockMvc für HTTP-Anfragen
class MenusControllerTest {

    @Autowired
    private MockMvc mockMvc; // Zum Senden von HTTP-Anfragen

    @Autowired
    private MenuRepository menuRepository; // Zum Vorbereiten/Überprüfen der DB

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private ObjectMapper objectMapper; // Zum Konvertieren von Objekten in JSON

    private Dish mainDish1;
    private Dish mainDish2;
    private Menu menu1;
    private Menu menu2;
    private Menu menu3;
    private Menu menu4;

    @BeforeEach
    void setUp() {
        // Testdaten vor jedem Test erstellen und speichern
        menuRepository.deleteAll(); // Sicherstellen, dass das Repository leer ist
        dishRepository.deleteAll(); // Sicherstellen, dass das Repository leer ist

        mainDish1 = dishRepository.save(Dish.builder()
                .name("Classic Burger")
                .price(new BigDecimal("8.99"))
                .type(DishType.MAIN)
                .position(0)
                .build());

        mainDish2 = dishRepository.save(Dish.builder()
                .name("Cheese Burger")
                .price(new BigDecimal("9.50"))
                .type(DishType.MAIN)
                .position(1)
                .build());

        menu1 = menuRepository.save(Menu.builder()
                .name("Classic Burger Menu")
                .price(new BigDecimal("8.99"))
                .dishes(List.of(mainDish1, mainDish2))
                .position(0)
                .build());

        menu2 = menuRepository.save(Menu.builder()
                .name("Cheese Burger Menu")
                .price(new BigDecimal("9.50"))
                .position(1)
                .build());

        menu3 = menuRepository.save(Menu.builder()
                .name("Fries Menu")
                .price(new BigDecimal("3.50"))
                .position(2)
                .build());

        menu4 = menuRepository.save(Menu.builder()
                .name("Cola Menu")
                .price(new BigDecimal("2.50"))
                .additionalInformation(Map.of(AdditionalInformationType.SIZE_IN_LITER.name(), new SizeInLiterAdditionalInformation(new BigDecimal("0.5"))))
                .position(3)
                .build());
    }

    @AfterEach
    void tearDown() {
        // Nach jedem Test aufräumen (optional, aber gute Praxis)
        menuRepository.deleteAll();
    }

    // --- Tests für /api/menus ---
    @Test
    @DisplayName("GET /api/menus should return all menus")
    void getAllMenus_shouldReturnAllMenus() throws Exception {
        // Given: Daten sind in setUp gespeichert

        // When & Then
        mockMvc.perform(get("/api/menus"))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4))) // Erwarte 4 Menüs insgesamt
                .andExpect(jsonPath("$[0].id").value(menu1.getId()))
                .andExpect(jsonPath("$[1].id").value(menu2.getId()))
                .andExpect(jsonPath("$[2].id").value(menu3.getId()))
                .andExpect(jsonPath("$[3].id").value(menu4.getId()));
    }

    @Test
    @DisplayName("POST /api/menus should add a new menu and return it")
    void addMenu_shouldAddMenuAndReturnIt() throws Exception {
        // Given
        MenuInputDTO newMenuInput = new MenuInputDTO("Veggie Burger Menu",
                "10.99",
                List.of(mainDish1.getId(), mainDish2.getId()),
                Map.of());
        String newMenuJson = objectMapper.writeValueAsString(newMenuInput);

        long initialCount = menuRepository.count();

        // When
        MvcResult result = mockMvc.perform(post("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newMenuJson))
                // Then
                .andExpect(status().isCreated()) // HTTP 201
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        MenuOutputDTO resultMenu = objectMapper.readValue(responseBody, MenuOutputDTO.class);
        String createdId = resultMenu.id();
        assertThat(createdId).isNotEmpty();
        assertThat(resultMenu)
                .hasFieldOrPropertyWithValue("name", "Veggie Burger Menu")
                .hasFieldOrPropertyWithValue("price", "10.99");
        assertThat(resultMenu.dishes())
                .hasSize(2)
                .extracting(DishOutputDTO::id)
                .containsExactlyInAnyOrder(mainDish1.getId(), mainDish2.getId())
        ;

        // Verify Database state
        assertThat(menuRepository.count()).isEqualTo(initialCount + 1);
        Menu savedMenu = menuRepository.findById(createdId).orElseThrow();
        assertThat(savedMenu.getName()).isEqualTo("Veggie Burger Menu");
        assertThat(savedMenu.getPrice()).isEqualTo(new BigDecimal("10.99"));
        assertThat(savedMenu.getDishes()).extracting(Dish::getId).containsExactlyInAnyOrder(mainDish1.getId(), mainDish2.getId());
        assertThat(savedMenu.getAdditionalInformation()).isEmpty();
    }

    @Test
    @DisplayName("POST /api/menus should add a new menu and return it first in the list when retrieving all menus")
    void addMenu_shouldReturnNewlyAddedMenuFirstInList() throws Exception {
        // Given
        // Create a new menu with the same position as an existing menu
        MenuInputDTO newMenuInput = new MenuInputDTO(
                "New Test Menu",
                "12.99",
                List.of(mainDish1.getId()),
                Map.of()
        );
        String newMenuJson = objectMapper.writeValueAsString(newMenuInput);

        // When - Add the new menu
        MvcResult postResult = mockMvc.perform(post("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newMenuJson))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract the ID of the newly created menu
        String responseBody = postResult.getResponse().getContentAsString();
        MenuOutputDTO resultMenu = objectMapper.readValue(responseBody, MenuOutputDTO.class);
        String createdId = resultMenu.id();

        // Then - Get all menus and verify the newly added menu appears first among menus with the same position
        mockMvc.perform(get("/api/menus"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(5))) // Now we have 5 menus
                .andExpect(jsonPath("$[0].id").value(createdId)); // The newly added menu should be first

        // Verify that the new menu has the same position as menu1 but appears first due to createdAt ordering
        Menu newMenu = menuRepository.findById(createdId).orElseThrow();
        assertThat(newMenu.getPosition()).isEqualTo(menu1.getPosition());

        // Verify that the createdAt timestamp of the new menu is after the createdAt of menu1
        assertThat(newMenu.getCreatedAt()).isAfter(menu1.getCreatedAt());
    }

    @Test
    @DisplayName("PUT /{menuId} aktualisiert ein Menü und gibt aktualisiertes MenuOutputDTO zurück")
    void updateMenu_putEndpoint_returnsUpdatedMenu() throws Exception {
        // Erstellt zuerst ein Menü oder stelle sicher, dass ein bekanntes Menü existiert
        String menuId = menu1.getId();
        MenuInputDTO inputDTO = new MenuInputDTO(
                "Integration Pasta",
                "9.99",
                List.of(mainDish1.getId()),
                Map.of());

        String requestBody = objectMapper.writeValueAsString(inputDTO);

        // PUT-Request
        mockMvc.perform(put("/api/menus/" + menuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(menuId))
                .andExpect(jsonPath("$.name").value("Integration Pasta"))
                .andExpect(jsonPath("$.price").value("9.99"));
    }

    @Nested
    @DisplayName("DELETE /{menuId}")
    class DeleteMenuTests {

        @Test
        @DisplayName("DELETE /{menuId} sollte ein Menü löschen und HTTP 204 zurückgeben")
        void deleteMenu_shouldRemoveMenu_whenMenuExists() throws Exception {
            // Given
            String menuIdToDelete = menu1.getId();
            long initialCount = menuRepository.count();

            // When & Then
            mockMvc.perform(delete("/api/menus/" + menuIdToDelete))
                    .andExpect(status().isNoContent()); // HTTP 204

            // Verify database state
            assertThat(menuRepository.count()).isEqualTo(initialCount - 1);
            assertThat(menuRepository.findById(menuIdToDelete)).isEmpty();
        }

        @Test
        @DisplayName("DELETE /{menuId} sollte HTTP 404 zurückgeben, wenn das Menü nicht existiert")
        void deleteMenu_shouldReturn404_whenMenuDoesNotExist() throws Exception {
            // Given
            String nonExistentMenuId = "non-existent-12345";

            // When & Then
            mockMvc.perform(delete("/api/menus/" + nonExistentMenuId))
                    .andExpect(status().isNotFound()); // HTTP 400
        }
    }

    @Test
    @DisplayName("PUT /{menuId} gibt 404 zurück wenn das Menü nicht existiert")
    void updateMenu_putEndpoint_returns404IfNotFound() throws Exception {
        String unknownMenuId = "nicht-existierend-4711";
        MenuInputDTO inputDTO = new MenuInputDTO(
                "NonExistent",
                "3.00",
                List.of(),
                Map.of());

        String requestBody = objectMapper.writeValueAsString(inputDTO);

        mockMvc.perform(put("/api/menus/" + unknownMenuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Nested
    @DisplayName("PUT /positions")
    class UpdateMenuPositionsTests {
        @Test
        @DisplayName("PUT /positions sollte Menüs mit neuen Positionen aktualisieren und neue Liste zurückgeben")
        void updateMenuPositions_shouldUpdateMenuPositions_whenAllMenusExist() throws Exception {
            // Given
            List<SortedInputDTO> sortedInputDTOS = List.of(
                    new SortedInputDTO(0, menu4.getId(), null),
                    new SortedInputDTO(3, menu1.getId(), null),
                    new SortedInputDTO(1, menu2.getId(), null),
                    new SortedInputDTO(2, menu3.getId(), null)
            );

            mockMvc.perform(put("/api/menus/positions")

            // Then
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sortedInputDTOS)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(4)))
                    .andExpect(jsonPath("$[0].id").value(menu4.getId()))
                    .andExpect(jsonPath("$[1].id").value(menu2.getId()))
                    .andExpect(jsonPath("$[2].id").value(menu3.getId()))
                    .andExpect(jsonPath("$[3].id").value(menu1.getId()));

            // Verify database state
            assertThat(menuRepository.findById(menu4.getId()).orElseThrow().getPosition()).isZero();
            assertThat(menuRepository.findById(menu1.getId()).orElseThrow().getPosition()).isEqualTo(3);
            assertThat(menuRepository.findById(menu2.getId()).orElseThrow().getPosition()).isEqualTo(1);
            assertThat(menuRepository.findById(menu3.getId()).orElseThrow().getPosition()).isEqualTo(2);
        }

        @Test
        @DisplayName("PUT /positions sollte nur bestehende Menüs mit neuen Positionen aktualisieren und aktualisierte Liste zurückgeben")
        void updateMenuPositions_shouldUpdateMenuPositions_whenAllSomeMenusDontExistAnymore() throws Exception {
            // Given
            List<SortedInputDTO> sortedInputDTOS = List.of(
                    new SortedInputDTO(0, menu4.getId(), null),
                    new SortedInputDTO(3, menu1.getId(), null),
                    new SortedInputDTO(1, menu2.getId(), null),
                    new SortedInputDTO(2, menu3.getId(), null)
            );

            menuRepository.deleteById(menu4.getId()); // Beverage Menu 1 is not present anymore
            menuRepository.deleteById(menu2.getId()); // Main Menu 2 is not present anymore

            mockMvc.perform(put("/api/menus/positions")

            // Then
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sortedInputDTOS)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(menu3.getId()))
                    .andExpect(jsonPath("$[1].id").value(menu1.getId()));

            // Verify database state
            assertThat(menuRepository.findById(menu1.getId()).orElseThrow().getPosition()).isEqualTo(3);
            assertThat(menuRepository.findById(menu3.getId()).orElseThrow().getPosition()).isEqualTo(2);
        }

        @Test
        @DisplayName("PUT /positions sollte ein Menü mit neuen Positionen aktualisieren und HTTP 204 zurückgeben")
        void updateMenuPositions_shouldUpdateMenuPositions_whenMoreMenusExist() throws Exception {
            // Given
            List<SortedInputDTO> sortedInputDTOS = List.of(
                    new SortedInputDTO(0, menu4.getId(), null),
                    new SortedInputDTO(1, menu2.getId(), null),
                    new SortedInputDTO(2, menu3.getId(), null)
            );

            mockMvc.perform(put("/api/menus/positions")

            // Then
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sortedInputDTOS)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(4)))
                    .andExpect(jsonPath("$[0].id").value(menu1.getId()))
                    .andExpect(jsonPath("$[1].id").value(menu4.getId()))
                    .andExpect(jsonPath("$[2].id").value(menu2.getId()))
                    .andExpect(jsonPath("$[3].id").value(menu3.getId()));

            // Verify database state
            // it is intended that two menus are on position 0, this is just an intermediate state
            // and will be resolved by the frontend in the next request
            assertThat(menuRepository.findById(menu4.getId()).orElseThrow().getPosition()).isZero();
            assertThat(menuRepository.findById(menu1.getId()).orElseThrow().getPosition()).isZero();
            assertThat(menuRepository.findById(menu2.getId()).orElseThrow().getPosition()).isEqualTo(1);
            assertThat(menuRepository.findById(menu3.getId()).orElseThrow().getPosition()).isEqualTo(2);
        }
    }

}
