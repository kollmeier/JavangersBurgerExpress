import type {DisplayItemOutputDTO} from "@/types/DisplayItemOutputDTO.ts";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import DisplayItemEdit from "./display-item-edit.tsx";
import type {DisplayItemInputDTO} from "@/types/DisplayItemInputDTO.ts";
import DisplayItemCard from "./display-item-card.tsx";

type Props = {
    id: string;
    displayItem: DisplayItemOutputDTO;
    categoryId: string;
    className?: string;
    onSubmit?: (submittedDisplayItem: DisplayItemInputDTO, displayItemId: string) => Promise<void>;
    onDelete?: (id: string) => Promise<void>;
    onCancel?: () => void;
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
        <div className={props.className} id={props.id}>
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