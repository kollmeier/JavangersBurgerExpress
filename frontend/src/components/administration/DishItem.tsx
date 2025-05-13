import type {DishOutputDTO} from "../../types/DishOutputDTO.ts";
import DishCard from "./DishCard.tsx";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import DishEdit from "./DishEdit.tsx";
import type {DishInputDTO} from "../../types/DishInputDTO.ts";

type Props = {
    id: string;
    dish: DishOutputDTO;
    onSubmit?: (event: React.FormEvent<HTMLFormElement>, submittedDish: DishInputDTO, dishId: string) => void;
    onCancel?: (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => void;
}
function DishItem(props: Readonly<Props>) {
    const [isEditing, setIsEditing] = useState<boolean>(false);

    /**
     * Wird aufgerufen, wenn das Gericht über das Formular bearbeitet wird.
     * @param event Das abgeschickte FormEvent.
     * @param submittedDish Das bearbeitete Gericht.
     */
    const handleSubmit = (event: React.FormEvent<HTMLFormElement>, submittedDish: DishInputDTO) => {
        if (props.onSubmit) {
            props.onSubmit(event, submittedDish, props.dish.id);
        }
    }

    /**
     * Wird aufgerufen, wenn das Editieren abgebrochen wird.
     * @param event Das Maus-Event.
     */
    const handleCancel = (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => {
        if (props.onCancel) {
            props.onCancel(event);
        }
        setIsEditing(false);
    }

    const dishId = useParams().dishId;

    useEffect(() => {
        setIsEditing(dishId === props.dish.id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [dishId]);

    return (
        <li className={"dish-card dish-card__" + props.dish.type} id={props.id}>
            {!isEditing ? (
                <DishCard dish={props.dish}/>
            ) : (
                <DishEdit dish={props.dish}
                          onSubmit={handleSubmit}
                          onCancel={handleCancel}/>
            )}
        </li>
    );
}

export default DishItem;