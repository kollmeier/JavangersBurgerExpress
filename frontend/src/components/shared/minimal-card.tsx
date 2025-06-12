import {cn} from "@/util";
import {colorVariants, ColorVariantsType} from "@/data";
import {ComponentPropsWithRef, PropsWithChildren} from "react";

export type MinimalCardProps = PropsWithChildren<{
    colorVariant?: ColorVariantsType;
    className?: string;
}> & ComponentPropsWithRef<"div">

const MinimalCard = ({colorVariant, className, children, ...props}: MinimalCardProps) => {
    return (
        <div
            className={cn("relative px-6 py-2 h-full min-w-xs min-h-32 rounded-3xl drop-shadow-lg bg-gray-100 text-gray-700", colorVariant && colorVariants[colorVariant].normal, className)}
            {...props}>
            {children}
        </div>
    )
}

export default MinimalCard;