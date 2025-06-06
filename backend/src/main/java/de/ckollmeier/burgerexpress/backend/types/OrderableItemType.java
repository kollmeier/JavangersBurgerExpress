package de.ckollmeier.burgerexpress.backend.types;

import lombok.Getter;

@Getter
public enum OrderableItemType {
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
    BEVERAGE("Beverage"),
    /**
     * Represents a menu.
     */
    MENU("Menu");

    /**
     * The description of the dish type.
     * -- GETTER --
     *  Returns the description of the dish type.

     */
    private final String description;

    OrderableItemType(final String description) {
        this.description = description;
    }

    public static OrderableItemType fromDishType(DishType dishType) {
        return switch (dishType) {
            case MAIN -> MAIN;
            case SIDE -> SIDE;
            case BEVERAGE -> BEVERAGE;
        };
    }
}
