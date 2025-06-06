import type {DisplayItemOutputDTO} from "@/types/DisplayItemOutputDTO.ts";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import DisplayItemEdit from "./display-item-edit.tsx";
import type {DisplayItemInputDTO} from "@/types/DisplayItemInputDTO.ts";
import DisplayItemCard from "./display-item-card.tsx";
import {cn} from "@/util";

type Props = {
    id: string;
    displayItem: DisplayItemOutputDTO;
    categoryId: string;
    className?: string;
    onSubmit?: (submittedDisplayItem: DisplayItemInputDTO, displayItemId: string) => Promise<void>;
    onDelete?: (id: string) => Promise<void>;
    onCancel?: () => void;
    isDragging?: boolean;
    isAnyCategoryDragging?: boolean;
}
function DisplayItemItem(props: Readonly<Props>) {
    const [isEditing, setIsEditing] = useState<boolean>(false);

    /**
     * Wird aufgerufen, wenn das Menü über das Formular bearbeitet wird.
     * @param submittedDisplayItem Das bearbeitete Menü.
     */
    const handleSubmit = async (submittedDisplayItem: DisplayItemInputDTO) => {
        if (props.onSubmit) {
            return props.onSubmit(submittedDisplayItem, props.displayItem.id);
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
            return props.onDelete(props.displayItem.id);
        }
        return Promise.resolve();
    }
    const displayItemId = useParams().displayItemId;

    useEffect(() => {
        setIsEditing(displayItemId === props.displayItem.id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [displayItemId]);

    return (
        <div
            className={cn(
                props.className,
                "origin-top",
                (props.isAnyCategoryDragging)
                    ? "h-auto opacity-0 max-h-0"
                    : "h-auto opacity-100 max-h-80",
            )}
            id={props.id}
            style={{
                transition: (props.isAnyCategoryDragging ? "max-height 100ms 200ms ease-in-out, opacity 200ms ease-in-out"
                        : "max-height 200ms ease-in-out, opacity 200ms 100ms ease-in-out"),
                willChange: "height, opacity",
            }}
        >
            {!isEditing ? (
                <DisplayItemCard displayItem={props.displayItem} onDelete={handleDelete}/>
            ) : (
                <DisplayItemEdit displayItem={props.displayItem}
                                 categoryId={props.categoryId}
                                 onSubmit={handleSubmit}
                                 onCancel={handleCancel}/>
            )}
        </div>
    );
}

export default DisplayItemItem;
