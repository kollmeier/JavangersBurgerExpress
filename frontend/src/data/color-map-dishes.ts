import {ColorVariantsType} from "@/data/color-variants.ts";

export type ColorMapDishesType = keyof typeof colorMapDishes;

export const colorMapDishes: Record<string, ColorVariantsType> = {
    main: "red",
    side: "green",
    beverage: "blue",
}