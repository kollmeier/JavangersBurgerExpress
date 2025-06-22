import {colorMapCards, ColorMapCardsType, colorVariants} from "@/data";
import {ColorVariantStyle} from "@/data/color-variants.ts";
import {cn} from "@/util/cn.ts";
import {CircleQuestionMark, CupSoda, Hamburger, Layers, Salad, StickyNote, Utensils} from "lucide-react";

export function getIcon(type: string) {
    switch (type) {
        case 'main': {
            return Hamburger;
        }
        case 'side': {
            return Salad;
        }
        case 'beverage': {
            return CupSoda;
        }
        case 'menu': {
            return Utensils;
        }
        case 'displayCategory': {
            return Layers;
        }
        case 'displayItem': {
            return StickyNote;
        }
        default: {
            return CircleQuestionMark;
        }
    }
}

export function getIconColor(type: ColorMapCardsType, variant: ColorVariantStyle = "normal") {
    return colorVariants[colorMapCards[type] ?? "neutral"][variant];
}

export function getIconElement(type: string, className?: string) {
    const Icon = getIcon((type));
    return <Icon className={className} />
}

export function getColoredIconElement(type: string, className?: string) {
    return getIconElement(type, cn(getIconColor(type), className));
}