import {type PropsWithChildren, type ReactNode} from "react";
import {cn} from "@/util";
import {colorVariants, ColorVariantsType} from "@/data";
import MinimalCard from "@/components/shared/minimal-card.tsx";
type CardProps = Readonly<PropsWithChildren<{
    ref?: React.Ref<HTMLDivElement>;
    style?: React.CSSProperties;
    className?: string;
    colorVariant?: ColorVariantsType;
    header?: ReactNode;
    headerClassName?: string;
    typeCircle?: ReactNode;
    priceCircle?: ReactNode;
    topRight?: ReactNode;
    image?: ReactNode;
    imageClassName?: string;
    footer?: ReactNode;
    footerClassName?: string;
    actions?: ReactNode;
    actionsClassName?: string;
    childrenClassName?: string;
}>>

const Card = ({
    ref,
    style,
    className,
    colorVariant,
    header,
    headerClassName,
    typeCircle,
    priceCircle,
    topRight,
    image,
    imageClassName,
    footer,
    footerClassName,
    actions,
    actionsClassName,
    children,
    childrenClassName
}: CardProps) => {


    return (
        <MinimalCard
            ref={ref}
            style={style}
            colorVariant={colorVariant}
            className={cn("px-0 py-0 grid grid-cols-card grid-rows-card gap-4", !!typeCircle && "rounded-tl-circle-md", !!priceCircle && "rounded-br-circle-lg", className)}>
            {typeCircle && <div className={cn("mt-circle-offset-md ml-circle-offset-md row-start-head col-start-first w-circle-md h-circle-md rounded-circle-md flex items-center justify-center !text-tc bg-gray-300 text-gray-800", colorVariant && colorVariants[colorVariant].light)}>{typeCircle}</div>}
            {header && <div className={cn("row-start-head col-middle_end self-end pt-2 mr-4 text-lg font-medium", headerClassName)}>{header}</div>}
            {children && <div className={cn("row-start-content col-first_side ml-4 min-w-1", childrenClassName)}>{children}</div>}
            {image && <div className={cn("row-start-content col-side_end min-w-1 mr-4 self-end flex flex-row items-end", imageClassName)}>{image}</div>}
            {priceCircle && <div className={cn("mt-circle-offset-lg ml-circle-offset-lg row-foot_end col-last w-circle-lg h-circle-lg self-end rounded-circle-lg flex items-center justify-center !text-pc bg-gray-300 text-gray-800", colorVariant && colorVariants[colorVariant].dark)}>{priceCircle}</div>}
            {topRight && <div className="absolute top-2 right-4">{topRight}</div>}
            {footer && <div className={cn("row-start-foot col-first_last last:pb-2 ml-4", footerClassName)}>{footer}</div>}
            {actions && <div className={cn("row-start-actions col-first_last flex flex-row gap-4 justify-end self-end border-t py-2 ml-4 min-w-1", actionsClassName)}>{actions}</div>}
        </MinimalCard>
    )
}

export default Card;