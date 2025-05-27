package ckollmeier.de.backend.converter;

import ckollmeier.de.backend.dto.AdditionalInformationDTO;
import ckollmeier.de.backend.dto.DishInputDTO;
import ckollmeier.de.backend.model.Dish;
import ckollmeier.de.backend.model.PlainTextAdditionalInformation;
import ckollmeier.de.backend.types.DishType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class DishConverterTest {

    @Test
    @DisplayName("Konvertiere DishInputDTO zu Dish (ohne AdditionalInformation)")
    void convertDishInputDTOWithoutAdditionalInformation() {
        DishInputDTO dto = new DishInputDTO(
                "main",
                "Pizza",
                "9.99",
                Collections.emptyMap(),
                null
        );

        Dish dish = DishConverter.convert(dto);

        assertThat(dish.getType()).isEqualTo(DishType.MAIN);
        assertThat(dish.getName()).isEqualTo("Pizza");
        assertThat(dish.getPrice()).isEqualByComparingTo(new BigDecimal("9.99"));
        assertThat(dish.getAdditionalInformation()).isEmpty();
    }

    @Test
    @DisplayName("Konvertiere DishInputDTO mit unbekanntem Type (wirft Exception)")
    void convertDishInputDTOWithUnknownType_throwsIllegalArgumentException() {
        DishInputDTO dto = new DishInputDTO(
                "unknown",
                "Pizza",
                "9.99",
                Collections.emptyMap(),
                null
        );

        assertThatThrownBy(() -> DishConverter.convert(dto))
        .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Konvertiere DishInputDTO zu Dish (mit AdditionalInformation)")
    void convertDishInputDTOWithAdditionalInformation() {
        Map<String, AdditionalInformationDTO> infoMap = new HashMap<>();
        infoMap.put("hinweis", new AdditionalInformationDTO(
                "PLAIN_TEXT",
                "Vegan",
                "Vegan",
                "Veg."
        ));

        DishInputDTO dto = new DishInputDTO(
                "side",
                "Salat",
                "4.50",
                infoMap,
                null
        );

        Dish dish = DishConverter.convert(dto);

        assertThat(dish.getType()).isEqualTo(DishType.SIDE);
        assertThat(dish.getName()).isEqualTo("Salat");
        assertThat(dish.getPrice()).isEqualByComparingTo(new BigDecimal("4.50"));
        assertThat(dish.getAdditionalInformation()).containsKey("hinweis");
        assertThat(dish.getAdditionalInformation().get("hinweis"))
                .isInstanceOf(PlainTextAdditionalInformation.class);
        assertThat(((PlainTextAdditionalInformation) dish.getAdditionalInformation().get("hinweis")).value())
                .isEqualTo("Vegan");
    }

    @Test
    @DisplayName("Konstruktor ist privat und nicht instanziierbar")
    void constructor_is_private_and_throws() throws Exception {
        var ctor = DishConverter.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertThatThrownBy(ctor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("This is a utility class and cannot be instantiated");
    }
}