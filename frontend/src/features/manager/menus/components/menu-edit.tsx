import MenuForm from "./menu-form.tsx";
import type {MenuInputDTO} from "@/types/MenuInputDTO.ts";
import type {MenuOutputDTO} from "@/types/MenuOutputDTO.ts";
import MinimalCard from "@/components/shared/minimal-card.tsx";
import {colorMapCards} from "@/data";

interface Props {
    onCancel?:  () => void;
    onSubmit?: (submittedMenu: MenuInputDTO, menuId: string) => Promise<void>;
    menu: MenuOutputDTO;
}

const MenuEdit = ({ onSubmit, menu, onCancel }: Props) => {

    const handleSubmit = function (submittedMenu: MenuInputDTO): Promise<void> {
        if (onSubmit) {
            return onSubmit(submittedMenu, menu.id);
        }
        return Promise.resolve()
    }

    return (
        <MinimalCard colorVariant={colorMapCards['menu']} className={"min-w-sm"}>
            <MenuForm menu={menu} onSubmit={handleSubmit} onCancel={onCancel} />
        </MinimalCard>
    );
}

export default MenuEdit;