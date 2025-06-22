import {Circle, CircleProps} from "@/components/ui/circle.tsx";
import {Link} from "react-router-dom";
import {cn} from "@/util";
import {ComponentPropsWithRef} from "react";


export type BeCircleLinkProps = ComponentPropsWithRef<typeof Link> & Omit<CircleProps, "ref">;

export function BeCircleLink({className, children, icon, ref, ...props}: BeCircleLinkProps) {
    return (
        <Link className={cn("flex flex-col items-center justify-center", className)} ref={ref} {...props}>
            <Circle icon={icon} size="lg" {...props}>{children}</Circle>
        </Link>
    )
}