import type {DisplayItemInputDTO} from "@/types/DisplayItemInputDTO.ts";
import DisplayItemForm from "@/features/manager/display-items/components/display-item-form.tsx";

interface Props {
    categoryId: string;
    onSubmit?: (submittedDisplayItem: DisplayItemInputDTO) => Promise<void>;
    onCancel?: () => void;
}

const DisplayItemAdd = ({ onSubmit, onCancel, categoryId }: Props) => {
    return (
        <DisplayItemForm
            categoryId={categoryId}
            onSubmit={onSubmit}
            onCancel={onCancel}/>
    );
}

export default DisplayItemAdd;