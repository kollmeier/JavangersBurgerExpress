import type {DishOutputDTO} from "../../../../types/DishOutputDTO.ts";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import DishEdit from "./dish-edit.tsx";
import type {DishInputDTO} from "../../../../types/DishInputDTO.ts";
import DishCard from "./dish-card.tsx";
import {cn} from "@/util";

type Props = {
    id: string;
    dish: DishOutputDTO;
    className?: string;
    onSubmit?: (event: React.FormEvent<HTMLFormElement>, submittedDish: DishInputDTO, dishId: string) => void;
    onDelete?: (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>, id: string) => void;
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

    /**
     *
     */
    const handleDelete = (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => {
        event.preventDefault();
        if (props.onDelete) {
            props.onDelete(event, props.dish.id);
        }
    }
    const dishId = useParams().dishId;

    useEffect(() => {
        setIsEditing(dishId === props.dish.id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [dishId]);

    return (
        <div className={cn("grow-1 basis-30 min-w-sm", props.className)} id={props.id}>
            {!isEditing ? (
                <DishCard dish={props.dish} onDelete={handleDelete}/>
            ) : (
                <DishEdit dish={props.dish}
                          onSubmit={handleSubmit}
                          onCancel={handleCancel}/>
            )}
        </div>
    );
}

export default DishItem;