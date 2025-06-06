import {cn} from "@/util";
import {Link, LinkProps} from "react-router-dom";
import {buttonColors, ButtonVariantsType} from "@/components/ui/index.ts";

export type BeButtonLinkProps = LinkProps & {
    variant?: ButtonVariantsType
};

const BeButtonLink = ({className, variant, ...props}: BeButtonLinkProps) => {
    return (
        <Link {...props} className={cn("btn", variant ? buttonColors[variant] : buttonColors.neutral, className)} />
    )
}

export default BeButtonLink;