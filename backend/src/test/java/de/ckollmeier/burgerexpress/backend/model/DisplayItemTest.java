package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.interfaces.BaseAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.types.OrderableItemType;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DisplayItemTest: Testfälle für DisplayItem (Preise und alte Preise)")
class DisplayItemTest {

    @Nested
    @DisplayName("getPrice: Preisermittlung für DisplayItem")
    class GetPrice {

        @Test
        @DisplayName("Soll tatsächlichen Preis (actualPrice) zurückgeben, wenn gesetzt")
        void getPrice_shouldReturnActualPrice_whenActualPriceIsSet() {
            // Given
            DisplayItem displayItem = DisplayItem.builder()
                    .id("id")
                    .categoryId(new ObjectId())
                    .name("Test")
                    .actualPrice(new BigDecimal("9.99"))
                    .orderableItems(Collections.emptyList())
                    .build();

            // When
            BigDecimal price = displayItem.getPrice();

            // Then
            assertThat(price).isEqualByComparingTo("9.99");
        }

        @Test
        @DisplayName("Soll Summe der Einzelpreise liefern, wenn actualPrice null ist")
        void getPrice_shouldReturnSumOfOrderableItemPrice_whenActualPriceIsNull() {
            // Given
            OrderableItem item1 = new OrderableItemStub(new BigDecimal("3.50"), null);
            OrderableItem item2 = new OrderableItemStub(new BigDecimal("2.20"), null);
            DisplayItem displayItem = DisplayItem.builder()
                    .id("id")
                    .categoryId(new ObjectId())
                    .name("Test")
                    .actualPrice(null)
                    .orderableItems(List.of(item1, item2))
                    .build();

            // When
            BigDecimal price = displayItem.getPrice();

            // Then
            assertThat(price).isEqualByComparingTo(item1.getPrice().add(item2.getPrice()));
        }
    }

    @Nested
    @DisplayName("getOldPrice: Alte Preise ermitteln")
    class GetOldPrice {

        @Test
        @DisplayName("Soll Summe der alten Einzelpreise liefern, wenn alle OrderableItems alte Preise haben")
        void getOldPrice_shouldReturnSumOfOldPrices_whenOrderableItemsHaveOldPrices() {
            // Given
            OrderableItem item1 = new OrderableItemStub(new BigDecimal("3.50"), new BigDecimal("5.00"));
            OrderableItem item2 = new OrderableItemStub(new BigDecimal("2.20"), new BigDecimal("3.00"));
            DisplayItem displayItem = DisplayItem.builder()
                    .id("id")
                    .categoryId(new ObjectId())
                    .name("Test")
                    .actualPrice(null)
                    .orderableItems(List.of(item1, item2))
                    .build();

            // When
            BigDecimal oldPrice = displayItem.getOldPrice();

            // Then
            assertThat(oldPrice).isEqualByComparingTo("8.00");
        }

        @Test
        @DisplayName("Soll null zurückgeben, wenn kein OrderableItem einen alten Preis hat")
        void getOldPrice_shouldReturnNull_whenNoOrderableItemHasOldPrice() {
            // Given
            OrderableItem item1 = new OrderableItemStub(new BigDecimal("3.50"), null);
            OrderableItem item2 = new OrderableItemStub(new BigDecimal("2.20"), null);
            DisplayItem displayItem = DisplayItem.builder()
                    .id("id")
                    .categoryId(new ObjectId())
                    .name("Test")
                    .actualPrice(null)
                    .orderableItems(List.of(item1, item2))
                    .build();

            // When
            BigDecimal oldPrice = displayItem.getOldPrice();

            // Then
            assertThat(oldPrice).isNull();
        }

        @Test
        @DisplayName("Soll Summe aus Einzelpreis und altem Preis liefern, wenn teils alte Preise vorhanden sind")
        void getOldPrice_shouldReturnSumOfPriceAndOldPrice_whenSomeOrderableItemHaveOldPrices() {
            // Given
            OrderableItem item1 = new OrderableItemStub(new BigDecimal("3.50"), BigDecimal.TEN);
            OrderableItem item2 = new OrderableItemStub(new BigDecimal("2.20"), null);
            DisplayItem displayItem = DisplayItem.builder()
                    .id("id")
                    .categoryId(new ObjectId())
                    .name("Test")
                    .actualPrice(null)
                    .orderableItems(List.of(item1, item2))
                    .build();

            // When
            BigDecimal oldPrice = displayItem.getOldPrice();

            // Then
            assertThat(oldPrice).isEqualTo(item2.getPrice().add(BigDecimal.TEN));
        }
    }

    // Einfaches Test-Stub für OrderableItem
    @DisplayName("Stub für OrderableItem")
    static class OrderableItemStub implements OrderableItem {
        private final BigDecimal price;
        private final BigDecimal oldPrice;

        OrderableItemStub(BigDecimal price, BigDecimal oldPrice) {
            this.price = price;
            this.oldPrice = oldPrice;
        }

        @Override
        public BigDecimal getPrice() {
            return price;
        }

        @Override
        public BigDecimal getOldPrice() {
            return oldPrice;
        }

        // die folgenden Methoden werden für die Tests nicht benötigt
        @Override public String getId() { return null; }
        @Override public String getName() { return null; }
        @Override public OrderableItemType getOrderableItemType() { return null; }
        @Override public Map<String, BaseAdditionalInformation> getAdditionalInformation() { return null; }
        @Override public Map<String, List<String>> getImageUrls() { return null; }
    }
}
