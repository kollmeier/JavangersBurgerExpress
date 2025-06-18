package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.interfaces.*;
import de.ckollmeier.burgerexpress.backend.types.OrderableItemType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@With
@Document(collection = "burger-express-menus")
@TypeAlias("burger-express-menu")
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Menu implements Sortable, FindableItem, PricedItem, NamedItem, OrderableItem, Serializable {
    /**
     * Die eindeutige ID des Menüs.
     */
    @Id
    private final String id;
    /**
     * Der Name des Menüs.
     */
    @NonNull
    private final String name;
    /**
     * Der Preis des Menüs.
     */
    @NonNull
    private final BigDecimal price;
    /**
     * Die Hauptgerichte des Menüs.
     */
    @DBRef
    @NonNull
    @Builder.Default
    private final List<Dish> dishes = new ArrayList<>();

    /**
     * Eine Liste mit zusätzlichen Informationen zum Menü.
     * Kann z.B. Allergene oder Zusatzstoffe enthalten.
     */
    @NonNull
    @Builder.Default
    private final Map<String, BaseAdditionalInformation> additionalInformation = new HashMap<>();

    @Builder.Default
    private final Instant createdAt = Instant.now();
    private final Instant updatedAt;

    @Override
    public Map<String, List<String>> getImageUrls () {
        return dishes.stream()
                .flatMap(dish -> dish.getImageUrls().entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (list1, list2) -> {
                            List<String> merged = new ArrayList<>(list1);
                            merged.addAll(list2);
                            return merged;
                        }

                ));
    }

    @Override
    public BigDecimal getOldPrice() {
        return dishes.stream()
                .map(Dish::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public OrderableItemType getOrderableItemType() {
        return OrderableItemType.MENU;
    }

    /**
     * Gibt die Position des Menüs in einer sortierten Liste an.
     */
    @Builder.Default
    private final Integer position = 0;

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public int compareWith(final Sortable other) {
        return this.getPosition() - other.getPosition();
    }
}
