package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.DisplayItemInputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.BaseAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

class DisplayItemConverterTest {

    static class StubOrderableItem implements OrderableItem {
        private final String id;
        private final String name;
        private final BigDecimal price;

        StubOrderableItem(String id, String name, BigDecimal price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
        @Override public String getId() { return id; }
        @Override public String getName() { return name; }
        @Override public Map<String, BaseAdditionalInformation> getAdditionalInformation() { return Collections.emptyMap(); }
        @Override public Map<String, List<String>> getImageUrls() { return Collections.emptyMap(); }
        @Override public BigDecimal getPrice() { return price; }
        @Override public BigDecimal getOldPrice() { return null; }
        @Override public de.ckollmeier.burgerexpress.backend.types.OrderableItemType getOrderableItemType() { return null; }
    }

    @Test
    @DisplayName("Konvertiere DisplayItemInputDTO zu DisplayItem (mit Preis und Items)")
    void convertDisplayItemInputDTO_to_DisplayItem_withPriceAndOrderables() {
        DisplayItemInputDTO input = new DisplayItemInputDTO(
                "Test-Burger",
                "Leckerer Burger",
                true,
                "4.99",
                List.of("it1", "it2"),
                true,
                "000000000000000000000000"
        );
        Function<String, OrderableItem> resolver = id -> new StubOrderableItem(id, "Item-" + id, new BigDecimal("1.00"));

        DisplayItem result = DisplayItemConverter.convert(input, resolver);

        assertThat(result.getName()).isEqualTo("Test-Burger");
        assertThat(result.getDescription()).isEqualTo("Leckerer Burger");
        assertThat(result.getActualPrice()).isEqualTo(new BigDecimal("4.99"));
        assertThat(result.getOrderableItems()).hasSize(2)
                .extracting(OrderableItem::getId)
                .containsExactly("it1", "it2");
    }

    @Test
    @DisplayName("Konvertiere DisplayItemInputDTO zu DisplayItem (ohne Preis, leere Orderables)")
    void convertDisplayItemInputDTO_to_DisplayItem_withoutPrice() {
        DisplayItemInputDTO input = new DisplayItemInputDTO(
                "Test-Veggie",
                "Veggie Burger",
                null,
                null,
                Collections.emptyList(),
                true,
                "000000000000000000000000"
        );
        Function<String, OrderableItem> resolver = id -> new StubOrderableItem(id, "Sollte-nicht-aufgerufen-werden", BigDecimal.ZERO);

        DisplayItem result = DisplayItemConverter.convert(input, resolver);

        assertThat(result.getName()).isEqualTo("Test-Veggie");
        assertThat(result.getDescription()).isEqualTo("Veggie Burger");
        assertThat(result.getActualPrice()).isNull();
        assertThat(result.getOrderableItems()).isEmpty();
    }

    @Test
    @DisplayName("Konvertiere DisplayItemInputDTO zu DisplayItem (mit null orderableItemIds)")
    void convertDisplayItemInputDTO_to_DisplayItem_withNullOrderableItemIds() {
        DisplayItemInputDTO input = new DisplayItemInputDTO(
                "Test-Null-Items",
                "Burger mit null Items",
                true,
                "5.99",
                null,
                true,
                "000000000000000000000000"
        );
        Function<String, OrderableItem> resolver = id -> new StubOrderableItem(id, "Sollte-nicht-aufgerufen-werden", BigDecimal.ZERO);

        DisplayItem result = DisplayItemConverter.convert(input, resolver);

        assertThat(result.getName()).isEqualTo("Test-Null-Items");
        assertThat(result.getDescription()).isEqualTo("Burger mit null Items");
        assertThat(result.getActualPrice()).isEqualTo(new BigDecimal("5.99"));
        assertThat(result.getOrderableItems()).isNull();
    }

    @Test
    @DisplayName("Konvertiert und überschreibt nur geänderte Werte in bestehendem DisplayItem")
    void convertDisplayItemInputDTO_withExistingDisplayItem_overrideFields() {
        DisplayItem existing = DisplayItem.builder()
                .name("Altname")
                .description("Alter Burger")
                .categoryId(new org.bson.types.ObjectId("000000000000000000000000"))
                .actualPrice(new BigDecimal("8.50"))
                .orderableItems(Collections.emptyList())
                .build();

        DisplayItemInputDTO input = new DisplayItemInputDTO(
                null,
                "Neue Beschreibung",
                true,
                "6.00",
                List.of("o1"),
                true,
                "000000000000000000000000"
        );
        Function<String, OrderableItem> resolver = id -> new StubOrderableItem(id, "test", new BigDecimal("2.00"));

        DisplayItem result = DisplayItemConverter.convert(input, existing, resolver);

        assertThat(result.getName()).isEqualTo("Altname");
        assertThat(result.getDescription()).isEqualTo("Neue Beschreibung");
        assertThat(result.getActualPrice()).isEqualTo(new BigDecimal("6.00"));
        assertThat(result.getOrderableItems()).hasSize(1)
                .extracting(OrderableItem::getId)
                .containsExactly("o1");
    }

    @Test
    @DisplayName("Konstruktor der Utility-Klasse ist privat und wirft Exception")
    void constructor_is_private_and_throws() throws Exception {
        var ctor = DisplayItemConverter.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertThatThrownBy(ctor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("This is a utility class and cannot be instantiated");
    }
}
