package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.AdditionalInformationDTO;
import de.ckollmeier.burgerexpress.backend.dto.DishOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.PlainTextAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class DishOutputDTOConverterTest {

    @Test
    @DisplayName("Konvertiere Dish zu DishOutputDTO (ohne AdditionalInformation)")
    void convertDishToOutputDTOWithoutAdditionalInformation() {
        Dish dish = Dish.builder()
                .id("abc1")
                .name("Risotto")
                .price(new BigDecimal("11.00"))
                .type(DishType.MAIN)
                .additionalInformation(Collections.emptyMap())
                .build();

        DishOutputDTO dto = DishOutputDTOConverter.convert(dish);

        assertThat(dto.id()).isEqualTo("abc1");
        assertThat(dto.name()).isEqualTo("Risotto");
        assertThat(dto.price()).isEqualTo("11.00");
        assertThat(dto.type()).isEqualTo("main");
        assertThat(dto.additionalInformation()).isEmpty();
    }

    @Test
    @DisplayName("Konvertiere Dish zu DishOutputDTO (mit AdditionalInformation)")
    void convertDishToOutputDTOWithAdditionalInformation() {
        Map<String, PlainTextAdditionalInformation> info = new HashMap<>();
        info.put("hinweis", new PlainTextAdditionalInformation("Glutenfrei"));
        Dish dish = Dish.builder()
                .id("abc2")
                .name("Salat")
                .price(new BigDecimal("6.50"))
                .type(DishType.SIDE)
                .additionalInformation(Collections.unmodifiableMap(info))
                .build();

        DishOutputDTO dto = DishOutputDTOConverter.convert(dish);

        assertThat(dto.id()).isEqualTo("abc2");
        assertThat(dto.name()).isEqualTo("Salat");
        assertThat(dto.price()).isEqualTo("6.50");
        assertThat(dto.type()).isEqualTo("side");
        assertThat(dto.additionalInformation()).containsKey("hinweis");
        AdditionalInformationDTO additionalDTO = dto.additionalInformation().get("hinweis");
        assertThat(additionalDTO.type()).isEqualTo("PLAIN_TEXT");
        assertThat(additionalDTO.value()).isEqualTo("Glutenfrei");
    }

    @Test
    @DisplayName("Konvertiere Liste von Dish zu Liste von DishOutputDTO")
    void convertListOfDishesToDTOs() {
        Dish dish1 = Dish.builder()
                .id("1")
                .name("Pizza")
                .price(new BigDecimal("8.90"))
                .type(DishType.MAIN)
                .additionalInformation(Collections.emptyMap())
                .build();
        Dish dish2 = Dish.builder()
                .id("2")
                .name("Bruschetta")
                .price(new BigDecimal("4.50"))
                .type(DishType.SIDE)
                .additionalInformation(Collections.emptyMap())
                .build();

        List<Dish> dishes = List.of(dish1, dish2);

        List<DishOutputDTO> dtoList = DishOutputDTOConverter.convert(dishes);

        assertThat(dtoList).hasSize(2);
        assertThat(dtoList.get(0).name()).isEqualTo("Pizza");
        assertThat(dtoList.get(1).name()).isEqualTo("Bruschetta");
    }

    @Test
    @DisplayName("Konstruktor der Utility-Klasse ist privat und wirft Exception")
    void constructor_is_private_and_throws() throws Exception {
        var ctor = DishOutputDTOConverter.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertThatThrownBy(ctor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("This is a utility class and cannot be instantiated");
    }
}