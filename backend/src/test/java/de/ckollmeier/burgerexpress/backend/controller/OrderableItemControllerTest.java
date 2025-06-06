package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.repository.MenuRepository;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderableItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private MenuRepository menuRepository;

    private Dish dish1;
    private Dish dish2;
    private Menu menu1;

    @BeforeEach
    void setUp() {
        // Clean repositories
        dishRepository.deleteAll();
        menuRepository.deleteAll();

        // Create test dishes
        dish1 = dishRepository.save(Dish.builder()
                .name("Burger")
                .price(new BigDecimal("5.99"))
                .type(DishType.MAIN)
                .position(0)
                .build());

        dish2 = dishRepository.save(Dish.builder()
                .name("Fries")
                .price(new BigDecimal("2.99"))
                .type(DishType.SIDE)
                .position(1)
                .build());

        // Create test menu with dishes
        List<Dish> menuDishes = new ArrayList<>();
        menuDishes.add(dish1);
        menuDishes.add(dish2);

        menu1 = menuRepository.save(Menu.builder()
                .name("Burger Menu")
                .price(new BigDecimal("7.99"))
                .dishes(menuDishes)
                .position(0)
                .build());
    }

    @AfterEach
    void tearDown() {
        // Clean repositories
        menuRepository.deleteAll();
        dishRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/orderable-items should return all orderable items")
    void getAllOrderableItems_shouldReturnAllItems() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/orderable-items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(dish1.getId()))
                .andExpect(jsonPath("$[0].name").value("Burger"))
                .andExpect(jsonPath("$[0].price").value("5.99"))
                .andExpect(jsonPath("$[1].id").value(dish2.getId()))
                .andExpect(jsonPath("$[1].name").value("Fries"))
                .andExpect(jsonPath("$[1].price").value("2.99"))
                .andExpect(jsonPath("$[2].id").value(menu1.getId()))
                .andExpect(jsonPath("$[2].name").value("Burger Menu"))
                .andExpect(jsonPath("$[2].price").value("7.99"));
    }

    @Test
    @DisplayName("GET /api/orderable-items/menus should return all menus")
    void getAllMenus_shouldReturnAllMenus() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/orderable-items/menus"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(menu1.getId()))
                .andExpect(jsonPath("$[0].name").value("Burger Menu"))
                .andExpect(jsonPath("$[0].price").value("7.99"));
    }

    @Test
    @DisplayName("GET /api/orderable-items/dishes should return all dishes")
    void getAllDishes_shouldReturnAllDishes() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/orderable-items/dishes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(dish1.getId()))
                .andExpect(jsonPath("$[0].name").value("Burger"))
                .andExpect(jsonPath("$[0].price").value("5.99"))
                .andExpect(jsonPath("$[1].id").value(dish2.getId()))
                .andExpect(jsonPath("$[1].name").value("Fries"))
                .andExpect(jsonPath("$[1].price").value("2.99"));
    }
}