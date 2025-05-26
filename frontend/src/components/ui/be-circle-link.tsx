import {Circle} from "@/components/ui/circle.tsx";
import {IconProp} from '@fortawesome/fontawesome-svg-core'
import {Link, LinkProps} from "react-router-dom";
import {cn} from "@/util";

export type BeCircleLinkProps = LinkProps &  {
    icon: IconProp
};

export function BeCircleLink({className, children, icon, ...props}: BeCircleLinkProps) {
    return (
        <Link className={cn("flex flex-col items-center justify-center", className)} {...props}>
            <Circle icon={icon} size="lg">{children}</Circle>
        </Link>
    )
}