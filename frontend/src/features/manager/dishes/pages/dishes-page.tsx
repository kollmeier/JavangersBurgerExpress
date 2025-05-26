import React, {useEffect} from 'react';
import { useNavigate, useParams} from 'react-router-dom';
import { faPlus } from '@fortawesome/free-solid-svg-icons/faPlus';
import DishAdd from "../components/dish-add.tsx";
import {toast} from "react-toastify";
import {usePageLayoutContext} from "@/context/page-layout-context.ts";
import DishItem from "../components/dish-item.tsx";
import type {DishInputDTO} from "@/types/DishInputDTO.ts";

import {BeCircleLink} from "@/components/ui/be-circle-link.tsx";
import MinimalCard from "@/components/shared/minimal-card.tsx";
import {isAxiosError} from "axios";
import {Dialog, DialogBackdrop, DialogPanel} from "@headlessui/react";
import BeButton from "@/components/ui/be-button.tsx";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faWarning} from "@fortawesome/free-solid-svg-icons";
import {useDishes} from "@/util";
import {useDishMutations} from "@/hooks/use-dish-mutations.ts";

const DishesPage: React.FC = () => {
    const dishes = useDishes();

    const {addDishMutation, updateDishMutation, deleteDishMutation} = useDishMutations();

    const dishId = useParams().dishId;

    const [dishToDelete, setDishToDelete] = React.useState<string | undefined>(undefined);

    const navigate = useNavigate();

    const {setSubHeader} = usePageLayoutContext();

    useEffect(() => {
        setSubHeader("Gerichte");
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

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

    const handleDeleteDish = async (id: string) => {
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

    return (
        <>
        <div className="grid grid-cols-1 auto-rows-fr sm:grid-cols-2 xl:grid-cols-3 gap-6">
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
            <Dialog
                onClose={() => setDishToDelete(undefined)}
                open={!!dishToDelete}>
                <DialogBackdrop className="fixed inset-0 bg-black/50 backdrop-blur-xs" />
                <div className="fixed inset-0 flex w-screen items-center justify-center p-4">
                    <DialogPanel className="grid gap-6 auto-cols-max grid-rows-2 max-w-xl space-y-4 border border-danger bg-neutral-600 shadow-2xl rounded-xl p-10">
                        <FontAwesomeIcon icon={faWarning} className="w-14 text-6xl text-danger"/>
                        <p className="col-start-2 max-w-md place-self-center">Sind Sie sicher, dass Sie das Gericht löschen möchten?</p>
                        <div className="flex justify-end gap-2 row-start-2 col-span-2 place-self-end">
                            <BeButton onClick={() => setDishToDelete(undefined)} className="btn btn-neutral">Abbrechen</BeButton>
                            <BeButton onClick={() => handleDeleteDish(dishToDelete!)} className="btn btn-danger">Löschen</BeButton>
                        </div>
                    </DialogPanel>
                </div>
            </Dialog>
        </>
    );
};

export default DishesPage;