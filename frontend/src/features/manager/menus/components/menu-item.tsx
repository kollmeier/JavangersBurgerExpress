import type {MenuOutputDTO} from "@/types/MenuOutputDTO.ts";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import MenuEdit from "./menu-edit.tsx";
import type {MenuInputDTO} from "@/types/MenuInputDTO.ts";
import MenuCard, {MenuCardProps} from "./menu-card.tsx";

type Props = {
    menu: MenuOutputDTO;
    onSubmit?: (submittedMenu: MenuInputDTO, menuId: string) => Promise<void>;
    onDelete?: (id: string) => Promise<void>;
    onCancel?: () => void;
}

export type MenuItemProps = Omit<MenuCardProps, keyof Props> & Props;

function MenuItem({menu, onSubmit, onDelete, onCancel, ...props}: Readonly<MenuItemProps>) {
    const [isEditing, setIsEditing] = useState<boolean>(false);

    /**
     * Wird aufgerufen, wenn das Menü über das Formular bearbeitet wird.
     * @param submittedMenu Das bearbeitete Menü.
     */
    const handleSubmit = async (submittedMenu: MenuInputDTO) => {
        if (onSubmit) {
            return onSubmit(submittedMenu, menu.id);
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
            return onDelete(menu.id);
        }
        return Promise.resolve();
    }
    const menuId = useParams().menuId;

    useEffect(() => {
        setIsEditing(menuId === menu.id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [menuId]);

    return (
        !isEditing ? (
            <MenuCard menu={menu} onDelete={handleDelete} {...props}/>
        ) : (
            <MenuEdit menu={menu}
                      onSubmit={handleSubmit}
                      onCancel={handleCancel}/>
        )
    );
}

export default MenuItem;