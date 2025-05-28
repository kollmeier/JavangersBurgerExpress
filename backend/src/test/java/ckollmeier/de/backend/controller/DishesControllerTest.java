package ckollmeier.de.backend.controller;

import ckollmeier.de.backend.dto.DishInputDTO;
import ckollmeier.de.backend.dto.SortedInputDTO;
import ckollmeier.de.backend.model.Dish;
import ckollmeier.de.backend.model.SizeInLiterAdditionalInformation;
import ckollmeier.de.backend.repository.DishRepository;
import ckollmeier.de.backend.types.AdditionalInformationType;
import ckollmeier.de.backend.types.DishType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest // Lädt den gesamten Spring-Kontext
@AutoConfigureMockMvc // Konfiguriert MockMvc für HTTP-Anfragen
class DishesControllerTest {

    @Autowired
    private MockMvc mockMvc; // Zum Senden von HTTP-Anfragen

    @Autowired
    private DishRepository dishRepository; // Zum Vorbereiten/Überprüfen der DB

    @Autowired
    private ObjectMapper objectMapper; // Zum Konvertieren von Objekten in JSON

    private Dish mainDish1;
    private Dish mainDish2;
    private Dish sideDish1;
    private Dish beverageDish1;

    @BeforeEach
    void setUp() {
        // Testdaten vor jedem Test erstellen und speichern
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

        sideDish1 = dishRepository.save(Dish.builder()
                .name("Fries")
                .price(new BigDecimal("3.50"))
                .type(DishType.SIDE)
                .position(2)
                .build());

        beverageDish1 = dishRepository.save(Dish.builder()
                .name("Cola")
                .price(new BigDecimal("2.50"))
                .type(DishType.BEVERAGE)
                .additionalInformation(Map.of(AdditionalInformationType.SIZE_IN_LITER.name(), new SizeInLiterAdditionalInformation(new BigDecimal("0.5"))))
                .position(3)
                .build());
    }

    @AfterEach
    void tearDown() {
        // Nach jedem Test aufräumen (optional, aber gute Praxis)
        dishRepository.deleteAll();
    }

