import DisplayCategoryForm from "./display-category-form.tsx";
import type {DisplayCategoryInputDTO} from "@/types/DisplayCategoryInputDTO.ts";
import type {DisplayCategoryOutputDTO} from "@/types/DisplayCategoryOutputDTO.ts";
import MinimalCard from "@/components/shared/minimal-card.tsx";
import {colorMapCards} from "@/data";

interface Props {
    onCancel?:  () => void;
    onSubmit?: (submittedDisplayCategory: DisplayCategoryInputDTO, displayCategoryId: string) => Promise<void>;
    displayCategory: DisplayCategoryOutputDTO;
}

const DisplayCategoryEdit = ({ onSubmit, displayCategory, onCancel }: Props) => {

    const handleSubmit = function (submittedDisplayCategory: DisplayCategoryInputDTO): Promise<void> {
        if (onSubmit) {
            return onSubmit(submittedDisplayCategory, displayCategory.id);
        }
        return Promise.resolve()
    }

    return (
        <MinimalCard colorVariant={colorMapCards['displayCategory']}>
            <DisplayCategoryForm displayCategory={displayCategory} onSubmit={handleSubmit} onCancel={onCancel} />
        </MinimalCard>
    );
}

export default DisplayCategoryEdit;