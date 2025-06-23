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

    public static List<OrderStatus> getFinalStatuses() {
        return List.of(APPROVED, PAID, DELIVERED, CANCELLED);
    }

    public static List<OrderStatus> getKitchenStatuses() {
        return List.of(PAID, IN_PROGRESS);
    }

    public static List<OrderStatus> getImmutableStatuses() {
        return List.of(APPROVED, PAID, IN_PROGRESS, READY, DELIVERED, CANCELLED);
    }
}
