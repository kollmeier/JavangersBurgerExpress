package de.ckollmeier.burgerexpress.backend.types;

import lombok.Getter;

@Getter
public enum DishType {
    /**
     * Represents a main dish.
     */
    MAIN("Main Dish"),
    /**
     * Represents a side dish.
     */
    SIDE("Side Dish"),
    /**
     * Represents a beverage.
     */
    BEVERAGE("Beverage");

    /**
     * The description of the dish type.
     * -- GETTER --
     *  Returns the description of the dish type.

     */
    private final String description;

    DishType(final String description) {
        this.description = description;
    }
}
