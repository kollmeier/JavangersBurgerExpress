package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.interfaces.AdditionalInformation;
import de.ckollmeier.burgerexpress.backend.interfaces.Sortable;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@With
@Document(collection = "burger-express-menus")
@TypeAlias("burger-express-menu")
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Menu implements Sortable {
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
    private final List<Dish> mainDishes;
    /**
     * Die Beilagen des Menüs.
     */
    @DBRef
    private final List<Dish> sideDishes;
    /**
     * Die Getränke des Menüs.
     */
    @DBRef
    private final List<Dish> beverages;

    /**
     * Eine Liste mit zusätzlichen Informationen zum Menü.
     * Kann z.B. Allergene oder Zusatzstoffe enthalten.
     */
    @NonNull
    @Builder.Default
    private final Map<String, AdditionalInformation<?>> additionalInformation = new HashMap<>();

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
