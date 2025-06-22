import {PropsWithChildren} from "react";
import {cn} from "@/util";
import {colorVariants, ColorVariantsType} from "@/data";
import {LucideIcon, LucideProps} from "lucide-react";


type Props = PropsWithChildren<{
    className?: string;
    icon?: LucideIcon;
    size?: "sm" | "md" | "lg";
    color?: ColorVariantsType;
    iconSize?: number
}>

export type CircleProps = Props & Omit<LucideProps, keyof Props>

export function Circle({children, className, icon, size, color, iconSize, ...props}: CircleProps) {
    const sizes = {
        sm: "!w-10 !h-10",
        md: "!w-12 !h-12",
        lg: "!w-16 !h-16"
    }

    const Icon = icon ?? "svg";

    return (
        <div className={cn("flex flex-col items-center", className)}>
            <div className={cn("btn !rounded-full flex flex-col p-1", size ? sizes[size] : sizes.md, color ? colorVariants[color].normal : colorVariants.neutral.dark)}>
                {icon ? <Icon size={iconSize} className="flex-1 !mt-0" {...props} /> : children}
            </div>
            {children && icon && <div>{children}</div>}
        </div>
    )
}