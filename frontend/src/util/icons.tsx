import {
    faBowlFood,
    faBurger,
    faFile,
    faFileCircleQuestion,
    faFolderOpen,
    faGlassWater,
    faUtensils
} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {colorMapCards, ColorMapCardsType, colorVariants} from "@/data";
import {ColorVariantStyle} from "@/data/color-variants.ts";
import {cn} from "@/util/cn.ts";

export function getIcon(type: string) {
    switch (type) {
        case 'main': {
            return faBurger;
        }
        case 'side': {
            return faBowlFood;
        }
        case 'beverage': {
            return faGlassWater;
        }
        case 'menu': {
            return faUtensils;
        }
        case 'displayCategory': {
            return faFolderOpen;
        }
        case 'displayItem': {
            return faFile;
        }
        default: {
            return faFileCircleQuestion;
        }
    }
}

export function getIconColor(type: ColorMapCardsType, variant: ColorVariantStyle = "normal") {
    return colorVariants[colorMapCards[type] ?? "neutral"][variant];
}

export function getIconElement(type: string, className?: string) {
    return <FontAwesomeIcon className={className} icon={getIcon(type)} />
}

export function getColoredIconElement(type: string, className?: string) {
    return getIconElement(type, cn(getIconColor(type), className));
}