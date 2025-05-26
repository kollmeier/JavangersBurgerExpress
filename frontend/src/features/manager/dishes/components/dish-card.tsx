import type {DishOutputDTO} from "@/types/DishOutputDTO.ts";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBowlFood, faBurger, faEdit, faGlassWater, faRemove, faUtensils} from "@fortawesome/free-solid-svg-icons";

import {useNavigate} from "react-router-dom";
import React from "react";
import Card from "@/components/shared/card.tsx";
import BeButton from "@/components/ui/be-button.tsx";

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

    return (
        <Card
            header={dish.name}
            colorVariant={"red"}
            actions={<>
                <BeButton onClick={handleEdit}><FontAwesomeIcon icon={faEdit}/> Bearbeiten</BeButton>
                <BeButton variant="danger" onClick={onDelete}><FontAwesomeIcon icon={faRemove}/> Löschen</BeButton>
            </>}
            image={dish.imageUrl && <img src={dish.imageUrl + '?size=200'} alt={dish.name}/>}
            typeCircle={dish.type && <FontAwesomeIcon icon={dishIcon(dish.type)}/>}
            priceCircle={<>€{dish.price}</>}
            >
            {dish.additionalInformation.description &&
                    <span className={"dish-info dish-info__" + dish.additionalInformation.description.type.toLowerCase()}>
                        {dish.additionalInformation.description.displayString}
                    </span>}
            {dish.additionalInformation.size &&
                <span className={"dish-info dish-info__" + dish.additionalInformation.size.type.toLowerCase()}>
                        {dish.additionalInformation.size.displayString}
                    </span>}
        </Card>
    );
}

export default DishCard;