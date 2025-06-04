import type {DisplayCategoryInputDTO} from "@/types/DisplayCategoryInputDTO.ts";
import DisplayCategoryForm from "@/features/manager/display-categories/components/display-category-form.tsx";

interface Props {
    onSubmit?: (submittedDisplayCategory: DisplayCategoryInputDTO) => Promise<void>;
    onCancel?: () => void;
}

const DisplayCategoryAdd = ({ onSubmit, onCancel }: Props) => {
    return (
        <DisplayCategoryForm
            onSubmit={onSubmit}
            onCancel={onCancel}/>
    );
}

export default DisplayCategoryAdd;