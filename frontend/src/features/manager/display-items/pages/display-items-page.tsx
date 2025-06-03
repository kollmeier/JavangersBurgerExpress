import React, {useEffect, useState} from 'react';
import { useNavigate, useParams} from 'react-router-dom';
import { faPlus } from '@fortawesome/free-solid-svg-icons/faPlus';
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
    verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import {toast} from "react-toastify";
import {usePageLayoutContext} from "@/context/page-layout-context.ts";

import {BeCircleLink} from "@/components/ui/be-circle-link.tsx";
import MinimalCard from "@/components/shared/minimal-card.tsx";
import {isAxiosError} from "axios";
import BeButton from "@/components/ui/be-button.tsx";
import {faWarning} from "@fortawesome/free-solid-svg-icons";
import BeDialog from "@/components/shared/be-dialog.tsx";
import {colorMapCards} from "@/data";
import {cn, useDisplayCategories} from "@/util";
import {DisplayCategoryOutputDTO} from "@/types/DisplayCategoryOutputDTO.ts";
import {useDisplayCategoryMutations} from "@/hooks/use-display-category-mutations.ts";
import {DisplayCategoryInputDTO} from "@/types/DisplayCategoryInputDTO.ts";
import DisplayCategoryAdd from "@/features/manager/display-categories/components/display-category-add.tsx";
import DisplayCategoryItem from "@/features/manager/display-categories/components/display-category-item.tsx";

