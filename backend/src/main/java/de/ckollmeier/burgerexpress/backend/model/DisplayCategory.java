package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.interfaces.FindableItem;
import de.ckollmeier.burgerexpress.backend.interfaces.NamedItem;
import de.ckollmeier.burgerexpress.backend.interfaces.Sortable;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@With
@Document(collection = "burger-express-categories")
@TypeAlias("burger-express-category")
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class DisplayCategory implements Sortable, FindableItem, NamedItem {
    @Id
    private final String id;
    private final String name;
    private final String description;
    @ReadOnlyProperty
    @DocumentReference(lookup = "{'categoryId': ?#{#self._id}}")
    @Builder.Default
    List<DisplayItem> displayItems = new ArrayList<>();
    private final String imageUrl;
    @Builder.Default
    private final boolean published = false;
    @Builder.Default
    private final Instant createdAt = Instant.now();
    private final Instant updatedAt;

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
