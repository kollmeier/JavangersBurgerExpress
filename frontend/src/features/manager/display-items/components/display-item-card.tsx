import type {DisplayItemOutputDTO} from "@/types/DisplayItemOutputDTO.ts";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faEdit,
    faGripLines,
    faRemove,
} from "@fortawesome/free-solid-svg-icons";

import {useNavigate} from "react-router-dom";
import React from "react";
import Card from "@/components/shared/card.tsx";
import BeButton from "@/components/ui/be-button.tsx";
import {colorMapCards} from "@/data";
import {useSortable} from "@dnd-kit/sortable";
import {CSS} from "@dnd-kit/utilities";
import {cn, getColoredIconElement, getIconColor, getIconElement} from "@/util";

type CardProps = {
    displayItem: DisplayItemOutputDTO;

    onDelete: (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => void;
}

const DisplayItemCard = ({displayItem, onDelete}: CardProps) => {

    const navigate = useNavigate();
    /**
     * Wird aufgerufen, wenn der Bearbeiten-Button geklickt wird.
     */
    const handleEdit = () => {
        navigate(`/manage/displayItems/${displayItem.id}/edit`);
    }

    const {
        attributes,
        listeners,
        setNodeRef,
        setActivatorNodeRef,
        transform,
        transition,
    } = useSortable({id: displayItem.id});

    const style = {
        transform: CSS.Transform.toString(transform),
        transition,
    };

    return (
        <Card
            ref={setNodeRef}
            style={style}
            header={displayItem.name}
            colorVariant={colorMapCards['displayItem']}
            actions={<>
                <BeButton variant="primary" onClick={handleEdit}><FontAwesomeIcon icon={faEdit}/> Bearbeiten</BeButton>
                <BeButton variant="danger" onClick={onDelete}><FontAwesomeIcon icon={faRemove}/> Löschen</BeButton>
            </>}
            typeCircle={getIconElement('displayItem')}
            priceCircle={<div className="flex flex-col items-center">{displayItem.oldPrice && <span
                className="text-[0.6em] line-through">{displayItem.oldPrice.replace('.', ',')}€</span>}{displayItem.price.replace('.', ',')}€</div>}
            footer={<div className="flex flex-wrap gap-1">{displayItem.orderableItems.map(orderableItem =>
                <span key={displayItem.id + orderableItem.id} className={cn("pill !text-sm", getIconColor(orderableItem.type, "light"))}>
                    {getColoredIconElement(orderableItem.type, "bg-transparent")} {orderableItem.name}
                </span>)}</div>}
            topRight={<span className="displayItem-type" ref={setActivatorNodeRef} {...attributes} {...listeners}><FontAwesomeIcon icon={faGripLines} className="text-xl cursor-move" /></span>}
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
