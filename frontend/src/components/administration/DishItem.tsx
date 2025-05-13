import type {DishOutputDTO} from "../../types/DishOutputDTO.ts";
import DishCard from "./DishCard.tsx";

type Props = {
    id: string;
    dish: DishOutputDTO;
}

function DishItem(props: Props) {

    return (
        <li className={"dish-card dish-card__" + props.dish.type} id={props.id}>
            <DishCard dish={props.dish}/>
        </li>
    );
}

export default DishItem;