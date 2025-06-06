package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.types.DishType;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

class DishTest {

    @Test
    void getImageUrls_shouldReturnEmptyList_whenNoImagesArePresent() {
        // Given
        Dish dish = Dish.builder()
                .name("Test")
                .price(BigDecimal.TEN)
                .type(DishType.MAIN)
                .build();

        // When
        Map<String, List<String>> imageUrls = dish.getImageUrls();

        // Then
        assertThat(imageUrls)
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @EnumSource(DishType.class)
    void getImageUrls_shouldReturnListWithOneElementAndTypeAsKey_whenImageUrlIsPresent(DishType type) {
        // Given
        Dish dish = Dish.builder()
                .name("Test")
                .price(BigDecimal.TEN)
                .type(type)
                .imageUrl("http://test.de/image.jpg")
                .build();

        // When
        Map<String, List<String>> imageUrls = dish.getImageUrls();

        // Then
        assertThat(imageUrls)
                .isNotNull()
                .hasSize(1)
                .containsKey(type.name())
                .extractingByKey(type.name(), as(InstanceOfAssertFactories.LIST))
                .hasSize(1)
                .containsExactly("http://test.de/image.jpg");
    }

    @Test
    void getOldPrice_shouldReturnNull() {
        // Given
        Dish dish = Dish.builder()
                .name("Test")
                .price(BigDecimal.TEN)
                .type(DishType.MAIN)
                .build();

        // When
        BigDecimal oldPrice = dish.getOldPrice();

        // Then
        assertThat(oldPrice).isNull();
    }
}