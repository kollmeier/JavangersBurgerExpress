import type {MenuInputDTO} from "@/types/MenuInputDTO.ts";
import MenuForm from "@/features/manager/menus/components/menu-form.tsx";

interface Props {
    onSubmit?: (submittedMenu: MenuInputDTO) => Promise<void>;
    onCancel?: () => void;
}

const DisplayItemAdd = ({ onSubmit, onCancel }: Props) => {
    return (
        <MenuForm
            onSubmit={onSubmit}
            onCancel={onCancel}/>
    );
}

export default DisplayItemAdd;