    // --- Tests für /api/dishes ---
    @Test
    @DisplayName("GET /api/dishes should return all dishes")
    void getAllDishes_shouldReturnAllDishes() throws Exception {
        // Given: Daten sind in setUp gespeichert

        // When & Then
        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4))) // Erwarte 4 Gerichte insgesamt
                .andExpect(jsonPath("$[0].id").value(mainDish1.getId()))
                .andExpect(jsonPath("$[1].id").value(mainDish2.getId()))
                .andExpect(jsonPath("$[2].id").value(sideDish1.getId()))
                .andExpect(jsonPath("$[3].id").value(beverageDish1.getId()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"main", "side", "beverage"})
    @DisplayName("POST /api/dishes should add a new dish and return it")
    void addDish_shouldAddDishAndReturnIt(final String typeAsString) throws Exception {
        // Given
        DishType type = DishType.valueOf(typeAsString.toUpperCase());
        DishInputDTO newDishInput = new DishInputDTO(typeAsString,"Veggie Burger", "10.99", Map.of(), "test-image-url.jpg");
        String newDishJson = objectMapper.writeValueAsString(newDishInput);

        long initialCount = dishRepository.count();

        // When
        MvcResult result = mockMvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newDishJson))
                // Then
                .andExpect(status().isCreated()) // HTTP 201
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty()) // ID sollte generiert worden sein
                .andExpect(jsonPath("$.name", is("Veggie Burger")))
                .andExpect(jsonPath("$.price", is("10.99")))
                .andExpect(jsonPath("$.imageUrl", is("test-image-url.jpg")))
                .andReturn();

        // Verify Database state
        assertThat(dishRepository.count()).isEqualTo(initialCount + 1);
        // Extrahiere die ID aus der Antwort, um das erstellte Objekt zu überprüfen
        String responseBody = result.getResponse().getContentAsString();
        String createdId = objectMapper.readTree(responseBody).get("id").asText();
        Dish savedDish = dishRepository.findById(createdId).orElseThrow();
        assertThat(savedDish.getName()).isEqualTo("Veggie Burger");
        assertThat(savedDish.getPrice()).isEqualTo(new BigDecimal("10.99"));
        assertThat(savedDish.getType()).isEqualTo(type);
    }

    @Test
    @DisplayName("PUT /{dishId} aktualisiert ein Gericht und gibt aktualisiertes DishOutputDTO zurück")
    void updateDish_putEndpoint_returnsUpdatedDish() throws Exception {
        // Erstellt zuerst ein Gericht oder stelle sicher, dass ein bekanntes Gericht existiert
        String dishId = mainDish1.getId();
        DishInputDTO inputDTO = new DishInputDTO(
                DishType.MAIN.name(),
                "Integration Pasta",
                "9.99",
                Map.of(),
                "test-image-url.jpg"
        );

        String requestBody = objectMapper.writeValueAsString(inputDTO);

        // PUT-Request
        mockMvc.perform(put("/api/dishes/" + dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(dishId))
                .andExpect(jsonPath("$.name").value("Integration Pasta"))
                .andExpect(jsonPath("$.price").value("9.99"))
                .andExpect(jsonPath("$.imageUrl").value("test-image-url.jpg"))
                .andExpect(jsonPath("$.type").value(DishType.MAIN.toString().toLowerCase()));
    }

    @Nested
    @DisplayName("DELETE /{dishId}")
    class DeleteDishTests {

        @Test
        @DisplayName("DELETE /{dishId} sollte ein Gericht löschen und HTTP 204 zurückgeben")
        void deleteDish_shouldRemoveDish_whenDishExists() throws Exception {
            // Given
            String dishIdToDelete = mainDish1.getId();
            long initialCount = dishRepository.count();

            // When & Then
            mockMvc.perform(delete("/api/dishes/" + dishIdToDelete))
                    .andExpect(status().isNoContent()); // HTTP 204

            // Verify database state
            assertThat(dishRepository.count()).isEqualTo(initialCount - 1);
            assertThat(dishRepository.findById(dishIdToDelete)).isEmpty();
        }

        @Test
        @DisplayName("DELETE /{dishId} sollte HTTP 400 zurückgeben, wenn das Gericht nicht existiert")
        void deleteDish_shouldReturn404_whenDishDoesNotExist() throws Exception {
            // Given
            String nonExistentDishId = "non-existent-12345";

            // When & Then
            mockMvc.perform(delete("/api/dishes/" + nonExistentDishId))
                    .andExpect(status().isBadRequest()); // HTTP 400
        }
    }

    @Test
    @DisplayName("PUT /{dishId} gibt 400 zurück wenn das Gericht nicht existiert")
    void updateDish_putEndpoint_returns400IfNotFound() throws Exception {
        String unknownDishId = "nicht-existierend-4711";
        DishInputDTO inputDTO = new DishInputDTO(
                DishType.SIDE.name(),
                "NonExistent",
                "3.00",
                Map.of(),
                null
        );

        String requestBody = objectMapper.writeValueAsString(inputDTO);

        mockMvc.perform(put("/api/dishes/" + unknownDishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Nested
    @DisplayName("PUT /positions")
    class UpdateDishPositionsTests {
        @Test
        @DisplayName("PUT /positions sollte Gerichte mit neuen Positionen aktualisieren und neue Liste zurückgeben")
        void updateDishPositions_shouldUpdateDishPositions_whenAllDishesExist() throws Exception {
            // Given
            List<SortedInputDTO> sortedInputDTOS = List.of(
                    new SortedInputDTO(0, beverageDish1.getId()),
                    new SortedInputDTO(3, mainDish1.getId()),
                    new SortedInputDTO(1, mainDish2.getId()),
                    new SortedInputDTO(2, sideDish1.getId())
            );

            mockMvc.perform(put("/api/dishes/positions")

            // Then
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sortedInputDTOS)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(4)))
                    .andExpect(jsonPath("$[0].id").value(beverageDish1.getId()))
                    .andExpect(jsonPath("$[1].id").value(mainDish2.getId()))
                    .andExpect(jsonPath("$[2].id").value(sideDish1.getId()))
                    .andExpect(jsonPath("$[3].id").value(mainDish1.getId()));

            // Verify database state
            assertThat(dishRepository.findById(beverageDish1.getId()).orElseThrow().getPosition()).isZero();
            assertThat(dishRepository.findById(mainDish1.getId()).orElseThrow().getPosition()).isEqualTo(3);
            assertThat(dishRepository.findById(mainDish2.getId()).orElseThrow().getPosition()).isEqualTo(1);
            assertThat(dishRepository.findById(sideDish1.getId()).orElseThrow().getPosition()).isEqualTo(2);
        }

        @Test
        @DisplayName("PUT /positions sollte nur bestehende Gerichte mit neuen Positionen aktualisieren und aktualisierte Liste zurückgeben")
        void updateDishPositions_shouldUpdateDishPositions_whenAllSomeDishesDontExistAnymore() throws Exception {
            // Given
            List<SortedInputDTO> sortedInputDTOS = List.of(
                    new SortedInputDTO(0, beverageDish1.getId()),
                    new SortedInputDTO(3, mainDish1.getId()),
                    new SortedInputDTO(1, mainDish2.getId()),
                    new SortedInputDTO(2, sideDish1.getId())
            );

            dishRepository.deleteById(beverageDish1.getId()); // Beverage Dish 1 is not present anymore
            dishRepository.deleteById(mainDish2.getId()); // Main Dish 2 is not present anymore

            mockMvc.perform(put("/api/dishes/positions")

            // Then
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sortedInputDTOS)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(sideDish1.getId()))
                    .andExpect(jsonPath("$[1].id").value(mainDish1.getId()));

            // Verify database state
            assertThat(dishRepository.findById(mainDish1.getId()).orElseThrow().getPosition()).isEqualTo(3);
            assertThat(dishRepository.findById(sideDish1.getId()).orElseThrow().getPosition()).isEqualTo(2);
        }

        @Test
        @DisplayName("PUT /positions sollte ein Gericht mit neuen Positionen aktualisieren und HTTP 204 zurückgeben")
        void updateDishPositions_shouldUpdateDishPositions_whenMoreDishesExist() throws Exception {
            // Given
            List<SortedInputDTO> sortedInputDTOS = List.of(
                    new SortedInputDTO(0, beverageDish1.getId()),
                    new SortedInputDTO(1, mainDish2.getId()),
                    new SortedInputDTO(2, sideDish1.getId())
            );

            mockMvc.perform(put("/api/dishes/positions")

            // Then
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sortedInputDTOS)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(4)))
                    .andExpect(jsonPath("$[0].id").value(mainDish1.getId()))
                    .andExpect(jsonPath("$[1].id").value(beverageDish1.getId()))
                    .andExpect(jsonPath("$[2].id").value(mainDish2.getId()))
                    .andExpect(jsonPath("$[3].id").value(sideDish1.getId()));

            // Verify database state
            // it is intended that two dishes are on position 0, this is just an intermediate state
            // and will be resolved by the frontend in the next request
            assertThat(dishRepository.findById(beverageDish1.getId()).orElseThrow().getPosition()).isZero();
            assertThat(dishRepository.findById(mainDish1.getId()).orElseThrow().getPosition()).isZero();
            assertThat(dishRepository.findById(mainDish2.getId()).orElseThrow().getPosition()).isEqualTo(1);
            assertThat(dishRepository.findById(sideDish1.getId()).orElseThrow().getPosition()).isEqualTo(2);
        }
    }

}