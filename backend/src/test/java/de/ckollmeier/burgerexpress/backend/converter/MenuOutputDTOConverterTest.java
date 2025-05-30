package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.AdditionalInformationDTO;
import de.ckollmeier.burgerexpress.backend.dto.DishOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.model.PlainTextAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuOutputDTOConverterTest {

    @Test
    @DisplayName("Konvertiere Menu zu MenuOutputDTO (ohne AdditionalInformation und Dishes)")
    void convertMenuToOutputDTOWithoutAdditionalInformation() {
        Menu menu = Menu.builder()
                .id("abc1")
                .name("Risotto Menu")
                .price(new BigDecimal("11.00"))
                .build();

        MenuOutputDTO dto = MenuOutputDTOConverter.convert(menu);

        assertThat(dto.id()).isEqualTo("abc1");
        assertThat(dto.name()).isEqualTo("Risotto Menu");
        assertThat(dto.price()).isEqualTo("11.00");
        assertThat(dto.additionalInformation()).isEmpty();
        assertThat(dto.dishes()).isEmpty();
    }

    @Test
    @DisplayName("Konvertiere Menu zu MenuOutputDTO (mit AdditionalInformation)")
    void convertMenuToOutputDTOWithAdditionalInformation() {
        Map<String, PlainTextAdditionalInformation> info = new HashMap<>();
        info.put("hinweis", new PlainTextAdditionalInformation("Glutenfrei"));
        Menu menu = Menu.builder()
                .id("abc2")
                .name("Salat Menu")
                .price(new BigDecimal("6.50"))
                .additionalInformation(Collections.unmodifiableMap(info))
                .build();

        MenuOutputDTO dto = MenuOutputDTOConverter.convert(menu);

        assertThat(dto.id()).isEqualTo("abc2");
        assertThat(dto.name()).isEqualTo("Salat Menu");
        assertThat(dto.price()).isEqualTo("6.50");
        assertThat(dto.additionalInformation()).containsKey("hinweis");
        AdditionalInformationDTO additionalDTO = dto.additionalInformation().get("hinweis");
        assertThat(additionalDTO.type()).isEqualTo("PLAIN_TEXT");
        assertThat(additionalDTO.value()).isEqualTo("Glutenfrei");
    }

    @Test
    @DisplayName("Konvertiere Menu zu MenuOutputDTO (mit Dishes)")
    void convertMenuToOutputDTOWithDishes() {
        Map<String, PlainTextAdditionalInformation> info = new HashMap<>();
        info.put("hinweis", new PlainTextAdditionalInformation("Glutenfrei"));
        Menu menu = Menu.builder()
                .id("abc2")
                .name("Salat Menu")
                .price(new BigDecimal("6.50"))
                .dishes(List.of(Dish.builder()
                        .id("1")
                        .name("Pizza")
                        .price(new BigDecimal("8.90"))
                        .type(DishType.MAIN)
                        .build(),
                    Dish.builder()
                        .id("1")
                        .name("Salat 1")
                        .price(new BigDecimal("8.90"))
                        .type(DishType.SIDE)
                        .build(),
                    Dish.builder()
                        .id("1")
                        .name("Salat 2")
                        .price(new BigDecimal("8.90"))
                        .type(DishType.SIDE)
                        .build(),
                    Dish.builder()
                        .id("1")
                        .name("Cola")
                        .price(new BigDecimal("8.90"))
                        .type(DishType.BEVERAGE)
                        .build()
                ))
                .additionalInformation(Collections.unmodifiableMap(info))
                .build();

        MenuOutputDTO dto = MenuOutputDTOConverter.convert(menu);

        assertThat(dto.id()).isEqualTo("abc2");
        assertThat(dto.name()).isEqualTo("Salat Menu");
        assertThat(dto.price()).isEqualTo("6.50");
        assertThat(dto.additionalInformation()).containsKey("hinweis");
        assertThat(dto.dishes()).hasSize(4).extracting(DishOutputDTO::name)
                .containsExactlyInAnyOrder("Pizza", "Salat 1", "Salat 2", "Cola");
        AdditionalInformationDTO additionalDTO = dto.additionalInformation().get("hinweis");
        assertThat(additionalDTO.type()).isEqualTo("PLAIN_TEXT");
        assertThat(additionalDTO.value()).isEqualTo("Glutenfrei");
    }

    @Test
    @DisplayName("Konvertiere Liste von Menu zu Liste von MenuOutputDTO")
    void convertListOfMenusToDTOs() {
        Menu menu1 = Menu.builder()
                .id("1")
                .name("Pizza Menu")
                .price(new BigDecimal("8.90"))
                .additionalInformation(Collections.emptyMap())
                .build();
        Menu menu2 = Menu.builder()
                .id("2")
                .name("Bruschetta Menu")
                .price(new BigDecimal("4.50"))
                .additionalInformation(Collections.emptyMap())
                .build();

        List<Menu> menus = List.of(menu1, menu2);

        List<MenuOutputDTO> dtoList = MenuOutputDTOConverter.convert(menus);

        assertThat(dtoList).hasSize(2);
        assertThat(dtoList.get(0).name()).isEqualTo("Pizza Menu");
        assertThat(dtoList.get(1).name()).isEqualTo("Bruschetta Menu");
    }

    @Test
    @DisplayName("Konstruktor der Utility-Klasse ist privat und wirft Exception")
    void constructor_is_private_and_throws() throws Exception {
        var ctor = MenuOutputDTOConverter.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertThatThrownBy(ctor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("This is a utility class and cannot be instantiated");
    }
}