import type {DisplayItemOutputDTO} from "@/types/DisplayItemOutputDTO.ts";

import {useNavigate} from "react-router-dom";
import React from "react";
import Card, {CardProps} from "@/components/shared/card.tsx";
import BeButton from "@/components/ui/be-button.tsx";
import {colorMapCards} from "@/data";
import {cn, getColoredIconElement, getIconColor, getIconElement} from "@/util";
import {useSortable} from "@dnd-kit/react/sortable";
import DishImages from "@/components/ui/dish-images.tsx";
import {Grip, Pen, Trash} from "lucide-react";

export type DisplayItemCardProps = {
    displayItem: DisplayItemOutputDTO;
    className?: string,
    index: number;
    id: string;
    isDraggable?: boolean;
    onDelete: (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => void;
} & CardProps

const DisplayItemCard = ({
                             displayItem,
                             className,
                             id,
                             index,
                             isDraggable = false,
                             onDelete,
                             ...props}: DisplayItemCardProps) => {

    const navigate = useNavigate();
    /**
     * Wird aufgerufen, wenn der Bearbeiten-Button geklickt wird.
     */
    const handleEdit = () => {
        navigate(`/manage/displayItems/${displayItem.id}/edit`);
    }

    const {ref, isDragging, handleRef} = useSortable({
        id,
        index,
        type: "displayItem",
        accept: "displayItem",
        transition: {
            idle: true,
        },
        group: displayItem.categoryId,
    });

    const dragClassName = isDragging ? "drop-shadow-2xl scale-105 z-10" : "";

    return (
        <Card
            header={displayItem.name}
            ref={ref}
            data-dragging={isDragging}
            className={cn(dragClassName,
                className,
                "min-h-0 scale-y-100 transition-[transform, height, margin] duration-300 ease-in-out]",
                !isDraggable && "scale-y-0 h-0 m-0 p-0")}
            colorVariant={colorMapCards['displayItem']}
            actions={<>
                <BeButton variant="primary" onClick={handleEdit} icon={Pen}>Bearbeiten</BeButton>
                <BeButton variant="danger" onClick={onDelete} icon={Trash}></BeButton>
            </>}
            image={<DishImages
                className="w-full h-full top-0 object-contain scale-70"
                mainImages={displayItem.orderableItems.flatMap(o => o.imageUrls["MAIN"] ?? [])}
                sideImages={displayItem.orderableItems.flatMap(o => o.imageUrls["SIDE"] ?? [])}
                beverageImages={displayItem.orderableItems.flatMap(o => o.imageUrls["BEVERAGE"] ?? [])}
            />}
            imageClassName="w-full h-full"
            typeCircle={getIconElement('displayItem')}
            priceCircle={<div className="flex flex-col items-center">{displayItem.oldPrice && <span
                className="text-[0.6em] line-through">{displayItem.oldPrice.replace('.', ',')}€</span>}{displayItem.price.replace('.', ',')}€</div>}
            footer={<div className="flex flex-wrap gap-1">{displayItem.orderableItems.map(orderableItem =>
                <span key={displayItem.id + orderableItem.id} className={cn("pill !text-sm", getIconColor(orderableItem.type, "light"))}>
                    {getColoredIconElement(orderableItem.type, "bg-transparent")} {orderableItem.name}
                </span>)}</div>}
            topRight={isDraggable && <span className="displayItem-type" ref={handleRef}><Grip className="text-xl cursor-grab" /></span>}
            {...props}
            >
            {displayItem.description &&
                <blockquote className={cn("text-sm text-gray-500 bg-white/10 mx-2 p-2 rounded-md shadow-xs relative",
                    "before:absolute before:-bottom-6 before:-left-2 before:content-[open-quote] before:text-3xl before:text-shadow-sm",
                    "after:absolute after:-top-2 after:-right-2 after:content-[close-quote] after:text-3xl after:text-shadow-sm",
                    "hover:bg-gray-50 max-h-9 hover:max-h-24 transition-[max-height,background-color] duration-300 ease-in-out]"
                )}>
                    <div className="not-hover:text-nowrap not-hover:text-ellipsis overflow-hidden transition-[overflow] hover:delay-300 hover:overflow-y-scroll max-h-20">{displayItem.description}</div>
                </blockquote>}
        </Card>
    );
}

export default DisplayItemCard;
