import React, {useEffect} from 'react';
import { useNavigate, useParams} from 'react-router-dom';
import { faPlus } from '@fortawesome/free-solid-svg-icons/faPlus';
import DishAdd from "../components/dish-add.tsx";
import {toast} from "react-toastify";
import {usePageLayoutContext} from "@/context/page-layout-context.ts";
import DishItem from "../components/dish-item.tsx";
import type {DishInputDTO} from "@/types/DishInputDTO.ts";

import {useDishesContext} from "@/context/dishes-context.ts";
import {BeCircleLink} from "@/components/ui/be-circle-link.tsx";
import MinimalCard from "@/components/shared/minimal-card.tsx";

const DishesPage: React.FC = () => {
    const {dishes, addDish, updateDish, deleteDish} = useDishesContext();

    const dishId = useParams().dishId;

    const navigate = useNavigate();

    const {setSubHeader} = usePageLayoutContext();

    useEffect(() => {
        setSubHeader("Gerichte");
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const handleSubmitAddDish = async (submittedDish: DishInputDTO) => {
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
        <div className="flex flex-row flex-wrap gap-6">
            <MinimalCard className={"grow-1 basis-30 min-h-64"}  colorVariant="red">
                {dishId !== 'add-main' ? (
                    <BeCircleLink icon={faPlus} to="/manage/dishes/add-main">Hauptgericht hinzufügen</BeCircleLink>
                ) : (
                    <DishAdd onSubmit={handleSubmitAddDish} onCancel={handleCancel} dishType="main"/>
                )}
            </MinimalCard>
            <MinimalCard className={"grow-1 basis-30 min-h-64 h-full"}  colorVariant="green">
                {dishId !== 'add-side' ? (
                    <BeCircleLink icon={faPlus} to="/manage/dishes/add-side">Beilage hinzufügen</BeCircleLink>
                ) : (
                    <DishAdd onSubmit={handleSubmitAddDish} onCancel={handleCancel} dishType="side"/>
                )}
            </MinimalCard>
            <MinimalCard className={"grow-1 basis-30 min-h-64 h-full"}  colorVariant="blue">
                {dishId !== 'add-beverage' ? (
                    <BeCircleLink icon={faPlus} to="/manage/dishes/add-beverage">Getränk hinzufügen</BeCircleLink>
                ) : (
                    <DishAdd onSubmit={handleSubmitAddDish} onCancel={handleCancel} dishType="beverage"/>
                )}
            </MinimalCard>
            {dishes
                .map((dish) => <DishItem key={dish.id}
                                                className="grow-1 basis-30"
                                                id={dish.id}
                                                dish={dish}
                                                onSubmit={handleSubmitUpdateDish}
                                                onDelete={handleDelete}
                                                onCancel={handleCancel}/>)}
        </div>
    );
};

export default DishesPage;