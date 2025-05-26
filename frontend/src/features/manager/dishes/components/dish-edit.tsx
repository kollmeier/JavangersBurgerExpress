import DishForm from "./dish-form.tsx";
import type {DishInputDTO} from "@/types/DishInputDTO.ts";
import type {DishOutputDTO} from "@/types/DishOutputDTO.ts";
import MinimalCard from "@/components/shared/minimal-card.tsx";
import {colorMapDishes} from "@/data";

interface Props {
    onCancel?:  () => void;
    onSubmit?: (submittedDish: DishInputDTO, dishId: string) => Promise<void>;
    dish: DishOutputDTO;
}

const DishEdit = ({ onSubmit, dish, onCancel }: Props) => {

    const handleSubmit = function (submittedDish: DishInputDTO): Promise<void> {
        if (onSubmit) {
            return onSubmit(submittedDish, dish.id);
        }
        return Promise.resolve()
    }

    return (
        <MinimalCard colorVariant={colorMapDishes[dish.type]} className={"min-w-sm"}>
            <DishForm dish={dish} dishType={dish.type} onSubmit={handleSubmit} onCancel={onCancel} />
        </MinimalCard>
    );
}

export default DishEdit;