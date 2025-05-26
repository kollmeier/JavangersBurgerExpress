import type {DishOutputDTO} from "@/types/DishOutputDTO.ts";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import DishEdit from "./dish-edit.tsx";
import type {DishInputDTO} from "@/types/DishInputDTO.ts";
import DishCard from "./dish-card.tsx";
import {cn} from "@/util";

type Props = {
    id: string;
    dish: DishOutputDTO;
    className?: string;
    onSubmit?: (submittedDish: DishInputDTO, dishId: string) => Promise<void>;
    onDelete?: (id: string) => Promise<void>;
    onCancel?: () => void;
}
function DishItem(props: Readonly<Props>) {
    const [isEditing, setIsEditing] = useState<boolean>(false);

    /**
     * Wird aufgerufen, wenn das Gericht über das Formular bearbeitet wird.
     * @param event Das abgeschickte FormEvent.
     * @param submittedDish Das bearbeitete Gericht.
     */
    const handleSubmit = async (submittedDish: DishInputDTO) => {
        if (props.onSubmit) {
            return props.onSubmit(submittedDish, props.dish.id);
        }
        return Promise.resolve();
    }

    /**
     * Wird aufgerufen, wenn das Editieren abgebrochen wird.
     * @param event Das Maus-Event.
     */
    const handleCancel = () => {
        if (props.onCancel) {
            props.onCancel();
        }
        setIsEditing(false);
    }

    /**
     *
     */
    const handleDelete = async () => {
        if (props.onDelete) {
            return props.onDelete(props.dish.id);
        }
        return Promise.resolve();
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