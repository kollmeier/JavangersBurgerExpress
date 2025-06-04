import {ColorVariantsType} from "@/data/color-variants.ts";

export type ColorMapCardsType = keyof typeof colorMapCards;

export const colorMapCards: Record<string, ColorVariantsType> = {
    main: "red",
    side: "green",
    beverage: "blue",
    menu: "teal",
    displayCategory: "blue",
}