package ckollmeier.de.backend.model;

import ckollmeier.de.backend.interfaces.AdditionalInformation;
import ckollmeier.de.backend.types.DishType;
import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Builder
@With
@Document(collection = "burger-express-dishes")
@TypeAlias("burger-express-dish")
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Dish {
    /**
     * Die eindeutige ID des Gerichts.
     */
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
    private final Map<String, AdditionalInformation<?>> additionalInformation = new HashMap<>();

    /**
     * Gibt die Position des Gerichts in einer sortierten Liste an.
     */
    @Builder.Default
    private final int position = 0;
}
