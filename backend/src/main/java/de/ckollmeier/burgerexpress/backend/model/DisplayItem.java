package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.interfaces.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@With
@Document(collection = "burger-express-display-items")
@TypeAlias("burger-express-display-item")
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class DisplayItem implements Sortable, FindableItem, PricedItem, NamedItem {
    @Id
    private final String id;
    @NonNull
    private final ObjectId categoryId;
    @NonNull
    private final String name;
    private final String description;
    @Builder.Default
    private final BigDecimal actualPrice = null;
    @DBRef
    @Builder.Default
    private final List<OrderableItem> orderableItems = new ArrayList<>();
    @Builder.Default
    private final Integer position = 0;
    @Builder.Default
    private final boolean published = false;
    @Builder.Default
    private final Instant createdAt = Instant.now();
    private final Instant updatedAt;

    @Override
    public BigDecimal getPrice() {
        return getActualPrice() != null ? getActualPrice() : orderableItems.stream()
                .map(OrderableItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getOldPrice() {
        if (orderableItems.isEmpty()) {
            return null;
        }
        if (getActualPrice() == null) {
            if (orderableItems.stream().noneMatch(item -> item.getOldPrice() != null)) {
                return null;
            }
            return orderableItems.stream()
                    .map(item ->
                            item.getOldPrice() != null ?
                                    item.getOldPrice() :
                                    item.getPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return orderableItems.stream()
                .map(OrderableItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public int compareWith(Sortable other) {
        return this.getPosition() - other.getPosition();
    }
}
