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
import {CollisionPriority} from "@dnd-kit/abstract";
import {useSortable} from "@dnd-kit/react/sortable";

type Props = {
    id: string;
    index: number;
    displayCategory: DisplayCategoryOutputDTO;
    displayItemsOrder: string[];
    setDisplayItemsOrder: (order: string[]) => void;
    isDraggable?: boolean;
    className?: string;
    onSubmit?: (submittedDisplayCategory: DisplayCategoryInputDTO, displayCategoryId: string) => Promise<void>;
    onDisplayItemSubmit?: (submittedDisplayItem: DisplayItemInputDTO, displayItemId: string) => Promise<void>;
    onDisplayItemDelete?: (id: string) => Promise<void>;
    onAddDisplayItemClicked: () => void;
    onDelete?: (id: string) => Promise<void>;
    onCancel?: () => void;
}
function DisplayCategoryItem({
                                 id,
                                 index,
                                 displayCategory,
                                 displayItemsOrder,
                                 setDisplayItemsOrder,
                                 isDraggable = false,
                                 ...props
}: Readonly<Props>) {
    const [isEditing, setIsEditing] = useState<boolean>(false);

    const {isDropTarget, ref, handleRef} = useSortable({
        id,
        index: index,
        type: "displayCategory",
        accept: ["displayItem", "displayCategory"],
        collisionPriority: CollisionPriority.Low
    })

    const dropClassName = isDropTarget ? "bg-blue-500/5" : "";

    const handleSubmit = async (submittedDisplayCategory: DisplayCategoryInputDTO) => {
        if (props.onSubmit) {
            return props.onSubmit(submittedDisplayCategory, displayCategory.id);
        }
        return Promise.resolve();
    }

    const handleCancel = () => {
        if (props.onCancel) {
            props.onCancel();
        }
        setIsEditing(false);
    }

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
        <div className={cn("col-span-3 grid grid-cols-1 auto-rows-min sm:grid-cols-2 xl:grid-cols-3 gap-6 rounded-lg transition-bg",
            dropClassName,
            "transition-[gap] duration-300 ease-in-out",
            isDraggable && "gap-0")} ref={isDraggable ? ref : undefined}>
            <div className={cn("h-39 transition-[height]", isEditing && "h-58", props.className)}>
                {!isEditing ? (
                    <DisplayCategoryCard 
                        displayCategory={displayCategory} 
                        onDelete={handleDelete} 
                        onAddDisplayItemClicked={props.onAddDisplayItemClicked}
                        isDraggable={isDraggable}
                        handleRef={handleRef}
                    />
                ) : (
                    <DisplayCategoryEdit displayCategory={displayCategory}
                                         onSubmit={handleSubmit}
                                         onCancel={handleCancel}/>
                )}
            </div>
            {displayCategory.displayItems.map((displayItem, index) => (
                <DisplayItemItem
                    key={displayItem.id}
                    id={displayItem.id}
                    index={index}
                    categoryId={displayCategory.id}
                    displayItem={displayItem}
                    onCancel={props.onCancel}
                    onSubmit={props.onDisplayItemSubmit}
                    onDelete={props.onDisplayItemDelete}
                    isDraggable={!isDraggable}
                    className="w-full h-full"
                />
            ))}
        </div>
    );
}

export default DisplayCategoryItem;
