import {cn} from "@/util";
import {Link, NavLink} from "react-router-dom";
import {buttonColors, ButtonVariantsType} from "@/components/ui/index.ts";

export type BeButtonLinkProps<T extends typeof NavLink | typeof Link = typeof Link> = React.ComponentPropsWithRef<T> & {
    as?: T
    variant?: ButtonVariantsType
};

const BeButtonLink = ({className, variant, as = Link, ...props}: BeButtonLinkProps) => {
    const As = as;
    return (
        <As {...props} className={cn("btn", variant ? buttonColors[variant] : buttonColors.neutral, className)} />
    )
}

export default BeButtonLink;