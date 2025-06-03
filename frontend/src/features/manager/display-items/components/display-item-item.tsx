import type {MenuOutputDTO} from "@/types/MenuOutputDTO.ts";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import DisplayItemEdit from "./display-item-edit.tsx";
import type {MenuInputDTO} from "@/types/MenuInputDTO.ts";
import DisplayItemCard from "./display-item-card.tsx";
import {cn} from "@/util";

type Props = {
    id: string;
    menu: MenuOutputDTO;
    className?: string;
    onSubmit?: (submittedMenu: MenuInputDTO, menuId: string) => Promise<void>;
    onDelete?: (id: string) => Promise<void>;
    onCancel?: () => void;
}
function DisplayItemItem(props: Readonly<Props>) {
    const [isEditing, setIsEditing] = useState<boolean>(false);

    /**
     * Wird aufgerufen, wenn das Menü über das Formular bearbeitet wird.
     * @param submittedMenu Das bearbeitete Menü.
     */
    const handleSubmit = async (submittedMenu: MenuInputDTO) => {
        if (props.onSubmit) {
            return props.onSubmit(submittedMenu, props.menu.id);
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
            return props.onDelete(props.menu.id);
        }
        return Promise.resolve();
    }
    const menuId = useParams().menuId;

    useEffect(() => {
        setIsEditing(menuId === props.menu.id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [menuId]);

    return (
        <div className={cn("grow-1 basis-30 min-w-sm", props.className)} id={props.id}>
            {!isEditing ? (
                <DisplayItemCard menu={props.menu} onDelete={handleDelete}/>
            ) : (
                <DisplayItemEdit menu={props.menu}
                                 onSubmit={handleSubmit}
                                 onCancel={handleCancel}/>
            )}
        </div>
    );
}

export default DisplayItemItem;