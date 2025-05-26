import type {DishInputDTO} from "@/types/DishInputDTO.ts";
import DishForm from "@/features/manager/dishes/components/dish-form.tsx";

interface Props {
    onSubmit?: (submittedDish: DishInputDTO) => Promise<void>;
    onCancel?: () => void;
    dishType: 'main' | 'side' | 'beverage';
}

const DishAdd = ({ dishType, onSubmit, onCancel }: Props) => {
    return (
        <DishForm
            onSubmit={onSubmit}
            dishType={dishType}
            onCancel={onCancel}/>
    );
}

export default DishAdd;