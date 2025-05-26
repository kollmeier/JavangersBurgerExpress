import {PropsWithChildren} from "react";
import {cn} from "@/util";
import {IconProp} from '@fortawesome/fontawesome-svg-core'
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {colorVariants, ColorVariantsType} from "@/data";

export type CircleProps = PropsWithChildren<{
    className?: string;
    icon?: IconProp;
    size?: "sm" | "md" | "lg";
    color?: ColorVariantsType;
}>

export function Circle({children, className, icon, size, color}: CircleProps) {
    const sizes = {
        sm: "w-10 h-10",
        md: "w-12 h-12",
        lg: "w-16 h-16"
    }
    return (
        <div className={cn("flex flex-col items-center", className)}>
            <div className={cn("rounded-full flex flex-col p-1", size ? sizes[size] : sizes.md, color ? colorVariants[color].normal : colorVariants.neutral.dark)}>
                {icon ? <FontAwesomeIcon icon={icon} className="grow-1"/> : children}
            </div>
            {children && icon && <div>{children}</div>}
        </div>
    )
}