import {cn} from "@/util";
import {Link, NavLink} from "react-router-dom";
import {buttonColors, ButtonVariantsType} from "@/components/ui/index.ts";
import {LucideIcon} from "lucide-react";
import React from "react";

export type BeButtonLinkProps<T extends typeof NavLink | typeof Link = typeof Link> = React.ComponentPropsWithRef<T> & {
    as?: T
    icon?: LucideIcon,
    iconClassName?: string,
    variant?: ButtonVariantsType
};

const BeButtonLink = ({className, iconClassName, variant, children, icon, as = Link, ...props}: BeButtonLinkProps) => {
    const As = as;
    const Icon = icon ?? "svg";
    return (
        <As {...props} className={cn("btn", variant ? buttonColors[variant] : buttonColors.neutral, className)}>
            {icon && <Icon className={cn({"mr-1": !!children}, iconClassName)}/>}
            {children}
        </As>
    )
}

export default BeButtonLink;