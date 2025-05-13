import type {DishOutputDTO} from "../../types/DishOutputDTO.ts";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBowlFood, faBurger, faGlassWater, faUtensils} from "@fortawesome/free-solid-svg-icons";

import "./DishCard.scss";

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
}

const DishCard = ({dish}: CardProps) => {
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
            </span>
        </div>
    );
}

export default DishCard;