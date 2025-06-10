import type {DishOutputDTO} from "@/types/DishOutputDTO.ts";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faEdit,
    faGripLines,
    faTrashCan
} from "@fortawesome/free-solid-svg-icons";

import {useNavigate} from "react-router-dom";
import React from "react";
import Card, {CardProps} from "@/components/shared/card.tsx";
import BeButton from "@/components/ui/be-button.tsx";
import {colorMapCards} from "@/data";
import {getIconElement} from "@/util";
import {useSortable} from "@dnd-kit/react/sortable";


export type DishCardProps = {
    index: number;
    dish: DishOutputDTO;
    onDelete: (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => void;
} & CardProps

const DishCard = ({index, dish, onDelete, ...props}: DishCardProps) => {

    const navigate = useNavigate();
    /**
     * Wird aufgerufen, wenn der Bearbeiten-Button geklickt wird.
     */
    const handleEdit = () => {
        navigate(`/manage/dishes/${dish.id}/edit`);
    }

    const {ref, handleRef} = useSortable({
        id: dish.id,
        index,
        type: "dish",
        accept: "dish"
    })

    return (
        <Card
            ref={ref}
            header={dish.name}
            colorVariant={colorMapCards[dish.type]}
            actions={<>
                <BeButton variant="primary" onClick={handleEdit}><FontAwesomeIcon icon={faEdit}/> Bearbeiten</BeButton>
                <BeButton variant="danger" onClick={onDelete}><FontAwesomeIcon icon={faTrashCan}/></BeButton>
            </>}
            image={dish.imageUrl && <img src={dish.imageUrl + '?size=200'} alt={dish.name} className="object-contain drop-shadow-lg"/>}
            typeCircle={dish.type && getIconElement(dish.type)}
            priceCircle={<>€{dish.price}</>}
            footer={dish.additionalInformation.size &&
                    <span className={"dish-info dish-info__" + dish.additionalInformation.size.type.toLowerCase()}>
                        {dish.additionalInformation.size.displayString}
                    </span>}
            topRight={<span className="dish-type" ref={handleRef}><FontAwesomeIcon icon={faGripLines} className="text-xl cursor-move" /></span>}
            {...props}
            >
            {dish.additionalInformation.description &&
                <span className={"dish-info dish-info__" + dish.additionalInformation.description.type.toLowerCase()}>
                    {dish.additionalInformation.description.displayString}
                </span>}
        </Card>
    );
}

export default DishCard;