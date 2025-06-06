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
import BeButton from "@/components/ui/be-button.tsx";
import {faWarning} from "@fortawesome/free-solid-svg-icons";
import BeDialog from "@/components/shared/be-dialog.tsx";
import {colorMapCards} from "@/data";
import {cn, errorMessage, useDisplayCategories} from "@/util";
import {DisplayCategoryOutputDTO} from "@/types/DisplayCategoryOutputDTO.ts";
import {useDisplayCategoryMutations} from "@/hooks/use-display-category-mutations.ts";
import {DisplayCategoryInputDTO} from "@/types/DisplayCategoryInputDTO.ts";
import DisplayCategoryAdd from "@/features/manager/display-categories/components/display-category-add.tsx";
import DisplayCategoryItem from "@/features/manager/display-categories/components/display-category-item.tsx";
import {DisplayItemInputDTO} from "@/types/DisplayItemInputDTO.ts";
import {useDisplayItemMutations} from "@/hooks/use-display-item-mutations.ts";
import DisplayItemAdd from "@/features/manager/display-items/components/display-item-add.tsx";
import {DisplayItemOutputDTO} from "@/types/DisplayItemOutputDTO.ts";
import {useDisplayItems} from "@/util/queries.ts";

const DisplayItemsPage: React.FC = () => {
    const {data: displayCategories} = useDisplayCategories();
    const [displayCategoriesOrder, setDisplayCategoriesOrder] = useState<string[]>([]);

    const {data: displayItems} = useDisplayItems();
    const [displayItemsOrderByCategory, setDisplayItemsOrderByCategory] = useState<{[categoryId: string]: string[]}>({});

    const setDisplayItemsOrder = (categoryId: string, displayItemsOrder: string[]) => {
        setDisplayItemsOrderByCategory({...displayItemsOrderByCategory, [categoryId]: displayItemsOrder});
    }

    const {updateData: setDisplayCategories, savePositionsMutation: saveCategoryPositionsMutation, addDisplayCategoryMutation, updateDisplayCategoryMutation, deleteDisplayCategoryMutation} = useDisplayCategoryMutations();
    const {updateData: setDisplayItems, savePositionsMutation: saveItemPositionsMutation, addDisplayItemMutation, updateDisplayItemMutation, deleteDisplayItemMutation} = useDisplayItemMutations();

    const sensors = useSensors(
        useSensor(PointerSensor),
        useSensor(KeyboardSensor, {
            coordinateGetter: sortableKeyboardCoordinates,
        })
    );

    const displayCategoryId = useParams().displayCategoryId;

    const [displayCategoryToDelete, setDisplayCategoryToDelete] = React.useState<string | undefined>(undefined);
    const [displayItemToDelete, setDisplayItemToDelete] = React.useState<string | undefined>(undefined);
    const [displayCategoryToAddTo, setDisplayCategoryToAddTo] = React.useState<string | undefined>(undefined);

    const [countItemsInCategoryToDelete, setCountItemsInCategoryToDelete] = React.useState<number>(0);

    const navigate = useNavigate();

    const {setSubHeader} = usePageLayoutContext();

    useEffect(() => {
        setSubHeader("Anzeige-Elemente");
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        if (displayCategories && displayCategories.length > 0) {
            setDisplayCategoriesOrder(displayCategories.map((displayCategory: DisplayCategoryOutputDTO) => displayCategory.id));
            setDisplayItems(displayCategories.flatMap((displayCategory: DisplayCategoryOutputDTO) => displayCategory.displayItems ?? []));
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [displayCategories]);

    useEffect(() => {
        if (displayItems && displayItems.length > 0) {
            setDisplayItemsOrderByCategory(displayItems.reduce((acc: {[categoryId: string]: string[]}, displayItem: DisplayItemOutputDTO) => {
                if (!acc[displayItem.categoryId]) {
                    acc[displayItem.categoryId] = [];
                }
                acc[displayItem.categoryId].push(displayItem.id);
                return acc;
            }, {}));
            displayCategories?.forEach((displayCategory: DisplayCategoryOutputDTO) => {
                displayCategory.displayItems = displayItems.filter((displayItem: DisplayItemOutputDTO) => displayItem.categoryId === displayCategory.id);
            });
            setDisplayCategories(displayCategories?.map(
                (displayCategory: DisplayCategoryOutputDTO) =>
                    ({
                        ...displayCategory,
                        displayItems: displayItems.filter(displayItem => displayItem.categoryId === displayCategory.id)
                    })
            ));
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [displayItems]);

    useEffect(() => {
        if (displayCategoriesOrder && displayCategoriesOrder.length > 0) {
            saveCategoryPositionsMutation.mutate(displayCategoriesOrder, {
                onError: () => toast.error('Fehler beim Speichern der Positionen.')
            });
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [displayCategoriesOrder]);

    useEffect(() => {
        if (displayItemsOrderByCategory && Object.keys(displayItemsOrderByCategory).length > 0) {
            saveItemPositionsMutation.mutate(Object.values(displayItemsOrderByCategory)
                .flatMap((displayItems: string[]) => displayItems), {
                onError: () => toast.error('Fehler beim Speichern der Positionen.')
            });
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [displayItemsOrderByCategory]);

    useEffect(() => {
        setCountItemsInCategoryToDelete(displayCategoryToDelete && displayItemsOrderByCategory[displayCategoryToDelete] ? displayItemsOrderByCategory[displayCategoryToDelete].length : 0);
    }, [displayCategoryToDelete, displayItemsOrderByCategory]);

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
                    render: 'Fehler beim Speichern der Kategorie: ' + errorMessage(error),
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
                    render: 'Fehler beim Speichern der Kategorie: ' + errorMessage(error),
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
        const toastId = toast.loading('Kategorie wird gelöscht...');
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
                    render: 'Fehler beim Löschen der Kategorie: ' + errorMessage(error),
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

    const handleSubmitAddDisplayItem = async (submittedDisplayItem: DisplayItemInputDTO) => {
        const toastId = toast.loading('Kategorie wird gespeichert...');
        return addDisplayItemMutation.mutate(submittedDisplayItem, {
            onSuccess: () => {
                toast.update(toastId, {
                    render: 'Anzeigeelement erfolgreich gespeichert',
                    type: 'success',
                    isLoading: false,
                    autoClose: 5000,
                });
                navigate("/manage/displayItems");
                setDisplayCategoryToAddTo(undefined);
            },
            onError: (error: unknown) => {
                toast.update(toastId, {
                    render: 'Fehler beim Speichern des Anzeigeelements: ' + errorMessage(error),
                    type: 'error',
                    isLoading: false,
                    autoClose: 5000,
                });
            }
        });
    };

    const handleSubmitUpdateDisplayItem = async (submittedDisplayItem: DisplayItemInputDTO, displayItemId: string) => {
        const toastId = toast.loading('Kategorie wird gespeichert...');
        return updateDisplayItemMutation.mutate({...submittedDisplayItem, id: displayItemId}, {
            onSuccess: () => {
                toast.update(toastId, {
                    render: 'Anzeigeelement erfolgreich gespeichert',
                    type: 'success',
                    isLoading: false,
                    autoClose: 5000,
                });
                navigate("/manage/displayItems");
            },
            onError: (error: unknown) => {
                toast.update(toastId, {
                    render: 'Fehler beim Speichern des Anzeigeelements: ' + errorMessage(error),
                    type: 'error',
                    isLoading: false,
                    autoClose: 5000,
                });
            }
        });
    };

    const handleDeleteDisplayItem = async (id?: string) => {
        if (!id) {
            return;
        }
        setDisplayItemToDelete(undefined);
        const toastId = toast.loading('Anzeigeelement wird gelöscht...');
        return deleteDisplayItemMutation.mutate(id, {
            onSuccess: () => {
                toast.update(toastId, {
                    render: 'Anzeigeelement erfolgreich gelöscht',
                    type: 'success',
                    isLoading: false,
                    autoClose: 5000,
                });
                navigate("/manage/displayItems");
            },
            onError: () => (error: unknown) => {
                toast.update(toastId, {
                    render: 'Fehler beim Löschen des Anzeigeelements: ' + errorMessage(error),
                    type: 'error',
                    isLoading: false,
                    autoClose: 5000,
                });
            }
        });
    }

    const handleDeleteDisplayItemConfirm = async (displayItemId: string) => {
        setDisplayItemToDelete(displayItemId);
    }

    const handleCancel = () => {
        navigate("/manage/displayItems");
        setDisplayCategoryToAddTo(undefined);
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
                                                                                      displayItemsOrder={displayItemsOrderByCategory[displayCategory.id] ?? []}
                                                                                      setDisplayItemsOrder={(order: string[]) => setDisplayItemsOrder(displayCategory.id, order)}
                                                                                      onSubmit={handleSubmitUpdateDisplayCategory}
                                                                                      onDisplayItemSubmit={handleSubmitUpdateDisplayItem}
                                                                                      onDisplayItemDelete={handleDeleteDisplayItemConfirm}
                                                                                      onAddDisplayItemClicked={() => setDisplayCategoryToAddTo(displayCategory.id)}
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
                    Sind Sie sicher, dass Sie die Kategorie {!!countItemsInCategoryToDelete && ("incl. " + countItemsInCategoryToDelete +" Element(en) ")}löschen möchten?
                </BeDialog>
                <BeDialog
                    onClose={() => setDisplayItemToDelete(undefined)}
                    open={!!displayItemToDelete}
                    icon={faWarning}
                    iconClassName="text-danger"
                    className="border border-danger"
                    actions={<>
                        <BeButton onClick={() => setDisplayItemToDelete(undefined)} className="btn btn-neutral">Abbrechen</BeButton>
                        <BeButton onClick={() => handleDeleteDisplayItem(displayItemToDelete)} className="btn btn-danger">Löschen</BeButton>
                    </>}>
                    Sind Sie sicher, dass Sie das Anzeigeelement löschen möchten?
                </BeDialog>
                <BeDialog
                    onClose={() => setDisplayCategoryToAddTo(undefined)}
                    open={!!displayCategoryToAddTo}
                    className="px-4 pt-2 pb-4"
                >
                    <h2 className="text-sm pb-2">Anzeigeelement anlegen</h2>
                    <div className="bg-gray-200 text-gray-700 rounded-md overflow-hidden border border-gray-200 p-2">
                      <DisplayItemAdd onSubmit={handleSubmitAddDisplayItem} onCancel={handleCancel} categoryId={displayCategoryToAddTo ?? ""} />
                    </div>
                </BeDialog>
            </SortableContext>
        </DndContext>
    );
};

export default DisplayItemsPage;
