package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.OrderableItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.BaseAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.PlainTextAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.model.SizeInLiterAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.types.OrderableItemType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderableItemOutputDTOConverterTest {

    private static Locale originalLocale;

    @BeforeAll
    static void setUpLocale() {
        originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.GERMAN);
    }

    @AfterAll
    static void restoreLocale() {
        Locale.setDefault(originalLocale);
    }

    @Test
    @DisplayName("Konvertiere OrderableItem zu OrderableItemOutputDTO (ohne AdditionalInformation und ohne alte Preise)")
    void convertOrderableItemWithoutAdditionalInformationAndOldPrice() {
        TestOrderableItem item = new TestOrderableItem(
                "oid1", "Cola", null, new BigDecimal("2.50"),
                OrderableItemType.BEVERAGE,
                Collections.emptyMap(),
                Collections.singletonMap("default", Arrays.asList("img1.jpg", "img2.jpg"))
        );

        OrderableItemOutputDTO dto = OrderableItemOutputDTOConverter.convert(item);

        assertThat(dto.id()).isEqualTo("oid1");
        assertThat(dto.name()).isEqualTo("Cola");
        assertThat(dto.oldPrice()).isNull();
        assertThat(dto.price()).isEqualTo("2.50");
        assertThat(dto.type()).isEqualTo("beverage");
        assertThat(dto.imageUrls()).containsKey("default");
        assertThat(dto.imageUrls().get("default")).containsExactly("img1.jpg", "img2.jpg");
        assertThat(dto.descriptionForDisplay()).isEmpty();
        assertThat(dto.descriptionForCart()).isEmpty();
    }

    @Test
    @DisplayName("Konvertiere OrderableItem zu OrderableItemOutputDTO (mit AdditionalInformation, Beschreibung und Größe)")
    void convertOrderableItemWithDescriptionsAndSizes() {
        Map<String, BaseAdditionalInformation> info = new HashMap<>();
        info.put("description", new PlainTextAdditionalInformation("leckeres Getränk"));
        info.put("size", new SizeInLiterAdditionalInformation(BigDecimal.ONE));
        TestOrderableItem item = new TestOrderableItem(
                "oid2", "Fanta", new BigDecimal("3.00"), new BigDecimal("2.30"),
                OrderableItemType.BEVERAGE, info,
                Collections.singletonMap("BEVERAGE", List.of("fanta.jpg"))
        );

        OrderableItemOutputDTO dto = OrderableItemOutputDTOConverter.convert(item);

        assertThat(dto.id()).isEqualTo("oid2");
        assertThat(dto.name()).isEqualTo("Fanta");
        assertThat(dto.oldPrice()).isEqualTo("3.00");
        assertThat(dto.price()).isEqualTo("2.30");
        assertThat(dto.type()).isEqualTo("beverage");
        assertThat(dto.imageUrls()).containsKey("BEVERAGE");
        assertThat(dto.imageUrls().get("BEVERAGE")).containsExactly("fanta.jpg");
        assertThat(dto.descriptionForDisplay()).containsExactly("leckeres Getränk", "Inhalt: 1,0 Liter");
        assertThat(dto.descriptionForCart()).containsExactly("leckeres Getränk", "1,0l");
    }

    @Test
    @DisplayName("Konvertiere Liste von OrderableItem zu Liste von OrderableItemOutputDTO")
    void convertListOfOrderableItemsToDTOs() {
        TestOrderableItem item1 = new TestOrderableItem(
                "1", "Burger", null, new BigDecimal("4.99"),
                OrderableItemType.MAIN, Collections.emptyMap(),
                Collections.singletonMap("platte", List.of("burger.jpg"))
        );
        TestOrderableItem item2 = new TestOrderableItem(
                "2", "Pommes", new BigDecimal("2.80"), new BigDecimal("2.30"),
                OrderableItemType.SIDE, Collections.emptyMap(),
                Collections.singletonMap("schale", List.of("pommes.jpg"))
        );
        List<OrderableItem> list = List.of(item1, item2);

        List<OrderableItemOutputDTO> dtoList = OrderableItemOutputDTOConverter.convert(list);

        assertThat(dtoList).hasSize(2);
        assertThat(dtoList.get(0).name()).isEqualTo("Burger");
        assertThat(dtoList.get(1).name()).isEqualTo("Pommes");
    }

    @Test
    @DisplayName("Konstruktor der Utility-Klasse ist privat und wirft Exception")
    void constructor_is_private_and_throws() throws Exception {
        var ctor = OrderableItemOutputDTOConverter.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertThatThrownBy(ctor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("This is a utility class and cannot be instantiated");
    }

    /**
     * Hilfsklasse zum Mocken von OrderableItem für die Tests.
     */
    static class TestOrderableItem implements OrderableItem {
        private final String id;
        private final String name;
        private final BigDecimal oldPrice;
        private final BigDecimal price;
        private final OrderableItemType type;
        private final Map<String, BaseAdditionalInformation> additionalInformation;
        private final Map<String, List<String>> imageUrls;

        TestOrderableItem(String id, String name, BigDecimal oldPrice, BigDecimal price, OrderableItemType type,
                          Map<String, BaseAdditionalInformation> additionalInformation,
                          Map<String, List<String>> imageUrls) {
            this.id = id;
            this.name = name;
            this.oldPrice = oldPrice;
            this.price = price;
            this.type = type;
            this.additionalInformation = additionalInformation;
            this.imageUrls = imageUrls;
        }

        @Override
        public String getId() { return id; }

        @Override
        public String getName() { return name; }

        @Override
        public BigDecimal getOldPrice() { return oldPrice; }

        @Override
        public BigDecimal getPrice() { return price; }

        @Override
        public OrderableItemType getOrderableItemType() { return type; }

        @Override
        public Map<String, BaseAdditionalInformation> getAdditionalInformation() { return additionalInformation; }

        @Override
        public Map<String, List<String>> getImageUrls() { return imageUrls; }
    }
}
