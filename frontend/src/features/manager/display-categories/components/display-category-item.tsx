import type {DisplayCategoryOutputDTO} from "@/types/DisplayCategoryOutputDTO.ts";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import DisplayCategoryEdit from "./display-category-edit.tsx";
import type {DisplayCategoryInputDTO} from "@/types/DisplayCategoryInputDTO.ts";
import DisplayCategoryCard from "./display-category-card.tsx";
import {cn} from "@/util";
import DisplayItemItem from "@/features/manager/display-items/components/display-item-item.tsx";
import {DisplayItemOutputDTO} from "@/types/DisplayItemOutputDTO.ts";
import {DisplayItemInputDTO} from "@/types/DisplayItemInputDTO.ts";

type Props = {
    id: string;
    displayCategory: DisplayCategoryOutputDTO;
    displayItemsOrder: string[];
    setDisplayItemsOrder: (order: string[]) => void;
    className?: string;
    onSubmit?: (submittedDisplayCategory: DisplayCategoryInputDTO, displayCategoryId: string) => Promise<void>;
    onDisplayItemSubmit?: (submittedDisplayItem: DisplayItemInputDTO, displayItemId: string) => Promise<void>;
    onDisplayItemDelete?: (id: string) => Promise<void>;
    onAddDisplayItemClicked: () => void;
    onDelete?: (id: string) => Promise<void>;
    onCancel?: () => void;
    isDragging?: boolean;
    isAnyCategoryDragging?: boolean;
}
function DisplayCategoryItem({displayCategory, displayItemsOrder, setDisplayItemsOrder, ...props}: Readonly<Props>) {
    const [isEditing, setIsEditing] = useState<boolean>(false);

    /**
     * Wird aufgerufen, wenn die Anzeige-Kategorie über das Formular bearbeitet wird.
     * @param submittedDisplayCategory Die bearbeitete Anzeige-Kategorie.
     */
    const handleSubmit = async (submittedDisplayCategory: DisplayCategoryInputDTO) => {
        if (props.onSubmit) {
            return props.onSubmit(submittedDisplayCategory, displayCategory.id);
        }
        return Promise.resolve();
    }

    /**
     * Wird aufgerufen, wenn das Editieren abgebrochen wird.
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
            return props.onDelete(displayCategory.id);
        }
        return Promise.resolve();
    }
    const displayCategoryId = useParams().displayCategoryId;

    useEffect(() => {
        setIsEditing(displayCategoryId === displayCategory.id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [displayCategoryId]);

    useEffect(() => {
        if (displayItemsOrder && displayItemsOrder.length > 0) {
            setDisplayItemsOrder(displayCategory.displayItems.map((displayItem: DisplayItemOutputDTO) => displayItem.id));
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [displayCategory.displayItems]);

    return (
        <>
            <div className={cn("h-39 transition-[height]", isEditing && "h-58", props.className)} id={props.id}>
                {!isEditing ? (
                    <DisplayCategoryCard 
                        displayCategory={displayCategory} 
                        onDelete={handleDelete} 
                        onAddDisplayItemClicked={props.onAddDisplayItemClicked}
                        isDragging={props.isDragging}
                    />
                ) : (
                    <DisplayCategoryEdit displayCategory={displayCategory}
                                         onSubmit={handleSubmit}
                                         onCancel={handleCancel}/>
                )}
            </div>
            {displayCategory.displayItems.map((displayItem) => (
                <DisplayItemItem
                    key={displayItem.id}
                    id={displayItem.id}
                    categoryId={displayCategory.id}
                    displayItem={displayItem}
                    onCancel={props.onCancel}
                    onSubmit={props.onDisplayItemSubmit}
                    onDelete={props.onDisplayItemDelete}
                    className="w-full h-full"
                    isDragging={props.isDragging}
                    isAnyCategoryDragging={props.isAnyCategoryDragging}
                />
            ))}
        </>
    );
}

export default DisplayCategoryItem;
