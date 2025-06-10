import type {DishOutputDTO} from "@/types/DishOutputDTO.ts";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import DishEdit from "./dish-edit.tsx";
import type {DishInputDTO} from "@/types/DishInputDTO.ts";
import DishCard, {DishCardProps} from "./dish-card.tsx";

type Props = {
    dish: DishOutputDTO;
    onSubmit?: (submittedDish: DishInputDTO, dishId: string) => Promise<void>;
    onDelete?: (id: string) => Promise<void>;
    onCancel?: () => void;
}

export type DishItemProps = Omit<DishCardProps, keyof Props> & Props

function DishItem({dish, onSubmit, onDelete, onCancel, ...props}: Readonly<DishItemProps>) {
    const [isEditing, setIsEditing] = useState<boolean>(false);

    /**
     * Wird aufgerufen, wenn das Gericht über das Formular bearbeitet wird.
     * @param submittedDish Das bearbeitete Gericht.
     */
    const handleSubmit = async (submittedDish: DishInputDTO) => {
        if (onSubmit) {
            return onSubmit(submittedDish, dish.id);
        }
        return Promise.resolve();
    }

    /**
     * Wird aufgerufen, wenn das Editieren abgebrochen wird.
     */
    const handleCancel = () => {
        if (onCancel) {
            onCancel();
        }
        setIsEditing(false);
    }

    /**
     *
     */
    const handleDelete = async () => {
        if (onDelete) {
            return onDelete(dish.id);
        }
        return Promise.resolve();
    }
    const dishId = useParams().dishId;

    useEffect(() => {
        setIsEditing(dishId === dish.id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [dishId]);

    return (
        !isEditing ? (
                <DishCard dish={dish} {...props} onDelete={handleDelete}/>
            ) : (
                <DishEdit dish={dish} {...props}
                          onSubmit={handleSubmit}
                          onCancel={handleCancel}/>
            )
    );
}

export default DishItem;