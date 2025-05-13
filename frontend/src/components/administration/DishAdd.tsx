import {type FormEvent} from "react";
import type {DishInputDTO} from "../../types/DishInputDTO.ts";
import DishForm from "./DishForm.tsx";

interface Props {
    onSubmit?: (event: FormEvent<HTMLFormElement>, submittedDish: DishInputDTO) => void;
    onCancel?: () => void;
    dishType: 'main' | 'side' | 'beverage';
}

const DishAdd = ({ dishType, onSubmit, onCancel }: Props) => {
    return (
          <DishForm onSubmit={onSubmit} dishType={dishType} onCancel={onCancel}/>
    );
}

export default DishAdd;