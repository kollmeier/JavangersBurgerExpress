package de.ckollmeier.burgerexpress.backend.interfaces;

public interface Sortable {
    String getId();
    int getPosition();
    <T extends Sortable> T withPosition(Integer position);
    int compareWith(Sortable other);
}
