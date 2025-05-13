import React, {useEffect} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import { faPlus } from '@fortawesome/free-solid-svg-icons/faPlus';
import DishAdd from "./DishAdd.tsx";
import {toast} from "react-toastify";
import {usePageLayoutContext} from "../../context/PageLayoutContext.ts";
import DishItem from "./DishItem.tsx";
import type {DishInputDTO} from "../../types/DishInputDTO.ts";

import "./DishesPage.scss";
import {useDishesContext} from "../../context/DishesContext.ts";

const DishesPage: React.FC = () => {
    const {dishes, addDish, updateDish, deleteDish} = useDishesContext();

    const dishId = useParams().dishId;

    const navigate = useNavigate();

    const {setSubHeader} = usePageLayoutContext();

    useEffect(() => {
        setSubHeader("Gerichte");
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const handleSubmitAddDish = async (_event: React.FormEvent, submittedDish: DishInputDTO) => {
        await toast.promise(
            addDish(submittedDish),
            {
                pending: 'Gericht wird gespeichert...',
                success: 'Gericht erfolgreich gespeichert',
                error: 'Fehler beim Speichern des Gerichts'
            });
        navigate("/manage/dishes");
    };

    const handleSubmitUpdateDish = async (_event: React.FormEvent, submittedDish: DishInputDTO, dishId: string) => {
        await toast.promise(
            updateDish(submittedDish, dishId),
            {
                pending: 'Gericht wird gespeichert...',
                success: 'Gericht erfolgreich gespeichert',
                error: 'Fehler beim Speichern des Gerichts'
            });
        navigate("/manage/dishes");
    };

    const handleDelete = async (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>, id: string) => {
        event.preventDefault();
        if (!confirm("Sind Sie sicher, dass Sie dieses Gericht löschen möchten?")) {
            return;
        }
        await toast.promise(
            deleteDish(id),
            {
                pending: 'Gericht wird gelöscht...',
                success: 'Gericht erfolgreich gelöscht',
                error: 'Fehler beim Löschen des Gerichts'
            });
        navigate("/manage/dishes");
    }

    const handleCancel = () => {
        navigate("/manage/dishes");
    }

    return (
        <ul className="dish-list">
            <li className="dish-card dish-card__main">
                {dishId !== 'add-main' ? (
                    <Link to="/manage/dishes/add-main" className="centered circle-button"><FontAwesomeIcon icon={faPlus}/><div>Hauptgericht hinzufügen</div></Link>
                ) : (
                    <DishAdd onSubmit={handleSubmitAddDish} onCancel={handleCancel} dishType="main"/>
                )}
            </li>
            <li className="dish-card dish-card__side">
                {dishId !== 'add-side' ? (
                    <Link to="/manage/dishes/add-side" className="centered circle-button"><FontAwesomeIcon icon={faPlus}/><div>Beilage hinzufügen</div></Link>
                ) : (
                    <DishAdd onSubmit={handleSubmitAddDish} onCancel={handleCancel} dishType="side"/>
                )}
            </li>
            <li className="dish-card dish-card__beverage" >
                {dishId !== 'add-beverage' ? (
                    <Link to="/manage/dishes/add-beverage" className="centered circle-button"><FontAwesomeIcon icon={faPlus}/><div>Getränk hinzufügen</div></Link>
                ) : (
                    <DishAdd onSubmit={handleSubmitAddDish} onCancel={handleCancel} dishType="beverage"/>
                )}
            </li>
            {dishes
                .map((dish) => <DishItem key={dish.id}
                                                id={dish.id}
                                                dish={dish}
                                                onSubmit={handleSubmitUpdateDish}
                                                onDelete={handleDelete}
                                                onCancel={handleCancel}/>)}
        </ul>
    );
};

export default DishesPage;