const DisplayItemsPage: React.FC = () => {
    const displayCategories = useDisplayCategories();
    const [displayCategoriesOrder, setDisplayCategoriesOrder] = useState<string[]>([]);

    const {savePositionsMutation, addDisplayCategoryMutation, updateDisplayCategoryMutation, deleteDisplayCategoryMutation} = useDisplayCategoryMutations();

    const sensors = useSensors(
        useSensor(PointerSensor),
        useSensor(KeyboardSensor, {
            coordinateGetter: sortableKeyboardCoordinates,
        })
    );

    const displayCategoryId = useParams().displayCategoryId;

    const [displayCategoryToDelete, setDisplayCategoryToDelete] = React.useState<string | undefined>(undefined);

    const navigate = useNavigate();

    const {setSubHeader} = usePageLayoutContext();

    useEffect(() => {
        setSubHeader("Anzeige-Elemente");
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        if (displayCategories && displayCategories.length > 0) {
            setDisplayCategoriesOrder(displayCategories.map((displayCategory: DisplayCategoryOutputDTO) => displayCategory.id));
        }
    }, [displayCategories]);

    useEffect(() => {
        savePositionsMutation.mutate(displayCategoriesOrder, {
            onError: () => toast.error('Fehler beim Speichern der Positionen.')
        });
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [displayCategoriesOrder]);

    const handleSubmitAddDisplayCategory = async (submittedDisplayCategory: DisplayCategoryInputDTO) => {
        const toastId = toast.loading('Kategorie wird gespeichert...');
        return addDisplayCategoryMutation.mutate(submittedDisplayCategory, {
            onSuccess: () => {
                toast.update(toastId, {
                    render: 'Kategorie erfolgreich gespeichert',
                    type: 'success',
                    isLoading: false,
                    autoClose: 5000,
                });
                navigate("/manage/displayItems");
            },
            onError: (error: unknown) => {
                toast.update(toastId, {
                    render: 'Fehler beim Speichern der Kategorie: ' + (isAxiosError(error) && error.message),
                    type: 'error',
                    isLoading: false,
                    autoClose: 5000,
                });
            }
        });
    };

    const handleSubmitUpdateDisplayCategory = async (submittedDisplayCategory: DisplayCategoryInputDTO, displayCategoryId: string) => {
        const toastId = toast.loading('Kategorie wird gespeichert...');
        return updateDisplayCategoryMutation.mutate({...submittedDisplayCategory, id: displayCategoryId}, {
            onSuccess: () => {
                toast.update(toastId, {
                    render: 'Kategorie erfolgreich gespeichert',
                    type: 'success',
                    isLoading: false,
                    autoClose: 5000,
                });
                navigate("/manage/displayItems");
            },
            onError: (error: unknown) => {
                toast.update(toastId, {
                    render: 'Fehler beim Speichern der Kategorie: ' + (isAxiosError(error) && error.message),
                    type: 'error',
                    isLoading: false,
                    autoClose: 5000,
                });
            }
        });
    };

    const handleDeleteDisplayCategory = async (id?: string) => {
        if (!id) {
            return;
        }
        setDisplayCategoryToDelete(undefined);
        const toastId = toast.loading('Menü wird gelöscht...');
        return deleteDisplayCategoryMutation.mutate(id, {
            onSuccess: () => {
                toast.update(toastId, {
                    render: 'Kategorie erfolgreich gelöscht',
                    type: 'success',
                    isLoading: false,
                    autoClose: 5000,
                });
                navigate("/manage/displayItems");
            },
            onError: () => (error: unknown) => {
                toast.update(toastId, {
                    render: 'Fehler beim Löschen der Kategorie: ' + (isAxiosError(error) && error.message),
                    type: 'error',
                    isLoading: false,
                    autoClose: 5000,
                });
            }
        });
    }

    const handleDeleteDisplayCategoryConfirm = async (displayCategoryId: string) => {
        setDisplayCategoryToDelete(displayCategoryId);
    }

    const handleCancel = () => {
        navigate("/manage/displayItems");
    }

    function handleDragEnd(event: DragEndEvent) {
        const {active, over} = event;
        if (!active || !over) {
            return;
        }

        if (active.id !== over.id) {
            setDisplayCategoriesOrder((displayCategoriesOrder) => {
                const oldIndex = displayCategoriesOrder.indexOf(active.id + "");
                const newIndex = displayCategoriesOrder.indexOf(over.id + "");

                return arrayMove(displayCategoriesOrder, oldIndex, newIndex);
            });
        }
    }

    return (
        <DndContext collisionDetection={closestCenter} sensors={sensors} onDragEnd={handleDragEnd}>
                <SortableContext items={displayCategoriesOrder} strategy={verticalListSortingStrategy}>
                <div className="grid grid-cols-1 auto-rows-min sm:grid-cols-2 xl:grid-cols-3 gap-6">
            <MinimalCard className={cn("col-span-1 sm:col-span-2 xl:col-span-3 h-28 transition-[height]", displayCategoryId === 'add' && "h-58")} colorVariant={colorMapCards.displayCategory}>
                {displayCategoryId !== 'add' ? (
                    <BeCircleLink icon={faPlus} to="/manage/displayItems/category/add">Kategorie hinzufügen</BeCircleLink>
                ) : (
                    <DisplayCategoryAdd onSubmit={handleSubmitAddDisplayCategory} onCancel={handleCancel}/>
                )}
            </MinimalCard>
            {displayCategories?.map((displayCategory) => <DisplayCategoryItem key={displayCategory.id}
                                                   className="col-span-1 sm:col-span-2 xl:col-span-3 min-h-5"
                                                   id={displayCategory.id}
                                                   displayCategory={displayCategory}
                                                   onSubmit={handleSubmitUpdateDisplayCategory}
                                                   onDelete={handleDeleteDisplayCategoryConfirm}
                                                   onCancel={handleCancel}/>)}
        </div>
            <BeDialog
                onClose={() => setDisplayCategoryToDelete(undefined)}
                open={!!displayCategoryToDelete}
                icon={faWarning}
                iconClassName="text-danger"
                className="border border-danger"
                actions={<>
                    <BeButton onClick={() => setDisplayCategoryToDelete(undefined)} className="btn btn-neutral">Abbrechen</BeButton>
                    <BeButton onClick={() => handleDeleteDisplayCategory(displayCategoryToDelete)} className="btn btn-danger">Löschen</BeButton>
                </>}>
                    Sind Sie sicher, dass Sie die Kategorie löschen möchten?
            </BeDialog>
                </SortableContext>
        </DndContext>
    );
};

export default DisplayItemsPage;