import type {DishOutputDTO} from "@/types/DishOutputDTO.ts";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faBowlFood,
    faBurger,
    faEdit,
    faGlassWater, faGripLines,
    faRemove,
    faUtensils
} from "@fortawesome/free-solid-svg-icons";

import {useNavigate} from "react-router-dom";
import React from "react";
import Card from "@/components/shared/card.tsx";
import BeButton from "@/components/ui/be-button.tsx";
import {colorMapDishes} from "@/data";
import {useSortable} from "@dnd-kit/sortable";
import {CSS} from "@dnd-kit/utilities";

const dishIcon = (type: string) => {
    switch (type) {
        case 'main': {
            return faBurger;
        }
        case 'side': {
            return faBowlFood;
        }
        case 'beverage': {
            return faGlassWater;
        }
        default: {
            return faUtensils;
        }
    }
}

type CardProps = {
    dish: DishOutputDTO;

    onDelete: (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => void;
}

const DishCard = ({dish, onDelete}: CardProps) => {

    const navigate = useNavigate();
    /**
     * Wird aufgerufen, wenn der Bearbeiten-Button geklickt wird.
     */
    const handleEdit = () => {
        navigate(`/manage/dishes/${dish.id}/edit`);
    }

    const {
        attributes,
        listeners,
        setNodeRef,
        transform,
        transition,
    } = useSortable({id: dish.id});

    const style = {
        transform: CSS.Transform.toString(transform),
        transition,
    };

    return (
        <Card
            ref={setNodeRef}
            style={style}
            header={dish.name}
            colorVariant={colorMapDishes[dish.type]}
            actions={<>
                <BeButton variant="primary" onClick={handleEdit}><FontAwesomeIcon icon={faEdit}/> Bearbeiten</BeButton>
                <BeButton variant="danger" onClick={onDelete}><FontAwesomeIcon icon={faRemove}/> Löschen</BeButton>
            </>}
            image={dish.imageUrl && <img src={dish.imageUrl + '?size=200'} alt={dish.name} className="object-contain drop-shadow-lg"/>}
            typeCircle={dish.type && <FontAwesomeIcon icon={dishIcon(dish.type)} />}
            priceCircle={<>€{dish.price}</>}
            footer={dish.additionalInformation.size &&
                    <span className={"dish-info dish-info__" + dish.additionalInformation.size.type.toLowerCase()}>
                        {dish.additionalInformation.size.displayString}
                    </span>}
            topRight={<span className="dish-type" {...attributes} {...listeners}><FontAwesomeIcon icon={faGripLines} className="text-xl cursor-move" /></span>}
            >
            {dish.additionalInformation.description &&
                <span className={"dish-info dish-info__" + dish.additionalInformation.description.type.toLowerCase()}>
                    {dish.additionalInformation.description.displayString}
                </span>}
        </Card>
    );
}

export default DishCard;