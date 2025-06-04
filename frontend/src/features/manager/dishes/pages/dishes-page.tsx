import React, {useEffect, useState} from 'react';
import { useNavigate, useParams} from 'react-router-dom';
import { faPlus } from '@fortawesome/free-solid-svg-icons/faPlus';
import DishAdd from "../components/dish-add.tsx";
import {
    DndContext,
    closestCenter,
    KeyboardSensor,
    PointerSensor,
    useSensor,
    useSensors,
    DragEndEvent,
} from '@dnd-kit/core';
import {
    arrayMove,
    SortableContext,
    sortableKeyboardCoordinates,
    rectSortingStrategy,
} from '@dnd-kit/sortable';
import {toast} from "react-toastify";
import {usePageLayoutContext} from "@/context/page-layout-context.ts";
import DishItem from "../components/dish-item.tsx";
import type {DishInputDTO} from "@/types/DishInputDTO.ts";

import {BeCircleLink} from "@/components/ui/be-circle-link.tsx";
import MinimalCard from "@/components/shared/minimal-card.tsx";
import {isAxiosError} from "axios";
import BeButton from "@/components/ui/be-button.tsx";
import {faWarning} from "@fortawesome/free-solid-svg-icons";
import {useDishes} from "@/util";
import {useDishMutations} from "@/hooks/use-dish-mutations.ts";
import BeDialog from "@/components/shared/be-dialog.tsx";
import {DishOutputDTO} from "@/types/DishOutputDTO.ts";

const DishesPage: React.FC = () => {
    const dishes = useDishes();
    const [dishesOrder, setDishesOrder] = useState<string[]>([]);

    const {savePositionsMutation, addDishMutation, updateDishMutation, deleteDishMutation} = useDishMutations();

    const sensors = useSensors(
        useSensor(PointerSensor),
        useSensor(KeyboardSensor, {
            coordinateGetter: sortableKeyboardCoordinates,
        })
    );

    const dishId = useParams().dishId;

    const [dishToDelete, setDishToDelete] = React.useState<string | undefined>(undefined);

    const navigate = useNavigate();

    const {setSubHeader} = usePageLayoutContext();

    useEffect(() => {
        setSubHeader("Gerichte");
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        if (dishes && dishes.length > 0) {
            setDishesOrder(dishes.map((dish: DishOutputDTO) => dish.id));
        }
    }, [dishes]);

    useEffect(() => {
        savePositionsMutation.mutate(dishesOrder, {
            onError: () => toast.error('Fehler beim Speichern der Positionen.')
        });
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [dishesOrder]);

    const handleSubmitAddDish = async (submittedDish: DishInputDTO) => {
        const toastId = toast.loading('Gericht wird gespeichert...');
        return addDishMutation.mutate(submittedDish, {
            onSuccess: () => {
                toast.update(toastId, {
                    render: 'Gericht erfolgreich gespeichert',
                    type: 'success',
                    isLoading: false,
                    autoClose: 5000,
                });
                navigate("/manage/dishes");
            },
            onError: (error: unknown) => {
                toast.update(toastId, {
                    render: 'Fehler beim Speichern des Gerichts: ' + (isAxiosError(error) && error.message),
                    type: 'error',
                    isLoading: false,
                    autoClose: 5000,
                });
            }
        });
    };

    const handleSubmitUpdateDish = async (submittedDish: DishInputDTO, dishId: string) => {
        const toastId = toast.loading('Gericht wird gespeichert...');
        return updateDishMutation.mutate({...submittedDish, id: dishId}, {
            onSuccess: () => {
                toast.update(toastId, {
                    render: 'Gericht erfolgreich gespeichert',
                    type: 'success',
                    isLoading: false,
                    autoClose: 5000,
                });
                navigate("/manage/dishes");
            },
            onError: (error: unknown) => {
                toast.update(toastId, {
                    render: 'Fehler beim Speichern des Gerichts: ' + (isAxiosError(error) && error.message),
                    type: 'error',
                    isLoading: false,
                    autoClose: 5000,
                });
            }
        });
    };

    const handleDeleteDish = async (id?: string) => {
        if (!id) {
            return;
        }
        setDishToDelete(undefined);
        const toastId = toast.loading('Gericht wird gelöscht...');
        return deleteDishMutation.mutate(id, {
            onSuccess: () => {
                toast.update(toastId, {
                    render: 'Gericht erfolgreich gelöscht',
                    type: 'success',
                    isLoading: false,
                    autoClose: 5000,
                });
                navigate("/manage/dishes");
            },
            onError: () => (error: unknown) => {
                toast.update(toastId, {
                    render: 'Fehler beim Löschen des Gerichts: ' + (isAxiosError(error) && error.message),
                    type: 'error',
                    isLoading: false,
                    autoClose: 5000,
                });
            }
        });
    }

    const handleDeleteDishConfirm = async (dishId: string) => {
        setDishToDelete(dishId);
    }

    const handleCancel = () => {
        navigate("/manage/dishes");
    }

    function handleDragEnd(event: DragEndEvent) {
        const {active, over} = event;
        if (!active || !over) {
            return;
        }

        if (active.id !== over.id) {
            setDishesOrder((dishesOrder) => {
                const oldIndex = dishesOrder.indexOf(active.id + "");
                const newIndex = dishesOrder.indexOf(over.id + "");

                return arrayMove(dishesOrder, oldIndex, newIndex);
            });
        }
    }

    return (
        <DndContext collisionDetection={closestCenter} sensors={sensors} onDragEnd={handleDragEnd}>
                <SortableContext items={dishesOrder} strategy={rectSortingStrategy}>
                <div className="grid grid-cols-1 auto-rows-min sm:grid-cols-2 xl:grid-cols-3 gap-6">
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
            {dishes?.map((dish) => <DishItem key={dish.id}
                                                className="grow-1 basis-30"
                                                id={dish.id}
                                                dish={dish}
                                                onSubmit={handleSubmitUpdateDish}
                                                onDelete={handleDeleteDishConfirm}
                                                onCancel={handleCancel}/>)}
        </div>
            <BeDialog
                onClose={() => setDishToDelete(undefined)}
                open={!!dishToDelete}
                icon={faWarning}
                iconClassName="text-danger"
                className="border border-danger"
                actions={<>
                    <BeButton onClick={() => setDishToDelete(undefined)} className="btn btn-neutral">Abbrechen</BeButton>
                    <BeButton onClick={() => handleDeleteDish(dishToDelete)} className="btn btn-danger">Löschen</BeButton>
                </>}>
                    Sind Sie sicher, dass Sie das Gericht löschen möchten?
            </BeDialog>
                </SortableContext>
        </DndContext>
    );
};

export default DishesPage;