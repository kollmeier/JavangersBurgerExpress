import type {DisplayCategoryOutputDTO} from "@/types/DisplayCategoryOutputDTO.ts";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faEdit, faFileCirclePlus,
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
import {cn, getIconElement} from "@/util";

type CardProps = {
    displayCategory: DisplayCategoryOutputDTO;
    onAddDisplayItemClicked: () => void;
    onDelete: (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => void;
    isDragging?: boolean;
}

const DisplayCategoryCard = ({displayCategory, onDelete, onAddDisplayItemClicked, isDragging}: CardProps) => {

    const navigate = useNavigate();
    /**
     * Wird aufgerufen, wenn der Bearbeiten-Button geklickt wird.
     */
    const handleEdit = () => {
        navigate(`/manage/displayItems/category/${displayCategory.id}/edit`);
    }

    const {
        attributes,
        listeners,
        setNodeRef,
        setActivatorNodeRef,
        transform,
        transition,
    } = useSortable({id: displayCategory.id});

    const style = {
        transform: CSS.Transform.toString(transform),
        transition,
    };

    return (
        <Card
            ref={setNodeRef}
            style={style}
            colorVariant={colorMapCards['displayCategory']}
            header={displayCategory.name}
            image={displayCategory.imageUrl && <img src={displayCategory.imageUrl + '?size=148'} alt={displayCategory.name} className="object-contain drop-shadow-lg max-h-22"/>}
            imageClassName={cn("place-self-end")}
            actions={<>
                <BeButton onClick={onAddDisplayItemClicked}><FontAwesomeIcon icon={faFileCirclePlus}/> Element hinzufügen</BeButton>
                <BeButton variant="primary" onClick={handleEdit}><FontAwesomeIcon icon={faEdit}/> Bearbeiten</BeButton>
                <BeButton variant="danger" onClick={onDelete}><FontAwesomeIcon icon={faRemove}/> Löschen</BeButton>
            </>}
            typeCircle={getIconElement("displayCategory")}
            topRight={<span className="displayCategory-type" ref={setActivatorNodeRef} {...attributes} {...listeners}><FontAwesomeIcon icon={faGripLines} className="text-xl cursor-move" /></span>}
            className={cn(
                "gap-y-1 gap-x-2",
                isDragging && "shadow-lg scale-105 z-10"
            )}
            headerClassName="row-span-1"
            childrenClassName="col-start-middle row-span-2 -mx-1.5 max-h-9"
            actionsClassName="col-span-4 mr-4"
            >
            {displayCategory.description &&
                <blockquote className={cn("text-sm text-gray-500 bg-white/10 mx-2 p-2 rounded-md shadow-xs relative",
                    "before:absolute before:-bottom-6 before:-left-2 before:content-[open-quote] before:text-3xl before:text-shadow-sm",
                    "after:absolute after:-top-2 after:-right-2 after:content-[close-quote] after:text-3xl after:text-shadow-sm",
                    "hover:bg-gray-50 max-h-9 hover:max-h-24 transition-[max-height,background-color] duration-300 ease-in-out]"
                )}>
                    <div className="not-hover:text-nowrap not-hover:text-ellipsis overflow-hidden hover:overflow-y-scroll max-h-20">{displayCategory.description}</div>
                </blockquote>}
        </Card>
    );
}

export default DisplayCategoryCard;
