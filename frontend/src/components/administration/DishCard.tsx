import type {DishOutputDTO} from "../../types/DishOutputDTO.ts";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBowlFood, faBurger, faEdit, faGlassWater, faRemove, faUtensils} from "@fortawesome/free-solid-svg-icons";

import "./DishCard.scss";
import {Link, useNavigate} from "react-router-dom";
import React from "react";

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
        <div>
            <span className="dish-type" ><FontAwesomeIcon icon={dishIcon(dish.type)}/></span>
            <span className="dish-name">{dish.name}</span>
            {dish.additionalInformation.description ? (
                <span className={"dish-info dish-info__" + dish.additionalInformation.description.type.toLowerCase()}>
                        {dish.additionalInformation.description.displayString}
                    </span>
            ) : null}
            {dish.imageUrl && (
                <span className="dish-image">
                        <img src={dish.imageUrl + '?size=200'} alt={dish.name}/>
                    </span>
            )}
            {dish.additionalInformation.size ? (
                <span className={"dish-info dish-info__" + dish.additionalInformation.size.type.toLowerCase()}>
                        {dish.additionalInformation.size.displayString}
                    </span>
            ) : null}
            <span className="dish-price">€{dish.price}</span>
            <span className="dish-actions">
                <Link className="button" to={`/manage/dishes/${dish.id}/edit`}
                      onClick={handleEdit}><FontAwesomeIcon icon={faEdit}/> Bearbeiten</Link>
                <Link to="#" className="button button--danger" onClick={onDelete}><FontAwesomeIcon icon={faRemove}/> Löschen</Link>
            </span>
        </div>
    );
}

export default DishCard;