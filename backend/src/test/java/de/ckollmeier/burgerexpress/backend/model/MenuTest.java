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

class MenuTest {

    @Test
    void getImageUrls_shouldReturnEmptyList_whenNoImagesArePresentInDishes() {
        // Given
        Dish dish1 = Dish.builder()
                .name("Dish 1")
                .price(BigDecimal.TEN)
                .type(DishType.MAIN)
                .build();

        Dish dish2 = Dish.builder()
                .name("Dish 2")
                .price(BigDecimal.TEN)
                .type(DishType.MAIN)
                .build();

        Menu menu = Menu.builder()
                .name("Menu 1")
                .price(BigDecimal.TEN)
                .dishes(List.of(dish1, dish2))
                .build();

        // When
        Map<String, List<String>> imageUrls = menu.getImageUrls();

        // Then
        assertThat(imageUrls)
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @EnumSource(DishType.class)
    void getImageUrls_shouldReturnListWithOneElementAndTypeAsKey_whenOneImageUrlIsPresent(DishType type) {
        // Given
        Dish dish1 = Dish.builder()
                .name("Dish 1")
                .price(BigDecimal.TEN)
                .type(type)
                .imageUrl("http://test.de/image1.jpg")
                .build();

        Dish dish2 = Dish.builder()
                .name("Dish 2")
                .price(BigDecimal.TEN)
                .type(type)
                .build();

        var menu = Menu.builder()
                .name("Menu 2")
                .price(BigDecimal.TEN)
                .dishes(List.of(dish1, dish2))
                .build();

        // When
        Map<String, List<String>> imageUrls = menu.getImageUrls();

        // Then
        assertThat(imageUrls)
                .isNotNull()
                .hasSize(1)
                .containsKey(type.name())
                .extractingByKey(type.name(), as(InstanceOfAssertFactories.LIST))
                .hasSize(1)
                .containsExactly("http://test.de/image1.jpg");
    }

    @ParameterizedTest
    @EnumSource(DishType.class)
    void getImageUrls_shouldReturnListWithTwoElementAndTypeAsKey_whenTwoImageUrlsArePresent(DishType type) {
        // Given
        Dish dish1 = Dish.builder()
                .name("Dish 1")
                .price(BigDecimal.TEN)
                .type(type)
                .imageUrl("http://test.de/image1.jpg")
                .build();

        Dish dish2 = Dish.builder()
                .name("Dish 2")
                .price(BigDecimal.TEN)
                .type(type)
                .imageUrl("http://test.de/image2.jpg")
                .build();

        var menu = Menu.builder()
                .name("Menu 2")
                .price(BigDecimal.TEN)
                .dishes(List.of(dish1, dish2))
                .build();

        // When
        Map<String, List<String>> imageUrls = menu.getImageUrls();

        // Then
        assertThat(imageUrls)
                .isNotNull()
                .hasSize(1)
                .containsKey(type.name())
                .extractingByKey(type.name(), as(InstanceOfAssertFactories.LIST))
                .hasSize(2)
                .containsExactly("http://test.de/image1.jpg", "http://test.de/image2.jpg");
    }

    @Test
    void getImageUrls_shouldReturnListWithElementsSortedByType_whenImageUrlsWithDifferentTypesArePresent() {
        // Given
        Dish dish1 = Dish.builder()
                .name("Dish 1")
                .price(BigDecimal.TEN)
                .type(DishType.MAIN)
                .imageUrl("http://test.de/image1.jpg")
                .build();

        Dish dish2 = Dish.builder()
                .name("Dish 2")
                .price(BigDecimal.TEN)
                .type(DishType.MAIN)
                .imageUrl("http://test.de/image2.jpg")
                .build();

        Dish dish3 = Dish.builder()
                .name("Dish 3")
                .price(BigDecimal.TEN)
                .type(DishType.SIDE)
                .imageUrl("http://test.de/image3.jpg")
                .build();

        Dish dish4 = Dish.builder()
                .name("Dish 4")
                .price(BigDecimal.TEN)
                .type(DishType.SIDE)
                .imageUrl("http://test.de/image4.jpg")
                .build();

        Dish dish5 = Dish.builder()
                .name("Dish 5")
                .price(BigDecimal.TEN)
                .type(DishType.BEVERAGE)
                .imageUrl("http://test.de/image5.jpg")
                .build();

        var menu = Menu.builder()
                .name("Menu 2")
                .price(BigDecimal.TEN)
                .dishes(List.of(dish1, dish2, dish3, dish4, dish5))
                .build();

        // When
        Map<String, List<String>> imageUrls = menu.getImageUrls();

        // Then
        assertThat(imageUrls)
                .isNotNull()
                .hasSize(3)
                .containsKeys(DishType.MAIN.name(), DishType.SIDE.name(), DishType.BEVERAGE.name())
                .extractingByKey(DishType.MAIN.name(), as(InstanceOfAssertFactories.LIST))
                .containsExactly("http://test.de/image1.jpg", "http://test.de/image2.jpg");

        assertThat(imageUrls)
                .extractingByKey(DishType.SIDE.name(), as(InstanceOfAssertFactories.LIST))
                .containsExactly("http://test.de/image3.jpg", "http://test.de/image4.jpg");

        assertThat(imageUrls)
                .extractingByKey(DishType.BEVERAGE.name(), as(InstanceOfAssertFactories.LIST))
                .containsExactly("http://test.de/image5.jpg");
    }

    @Test
    void getOldPrice_shouldReturnPriceOfDish_whenOneDishIsPresent() {
        // Given
        Dish dish1 = Dish.builder()
                .name("Dish 1")
                .price(BigDecimal.TEN)
                .type(DishType.MAIN)
                .build();

        Menu menu = Menu.builder()
                .name("Menu 1")
                .price(BigDecimal.ONE)
                .dishes(List.of(dish1))
                .build();

        // When
        BigDecimal oldPrice = menu.getOldPrice();

        // Then
        assertThat(oldPrice).isEqualTo(BigDecimal.TEN);
    }
    @Test
    void getOldPrice_shouldReturnSumOfPrices_whenMultipleDishesArePresent() {
        // Given
        Dish dish1 = Dish.builder()
                .name("Dish 1")
                .price(BigDecimal.TEN)
                .type(DishType.MAIN)
                .build();

        Dish dish2 = Dish.builder()
                .name("Dish 2")
                .price(BigDecimal.ONE)
                .type(DishType.SIDE)
                .build();

        Dish dish3 = Dish.builder()
                .name("Dish 3")
                .price(BigDecimal.TWO)
                .type(DishType.BEVERAGE)
                .build();

        Menu menu = Menu.builder()
                .name("Menu 1")
                .price(BigDecimal.ONE)
                .dishes(List.of(dish1, dish2, dish3))
                .build();

        // When
        BigDecimal oldPrice = menu.getOldPrice();

        // Then
        assertThat(oldPrice).isEqualTo(new BigDecimal(13));
    }
}