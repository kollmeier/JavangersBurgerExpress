import type {MenuOutputDTO} from "@/types/MenuOutputDTO.ts";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faEdit,
    faGripLines,
    faRemove,
    faUtensils
} from "@fortawesome/free-solid-svg-icons";

import {useNavigate} from "react-router-dom";
import React from "react";
import Card from "@/components/shared/card.tsx";
import BeButton from "@/components/ui/be-button.tsx";
import {colorMapCards} from "@/data";
import {useSortable} from "@dnd-kit/sortable";
import {CSS} from "@dnd-kit/utilities";

const menuIcon = () => {
    return faUtensils;
}

type CardProps = {
    menu: MenuOutputDTO;

    onDelete: (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => void;
}

const DisplayItemCard = ({menu, onDelete}: CardProps) => {

    const navigate = useNavigate();
    /**
     * Wird aufgerufen, wenn der Bearbeiten-Button geklickt wird.
     */
    const handleEdit = () => {
        navigate(`/manage/menus/${menu.id}/edit`);
    }

    const {
        attributes,
        listeners,
        setNodeRef,
        transform,
        transition,
    } = useSortable({id: menu.id});

    const style = {
        transform: CSS.Transform.toString(transform),
        transition,
    };

    return (
        <Card
            ref={setNodeRef}
            style={style}
            header={menu.name}
            colorVariant={colorMapCards['menu']}
            actions={<>
                <BeButton variant="primary" onClick={handleEdit}><FontAwesomeIcon icon={faEdit}/> Bearbeiten</BeButton>
                <BeButton variant="danger" onClick={onDelete}><FontAwesomeIcon icon={faRemove}/> Löschen</BeButton>
            </>}
            typeCircle={<FontAwesomeIcon icon={menuIcon()} />}
            priceCircle={<div className="flex flex-col items-center"><span className="text-[0.6em] line-through">{menu.dishes.reduce((s,d) => s + parseFloat(d.price), 0).toFixed(2)}€</span>{menu.price.replace('.', ',')}€</div>}
            footer={<div className="flex flex-wrap gap-1">{menu.dishes.map(dish => <span key={menu.id + dish.id} className="not-last:after:content-[',_']">{dish.name}</span>)}</div>}
            topRight={<span className="menu-type" {...attributes} {...listeners}><FontAwesomeIcon icon={faGripLines} className="text-xl cursor-move" /></span>}
            >
            {menu.additionalInformation.description &&
                <span className={"menu-info menu-info__" + menu.additionalInformation.description.type.toLowerCase()}>
                    {menu.additionalInformation.description.displayString}
                </span>}
        </Card>
    );
}

export default DisplayItemCard;