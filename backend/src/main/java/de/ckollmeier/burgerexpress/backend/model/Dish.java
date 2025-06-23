package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.interfaces.*;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import de.ckollmeier.burgerexpress.backend.types.OrderableItemType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@With
@Document(collection = "burger-express-dishes")
@TypeAlias("burger-express-dish")
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Dish implements Sortable, FindableItem, PricedItem, NamedItem, OrderableItem, Serializable {
    /**
     * Die eindeutige ID des Gerichts.
     */
    @Id
    private final String id;

    /**
     * Der Name des Gerichts.
     */
    @NonNull
    private final String name;
    /**
     * Der Preis des Gerichts.
     */
    @NonNull
    private final BigDecimal price;
    /**
     * Der Typ des Gerichts.
     */
    @NonNull
    private final DishType type;
    /**
     * Eine Liste mit zusätzlichen Informationen zum Gericht.
     * Kann z.B. Allergene oder Zusatzstoffe enthalten.
     * Bei Getränken z.B. die Größe in Milliliter
     */
    @NonNull
    @Builder.Default
    private final Map<String, BaseAdditionalInformation> additionalInformation = new HashMap<>();

    private final String imageUrl;

    @Builder.Default
    private final Instant createdAt = Instant.now();

    private final Instant updatedAt;

    @Override
    public Map<String, List<String>> getImageUrls () {
        return imageUrl != null ? Map.of(type.name(), List.of(imageUrl)) : Map.of();
    }

    @Override
    public BigDecimal getOldPrice() {
        return null;
    }

    @Override
    public OrderableItemType getOrderableItemType() {
        return OrderableItemType.fromDishType(type);
    }

    /**
     * Gibt die Position des Gerichts in einer sortierten Liste an.
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

    @Override
    public List<OrderableItem> getSubItems() {
        return List.of();
    }
}
