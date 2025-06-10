import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import DisplayItemEdit from "./display-item-edit.tsx";
import type {DisplayItemInputDTO} from "@/types/DisplayItemInputDTO.ts";
import DisplayItemCard, {DisplayItemCardProps} from "./display-item-card.tsx";

type Props = {
    categoryId: string;
    onSubmit?: (submittedDisplayItem: DisplayItemInputDTO, displayItemId: string) => Promise<void>;
    onDelete?: (id: string) => Promise<void>;
    onCancel?: () => void;
}

export type DisplayItemItemProps = Omit<DisplayItemCardProps, keyof Props> & Props;

function DisplayItemItem({categoryId, onSubmit, onDelete, onCancel, ...props}: Readonly<DisplayItemItemProps>) {
    const [isEditing, setIsEditing] = useState<boolean>(false);

    /**
     * Wird aufgerufen, wenn das Menü über das Formular bearbeitet wird.
     * @param submittedDisplayItem Das bearbeitete Menü.
     */
    const handleSubmit = async (submittedDisplayItem: DisplayItemInputDTO) => {
        if (onSubmit) {
            return onSubmit(submittedDisplayItem, props.displayItem.id);
        }
        return Promise.resolve();
    }

    /**
     * Wird aufgerufen, wenn das Editieren abgebrochen wird.
     */
    const handleCancel = () => {
        if (onCancel) {
            onCancel();
        }
        setIsEditing(false);
    }

    /**
     *
     */
    const handleDelete = async () => {
        if (onDelete) {
            return onDelete(props.displayItem.id);
        }
        return Promise.resolve();
    }
    const displayItemId = useParams().displayItemId;

    useEffect(() => {
        setIsEditing(displayItemId === props.displayItem.id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [displayItemId]);

    return (
        <>
            {!isEditing ? (
                <DisplayItemCard {...props} onDelete={handleDelete}/>
            ) : (
                <DisplayItemEdit displayItem={props.displayItem}
                                 categoryId={categoryId}
                                 onSubmit={handleSubmit}
                                 onCancel={handleCancel}/>
            )}
        </>
    );
}

export default DisplayItemItem;
