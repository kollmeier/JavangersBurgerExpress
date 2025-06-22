import type {DisplayCategoryOutputDTO} from "@/types/DisplayCategoryOutputDTO.ts";

import {useNavigate} from "react-router-dom";
import React from "react";
import Card from "@/components/shared/card.tsx";
import BeButton from "@/components/ui/be-button.tsx";
import {colorMapCards} from "@/data";
import {cn, getIconElement} from "@/util";
import {FileEdit, FilePlus, Trash, Grip} from "lucide-react";

type CardProps = {
    displayCategory: DisplayCategoryOutputDTO;
    onAddDisplayItemClicked: () => void;
    handleRef:  (element: (Element | null)) => void,
    onDelete: (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => void;
    isDragging?: boolean;
    isDraggable?: boolean;
}

const DisplayCategoryCard = ({
                                 displayCategory,
                                 handleRef,
                                 onDelete,
                                 onAddDisplayItemClicked,
                                 isDragging = false,
                                 isDraggable = false,
}: CardProps) => {

    const navigate = useNavigate();
    /**
     * Wird aufgerufen, wenn der Bearbeiten-Button geklickt wird.
     */
    const handleEdit = () => {
        navigate(`/manage/displayItems/category/${displayCategory.id}/edit`);
    }


    return (
        <Card
            colorVariant={colorMapCards['displayCategory']}
            header={displayCategory.name}
            image={displayCategory.imageUrl && <img src={displayCategory.imageUrl + '?size=148'} alt={displayCategory.name} className="object-contain drop-shadow-lg max-h-22"/>}
            imageClassName={cn("row-head_foot place-self-end")}
            actions={<>
                <BeButton onClick={onAddDisplayItemClicked}><FilePlus /> Element hinzufügen</BeButton>
                <BeButton variant="primary" onClick={handleEdit}><FileEdit /> Bearbeiten</BeButton>
                <BeButton variant="danger" onClick={onDelete}><Trash /></BeButton>
            </>}
            typeCircle={getIconElement("displayCategory")}
            topRight={isDraggable && <span ref={handleRef} className="displayCategory-type" ><Grip className="text-xl cursor-move" /></span>}
            className={cn(
                "gap-y-1 gap-x-2",
                isDragging && "shadow-xl scale-102 z-10"
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
