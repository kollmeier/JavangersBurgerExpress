import type {MenuOutputDTO} from "@/types/MenuOutputDTO.ts";

import {useNavigate} from "react-router-dom";
import React from "react";
import Card, {CardProps} from "@/components/shared/card.tsx";
import BeButton from "@/components/ui/be-button.tsx";
import {colorMapCards} from "@/data";
import {getIconElement} from "@/util";
import {useSortable} from "@dnd-kit/react/sortable";
import DishImages from "@/components/ui/dish-images.tsx";
import {Grip, Pen, Trash} from "lucide-react";

export type MenuCardProps = {
    menu: MenuOutputDTO;
    index: number;
    onDelete: (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => void;
} & CardProps

const MenuCard = ({menu, index, onDelete, ...props}: MenuCardProps) => {

    const navigate = useNavigate();
    /**
     * Wird aufgerufen, wenn der Bearbeiten-Button geklickt wird.
     */
    const handleEdit = () => {
        navigate(`/manage/menus/${menu.id}/edit`);
    }

    const {ref, handleRef} = useSortable({
        id: menu.id,
        index,
        type: "menu",
        accept: "menu"
    })

    return (
        <Card
            ref={ref}
            header={menu.name}
            colorVariant={colorMapCards['menu']}
            actions={<>
                <BeButton variant="primary" onClick={handleEdit} icon={Pen}>Bearbeiten</BeButton>
                <BeButton variant="danger" onClick={onDelete} icon={Trash}></BeButton>
            </>}
            typeCircle={getIconElement('menu')}
            priceCircle={<div className="flex flex-col items-center"><span className="text-[0.6em] line-through">{menu.dishes.reduce((s,d) => s + parseFloat(d.price), 0).toFixed(2)}€</span>{menu.price.replace('.', ',')}€</div>}
            footer={<div className="flex flex-wrap gap-1">{menu.dishes.map(dish => <span key={menu.id + dish.id} className="not-last:after:content-[',_']">{dish.name}</span>)}</div>}
            image={<DishImages
                className="w-full h-full top-0 object-contain"
                mainImages={menu.dishes.filter(dish => dish.type === "main").map(dish => dish.imageUrl)}
                sideImages={menu.dishes.filter(dish => dish.type === "side").map(dish => dish.imageUrl)}
                beverageImages={menu.dishes.filter(dish => dish.type === "beverage").map(dish => dish.imageUrl)}
            />}
            imageClassName="w-full h-full"
            topRight={<span className="menu-type" ref={handleRef}><Grip className="text-xl cursor-move" /></span>}
            {...props}
            >
            {menu.additionalInformation.description &&
                <span className={"menu-info menu-info__" + menu.additionalInformation.description.type.toLowerCase()}>
                    {menu.additionalInformation.description.displayString}
                </span>}
        </Card>
    );
}

export default MenuCard;