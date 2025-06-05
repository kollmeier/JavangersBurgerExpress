package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.DisplayItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderableItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.AdditionalInformation;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.types.OrderableItemType;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class DisplayItemOutputDTOConverterTest {

    // Stub für OrderableItem (da es ein Interface ist)
    static class StubOrderableItem implements OrderableItem {
        private final String id;
        private final String name;
        private final BigDecimal price;
        private final BigDecimal oldPrice;
        private final OrderableItemType type;

        public StubOrderableItem(String id, String name, BigDecimal price, BigDecimal oldPrice, OrderableItemType type) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.oldPrice = oldPrice;
            this.type = type;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Map<String, AdditionalInformation<?>> getAdditionalInformation() {
            return Collections.emptyMap();
        }

        @Override
        public Map<String, List<String>> getImageUrls() {
            return Collections.emptyMap();
        }

        @Override
        public BigDecimal getPrice() {
            return price;
        }

        @Override
        public BigDecimal getOldPrice() {
            return oldPrice;
        }

        @Override
        public OrderableItemType getOrderableItemType() {
            return type;
        }
    }

    @Test
    @DisplayName("Konvertiere DisplayItem zu DisplayItemOutputDTO (mit actualPrice und OrderableItems)")
    void convertDisplayItemToOutputDTOWithoutOldPriceAndOrderables() {
        DisplayItem displayItem = DisplayItem.builder()
                .id("item1")
                .categoryId(new ObjectId())
                .name("Burger Classic")
                .description("Ein klassischer Rindfleisch-Burger")
                .orderableItems(Collections.emptyList())
                .actualPrice(new BigDecimal("5.99"))
                .published(true)
                .build();

        DisplayItemOutputDTO dto = DisplayItemOutputDTOConverter.convert(displayItem);

        assertThat(dto.id()).isEqualTo("item1");
        assertThat(dto.name()).isEqualTo("Burger Classic");
        assertThat(dto.description()).isEqualTo("Ein klassischer Rindfleisch-Burger");
        assertThat(dto.orderableItems()).isEmpty();
        assertThat(dto.price()).isEqualTo("5.99");
        assertThat(dto.oldPrice()).isNull();
        assertThat(dto.published()).isTrue();
    }

    @Test
    @DisplayName("Konvertiere DisplayItem zu DisplayItemOutputDTO (mit oldPrice und OrderableItems)")
    void convertDisplayItemToOutputDTOWithOldPriceAndOrderableItems() {
        StubOrderableItem orderableItem1 = new StubOrderableItem(
                "o1",
                "Pommes",
                new BigDecimal("2.30"),
                null,
                OrderableItemType.SIDE);
        StubOrderableItem orderableItem2 = new StubOrderableItem(
                "o2",
                "Cola",
                new BigDecimal("1.50"),
                new BigDecimal("1.60"),
                OrderableItemType.BEVERAGE);

        DisplayItem displayItem = DisplayItem.builder()
                .id("item2")
                .categoryId(new ObjectId())
                .name("Cheese Burger")
                .description("Burger mit Käse")
                .orderableItems(List.of(orderableItem1, orderableItem2))
                .actualPrice(new BigDecimal("3.49"))
                .published(false)
                .build();

        DisplayItemOutputDTO dto = DisplayItemOutputDTOConverter.convert(displayItem);

        assertThat(dto.id()).isEqualTo("item2");
        assertThat(dto.name()).isEqualTo("Cheese Burger");
        assertThat(dto.description()).isEqualTo("Burger mit Käse");
        assertThat(dto.orderableItems()).hasSize(2)
                .extracting(OrderableItemOutputDTO::name)
                .containsExactlyInAnyOrder("Pommes", "Cola");
        assertThat(dto.price()).isEqualTo("3.49");
        assertThat(dto.oldPrice()).isEqualTo("3.80");
        assertThat(dto.published()).isFalse();
    }

    @Test
    @DisplayName("Konvertiere Liste von DisplayItems zu Liste von DisplayItemOutputDTO")
    void convertListOfDisplayItemsToDTOs() {
        DisplayItem item1 = DisplayItem.builder()
                .id("item1")
                .categoryId(new ObjectId())
                .name("Hot Dog")
                .description("Würstchen im Brötchen")
                .orderableItems(Collections.emptyList())
                .actualPrice(new BigDecimal("1.99"))
                .published(true)
                .build();

        DisplayItem item2 = DisplayItem.builder()
                .id("item2")
                .categoryId(new ObjectId())
                .name("Veggie Burger")
                .description("Vegetarischer Burger")
                .orderableItems(Collections.emptyList())
                .actualPrice(new BigDecimal("2.99"))
                .published(true)
                .build();

        List<DisplayItem> displayItems = List.of(item1, item2);
        List<DisplayItemOutputDTO> dtoList = DisplayItemOutputDTOConverter.convert(displayItems);

        assertThat(dtoList).hasSize(2);
        assertThat(dtoList.get(0).name()).isEqualTo("Hot Dog");
        assertThat(dtoList.get(1).name()).isEqualTo("Veggie Burger");
    }

    @Test
    @DisplayName("Konstruktor der Utility-Klasse ist privat und wirft Exception")
    void constructor_is_private_and_throws() throws Exception {
        var ctor = DisplayItemOutputDTOConverter.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertThatThrownBy(ctor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("This is a utility class and cannot be instantiated");
    }
}
