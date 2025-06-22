import {cn} from "@/util";
import {buttonColors, ButtonVariantsType} from "@/components/ui/index.ts";
import React from "react";
import {Button, ButtonProps} from "@headlessui/react";
import {LucideIcon} from "lucide-react";


export type BeButtonProps = ButtonProps & {
    variant?: ButtonVariantsType,
    icon?: LucideIcon,
    iconClassName?: string,
};

const BeButton: React.FC<BeButtonProps> = ({
  className,
  variant,
  icon,
  iconClassName,
  children,
  ...props
}: BeButtonProps) => {

    const Icon = icon ?? "svg";

    const childrenWithIcon = typeof children === "function" ?
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        (bag: any) => <>
            {icon && <Icon className={cn({"mr-1": !!children}, iconClassName)}/>}
            {children(bag)}
        </> : <>
            {icon && <Icon className={cn({"mr-1": !!children}, iconClassName)}/>}
            {children}
        </>
    ;
    return (
        <Button children={childrenWithIcon} {...props} className={cn("btn disabled:opacity-15 disabled:pointer-events-none", variant ? buttonColors[variant] : buttonColors.neutral, className)} />
    )
}

export default BeButton;