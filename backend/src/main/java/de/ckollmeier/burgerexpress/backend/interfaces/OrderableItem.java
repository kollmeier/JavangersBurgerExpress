package de.ckollmeier.burgerexpress.backend.interfaces;

import de.ckollmeier.burgerexpress.backend.types.OrderableItemType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderableItem extends FindableItem {
    String getId();
    String getName();
    Map<String, AdditionalInformation<?>> getAdditionalInformation();
    Map<String, List<String>> getImageUrls();
    BigDecimal getPrice();
    BigDecimal getOldPrice();
    OrderableItemType getOrderableItemType();
}
