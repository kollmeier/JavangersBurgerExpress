package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.AdditionalInformationDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuInputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.model.PlainTextAdditionalInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MenuConverterTest {

    @Test
    @DisplayName("Konvertiere MenuInputDTO zu Menu (ohne AdditionalInformation)")
    void convertMenuInputDTOWithoutAdditionalInformation() {
        MenuInputDTO dto = new MenuInputDTO(
                "Pizza Menu",
                "9.99",
                List.of("1", "2", "3"),
                Collections.emptyMap()
        );

        Function<String, Dish> resolveDish = (String dishId) -> {
            Dish dish = mock(Dish.class);
            when(dish.getId()).thenReturn(dishId);
            return dish;
        };

        Menu menu = MenuConverter.convert(dto, resolveDish);

        assertThat(menu.getName()).isEqualTo("Pizza Menu");
        assertThat(menu.getPrice()).isEqualByComparingTo(new BigDecimal("9.99"));
        assertThat(menu.getDishes()).hasSize(3).extracting(Dish::getId).containsExactlyInAnyOrder("1", "2", "3");
        assertThat(menu.getPosition()).isZero();
        assertThat(menu.getAdditionalInformation()).isEmpty();
    }

    @Test
    @DisplayName("Konvertiere MenuInputDTO zu Menu (mit AdditionalInformation)")
    void convertMenuInputDTOWithAdditionalInformation() {
        Map<String, AdditionalInformationDTO> infoMap = new HashMap<>();
        infoMap.put("hinweis", new AdditionalInformationDTO(
                "PLAIN_TEXT",
                "Vegan",
                "Vegan",
                "Veg."
        ));

        MenuInputDTO dto = new MenuInputDTO(
                "Salat",
                "4.50",
                List.of(),
                infoMap
        );

        Menu menu = MenuConverter.convert(dto, id -> null);

        assertThat(menu.getName()).isEqualTo("Salat");
        assertThat(menu.getPrice()).isEqualByComparingTo(new BigDecimal("4.50"));
        assertThat(menu.getAdditionalInformation()).containsKey("hinweis");
        assertThat(menu.getAdditionalInformation().get("hinweis"))
                .isInstanceOf(PlainTextAdditionalInformation.class);
        assertThat(((PlainTextAdditionalInformation) menu.getAdditionalInformation().get("hinweis")).value())
                .isEqualTo("Vegan");
    }

    @Test
    @DisplayName("Konstruktor ist privat und nicht instanziierbar")
    void constructor_is_private_and_throws() throws Exception {
        var ctor = MenuConverter.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertThatThrownBy(ctor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("This is a utility class and cannot be instantiated");
    }
}