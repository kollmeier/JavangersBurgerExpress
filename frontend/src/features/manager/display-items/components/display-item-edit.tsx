import DisplayItemForm from "./display-item-form.tsx";
import type {DisplayItemInputDTO} from "@/types/DisplayItemInputDTO.ts";
import type {DisplayItemOutputDTO} from "@/types/DisplayItemOutputDTO.ts";
import MinimalCard from "@/components/shared/minimal-card.tsx";
import {colorMapCards} from "@/data";

interface Props {
    onCancel?:  () => void;
    onSubmit?: (submittedDisplayItem: DisplayItemInputDTO, displayItemId: string) => Promise<void>;
    categoryId: string;
    displayItem: DisplayItemOutputDTO;
}

const DisplayItemEdit = ({ categoryId, onSubmit, displayItem, onCancel }: Props) => {

    const handleSubmit = function (submittedDisplayItem: DisplayItemInputDTO): Promise<void> {
        if (onSubmit) {
            return onSubmit(submittedDisplayItem, displayItem.id);
        }
        return Promise.resolve()
    }

    return (
        <MinimalCard colorVariant={colorMapCards['displayItem']} className={"min-w-sm"}>
            <DisplayItemForm
                displayItem={displayItem}
                categoryId={categoryId}
                onSubmit={handleSubmit}
                onCancel={onCancel} />
        </MinimalCard>
    );
}

export default DisplayItemEdit;