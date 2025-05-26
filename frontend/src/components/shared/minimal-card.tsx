import {cn} from "@/util";
import {colorVariants, ColorVariantsType} from "@/data";
import {PropsWithChildren} from "react";

type MinimalCardProps = PropsWithChildren<{
    className?: string;
    colorVariant?: ColorVariantsType;
}>

const MinimalCard = ({colorVariant, className, children}: MinimalCardProps) => {
    return (
        <div className={cn("relative px-6 py-2 h-full min-w-xs min-h-32 flex flex-row justify-center align-stretch rounded-3xl drop-shadow-lg bg-gray-100 text-gray-700", colorVariant && colorVariants[colorVariant].normal, className)}>
            {children}
        </div>
    )
}

export default MinimalCard;