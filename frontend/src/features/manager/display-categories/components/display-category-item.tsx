import type {DisplayCategoryOutputDTO} from "@/types/DisplayCategoryOutputDTO.ts";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import DisplayCategoryEdit from "./display-category-edit.tsx";
import type {DisplayCategoryInputDTO} from "@/types/DisplayCategoryInputDTO.ts";
import DisplayCategoryCard from "./display-category-card.tsx";
import {cn} from "@/util";

type Props = {
    id: string;
    displayCategory: DisplayCategoryOutputDTO;
    className?: string;
    onSubmit?: (submittedDisplayCategory: DisplayCategoryInputDTO, displayCategoryId: string) => Promise<void>;
    onDelete?: (id: string) => Promise<void>;
    onCancel?: () => void;
}
function DisplayCategoryItem(props: Readonly<Props>) {
    const [isEditing, setIsEditing] = useState<boolean>(false);

    /**
     * Wird aufgerufen, wenn die Anzeige-Kategorie über das Formular bearbeitet wird.
     * @param submittedDisplayCategory Die bearbeitete Anzeige-Kategorie.
     */
    const handleSubmit = async (submittedDisplayCategory: DisplayCategoryInputDTO) => {
        if (props.onSubmit) {
            return props.onSubmit(submittedDisplayCategory, props.displayCategory.id);
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
            return props.onDelete(props.displayCategory.id);
        }
        return Promise.resolve();
    }
    const displayCategoryId = useParams().displayCategoryId;

    useEffect(() => {
        setIsEditing(displayCategoryId === props.displayCategory.id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [displayCategoryId]);

    return (
        <div className={cn("h-39 transition-[height]", isEditing && "h-58", props.className)} id={props.id}>
            {!isEditing ? (
                <DisplayCategoryCard displayCategory={props.displayCategory} onDelete={handleDelete}/>
            ) : (
                <DisplayCategoryEdit displayCategory={props.displayCategory}
                                     onSubmit={handleSubmit}
                                     onCancel={handleCancel}/>
            )}
        </div>
    );
}

export default DisplayCategoryItem;