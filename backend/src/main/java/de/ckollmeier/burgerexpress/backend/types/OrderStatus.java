package de.ckollmeier.burgerexpress.backend.types;

import java.util.List;

public enum OrderStatus {
    PENDING,
    CHECKOUT,
    APPROVING,
    APPROVED,
    PAID,
    IN_PROGRESS,
    READY,
    DELIVERED,
    CANCELLED;

    public boolean isFinal() {
        return getFinalStatuses().contains(this);
    }

    public boolean isImmutable() {
        return getImmutableStatuses().contains(this);
    }

    public boolean isKitchen() {
        return getKitchenStatuses().contains(this);
    }

    public static List<OrderStatus> getFinalStatuses() {
        return List.of(APPROVED, PAID, DELIVERED, CANCELLED);
    }

    public static List<OrderStatus> getKitchenStatuses() {
        return List.of(PAID, IN_PROGRESS);
    }

    public static List<OrderStatus> getImmutableStatuses() {
        return List.of(APPROVED, PAID, IN_PROGRESS, READY, DELIVERED, CANCELLED);
    }

    public OrderStatus advancedStatus() {
        return switch (this) {
           case PENDING -> CHECKOUT;
           case CHECKOUT -> APPROVING;
           case APPROVING -> APPROVED;
           case APPROVED -> PAID;
           case PAID -> IN_PROGRESS;
           case IN_PROGRESS -> READY;
           case READY -> DELIVERED;
           case DELIVERED -> DELIVERED;
           case CANCELLED -> CANCELLED;
        };
    }
}
