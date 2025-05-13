import DishForm from "./DishForm.tsx";
import React, {type FormEvent} from "react";
import type {DishInputDTO} from "../../types/DishInputDTO.ts";
import type {DishOutputDTO} from "../../types/DishOutputDTO.ts";

interface Props {
    onCancel?:  React.UIEventHandler<HTMLButtonElement | HTMLAnchorElement>;
    onSubmit?: (event: FormEvent<HTMLFormElement>, submittedDish: DishInputDTO, dishId: string) => void;
    dish: DishOutputDTO;
}

const DishEdit = ({ onSubmit, dish, onCancel }: Props) => {

    const handleSubmit = function (ev: FormEvent<HTMLFormElement>, submittedDish: DishInputDTO) {
        if (onSubmit) {
            onSubmit(ev, submittedDish, dish.id);
        }
    }

    return (
        <DishForm dish={dish} dishType={dish.type} onSubmit={handleSubmit} onCancel={onCancel} />
    );
}

export default DishEdit;