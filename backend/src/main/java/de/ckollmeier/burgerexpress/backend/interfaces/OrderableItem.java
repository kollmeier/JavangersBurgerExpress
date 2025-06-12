package de.ckollmeier.burgerexpress.backend.interfaces;

import de.ckollmeier.burgerexpress.backend.types.OrderableItemType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderableItem extends FindableItem, Serializable {
    String getId();
    String getName();
    Map<String, BaseAdditionalInformation> getAdditionalInformation();
    Map<String, List<String>> getImageUrls();
    BigDecimal getPrice();
    BigDecimal getOldPrice();
    OrderableItemType getOrderableItemType();
}
