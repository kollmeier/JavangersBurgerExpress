package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.AdditionalInformationDTO;
import de.ckollmeier.burgerexpress.backend.dto.DishInputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.PlainTextAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    @DisplayName("Konvertiere DishInputDTO zu bestehendem Dish (Felder werden aktualisiert)")
    void convertDishInputDTOUpdatesExistingDish() {
        // Arrangement: vorhandenes Dish-Objekt mit anderen Werten
        Dish existing = Dish.builder()
                .id("x1")
                .name("Altname")
                .price(new BigDecimal("2.50"))
                .type(DishType.SIDE)
                .additionalInformation(Collections.emptyMap())
                .build();

        Map<String, AdditionalInformationDTO> infoMap = new HashMap<>();
        infoMap.put("test", new AdditionalInformationDTO(
            "PLAIN_TEXT", "nur Test", null, null
        ));

        DishInputDTO dto = new DishInputDTO(
                "main",
                "Schnitzel",
                "13.99",
                infoMap,
                null
        );

        // Act: konvertieren, aber mit bestehendem Dish
        Dish updated = DishConverter.convert(dto, existing);

        // Assert: Das geänderte Objekt enthält nun Werte aus dem DTO
        assertThat(updated.getId()).isEqualTo("x1");
        assertThat(updated.getType()).isEqualTo(DishType.MAIN);
        assertThat(updated.getName()).isEqualTo("Schnitzel");
        assertThat(updated.getPrice()).isEqualByComparingTo(new BigDecimal("13.99"));
        assertThat(updated.getAdditionalInformation()).containsKey("test");
        assertThat(updated.getAdditionalInformation().get("test")).isInstanceOf(PlainTextAdditionalInformation.class);
        assertThat(((PlainTextAdditionalInformation) updated.getAdditionalInformation().get("test")).value())
                .isEqualTo("nur Test");
    }

    @Test
    @DisplayName("Konvertiere DishInputDTO zu bestehendem Dish (ohne AdditionalInformation, behält alte Infos)")
    void convertDishInputDTOUpdatesExistingDishAndRemovesAdditionalInformation() {
        Map<String, PlainTextAdditionalInformation> altesInfo = new HashMap<>();
        altesInfo.put("hinweis", new PlainTextAdditionalInformation("vorher"));

        Dish existing = Dish.builder()
                .id("x2")
                .name("Old")
                .price(new BigDecimal("1.99"))
                .type(DishType.SIDE)
                .additionalInformation(new HashMap<>(altesInfo))
                .build();

        DishInputDTO dto = new DishInputDTO(
                "main",
                "Frisch",
                "7.77",
                Collections.emptyMap(),
                null
        );

        Dish updated = DishConverter.convert(dto, existing);

        assertThat(updated.getType()).isEqualTo(DishType.MAIN);
        assertThat(updated.getName()).isEqualTo("Frisch");
        assertThat(updated.getPrice()).isEqualByComparingTo(new BigDecimal("7.77"));
        assertThat(updated.getAdditionalInformation()).isNotEmpty();
    }

    @Test
    @DisplayName("Konvertiert null-DishInputDTO auf bestehendes Dish gibt IllegalArgumentException")
    void convertNullDishInputDTOWithExistingDish_throwsException() {
        Dish existierendesDish = Dish.builder()
                .id("x3")
                .name("Name")
                .price(new BigDecimal("1.00"))
                .type(DishType.SIDE)
                .additionalInformation(Collections.emptyMap())
                .build();

        //noinspection DataFlowIssue
        assertThatThrownBy(() -> DishConverter.convert(null, existierendesDish))
                .isInstanceOf(NullPointerException.class);
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

    @Test
    @DisplayName("Konvertiert einzelne Dish-ID zu Dish per Resolver")
    void convertDishIdToDish_referenceReturned() {
        Dish dummyDish = mock(Dish.class);
        when(dummyDish.getId()).thenReturn("ab42");

        Dish result = DishConverter.convert("ab42", id -> {
            if ("ab42".equals(id)) return dummyDish;
            return null;
        });

        assertThat(result).isSameAs(dummyDish);
        assertThat(result.getId()).isEqualTo("ab42");
    }

    @Test
    @DisplayName("Konvertiert Liste von Dish-IDs zu Liste von Dishes per Resolver")
    void convertDishIdsListToDishList_referenceListReturned() {
        Dish dish1 = mock(Dish.class); when(dish1.getId()).thenReturn("id1");
        Dish dish2 = mock(Dish.class); when(dish2.getId()).thenReturn("id2");
        Dish dish3 = mock(Dish.class); when(dish3.getId()).thenReturn("id3");

        var ids = java.util.List.of("id1", "id2", "id3");
        var resolver = (java.util.function.Function<String, Dish>) id -> switch (id) {
            case "id1" -> dish1;
            case "id2" -> dish2;
            case "id3" -> dish3;
            default -> null;
        };

        var result = DishConverter.convert(ids, resolver);

        assertThat(result).containsExactly(dish1, dish2, dish3);
    }

    @Test
    @DisplayName("Konvertieren mit ungültiger ID liefert null-Eintrag")
    void convertWithInvalidIdYieldsNull() {
        var ids = java.util.List.of("foo", "bar");
        var resolver = (java.util.function.Function<String, Dish>) id -> null;

        var result = DishConverter.convert(ids, resolver);

        assertThat(result).containsOnlyNulls();
    }

    @Test
    @DisplayName("Konvertieren einer leeren Liste von IDs gibt leere Liste zurück")
    void convertEmptyListOfIdsReturnsEmptyList() {
        var result = DishConverter.convert(Collections.emptyList(), id -> null);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Konvertiert Dish-ID mit dishResolver, der null zurückgibt, liefert null")
    void convertDishIdWithNullResolverResultYieldsNull() {
        Dish result = DishConverter.convert("nix", id -> null);
        assertThat(result).isNull();
    }
}