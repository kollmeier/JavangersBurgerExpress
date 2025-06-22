import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {toast} from "react-toastify";
import {usePageLayoutContext} from "@/context/page-layout-context.ts";

import {BeCircleLink} from "@/components/ui/be-circle-link.tsx";
import MinimalCard from "@/components/shared/minimal-card.tsx";
import BeButton from "@/components/ui/be-button.tsx";
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
import {DragDropProvider} from "@dnd-kit/react"
import {move} from "@dnd-kit/helpers";
import {CircleX, FolderPlus, SquareDashed, SquareDashedTopSolid, Trash, TriangleAlert} from 'lucide-react';

const DisplayItemsPage: React.FC = () => {
    const {data: displayCategories} = useDisplayCategories();
    const [displayItems, setDisplayItems] = useState<DisplayItemOutputDTO[]>();

    const [displayItemsOrderByCategory, setDisplayItemsOrderByCategory] = useState<{[categoryId: string]: string[]}>({});
    const [displayCategoriesOrder, setDisplayCategoriesOrder] = useState(() => Object.keys(displayItemsOrderByCategory ?? []));

    const {updateData: setDisplayCategories, savePositionsMutation: saveCategoryPositionsMutation, addDisplayCategoryMutation, updateDisplayCategoryMutation, deleteDisplayCategoryMutation} = useDisplayCategoryMutations();
    const {savePositionsMutation: saveItemPositionsMutation, addDisplayItemMutation, updateDisplayItemMutation, deleteDisplayItemMutation} = useDisplayItemMutations(setDisplayCategories);

    const displayCategoryId = useParams().displayCategoryId;

    const [displayCategoryToDelete, setDisplayCategoryToDelete] = React.useState<string | undefined>(undefined);
    const [displayItemToDelete, setDisplayItemToDelete] = React.useState<string | undefined>(undefined);
    const [displayCategoryToAddTo, setDisplayCategoryToAddTo] = React.useState<string | undefined>(undefined);

    const [countItemsInCategoryToDelete, setCountItemsInCategoryToDelete] = React.useState<number>(0);
    const [areCategoriesDraggable, setAreCategoriesDraggable] = React.useState<boolean>(false);

    const navigate = useNavigate();

    const {setSubHeader, setActions} = usePageLayoutContext();

    useEffect(() => {
        setSubHeader("Anzeige-Elemente");
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        setActions(
            <BeButton
                onClick={() => setAreCategoriesDraggable(!areCategoriesDraggable)}
                className="btn btn-neutral w-fit"
            > {areCategoriesDraggable ? <><SquareDashed /> Elemente anordnen</> :<><SquareDashedTopSolid /> Kategorien anordnen</>}</BeButton>
        );
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [areCategoriesDraggable]);

    useEffect(() => {
        if (displayCategories && displayCategories.length > 0) {
            setDisplayCategoriesOrder(displayCategories.map((displayCategory: DisplayCategoryOutputDTO) => displayCategory.id));
            setDisplayItems(displayCategories.flatMap((displayCategory: DisplayCategoryOutputDTO) => displayCategory.displayItems ?? []));
        }
    }, [displayCategories]);

    useEffect(() => {
        if (displayCategoriesOrder && displayCategoriesOrder.length > 0) {
            saveCategoryPositionsMutation.mutate(displayCategoriesOrder);
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [displayCategoriesOrder]);

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

    const displayCategoryForId = (id: string) => {
        return displayCategories?.find((category: DisplayCategoryOutputDTO) => category.id === id);
    }

    const displayItemForId = (id: string) => {
        return displayItems?.find((item: DisplayItemOutputDTO) => item.id === id);
    }

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

    return (
        <DragDropProvider
            onDragOver={(event) => {
                const {source} = event.operation;

                if (source?.type === "displayCategory") {
                    return;
                }

                setDisplayItemsOrderByCategory((items) => move(items, event))
            }}
            onDragEnd={(event) => {
                const {source} = event.operation;

                if (event.canceled) {
                    return;
                }

                if (source?.type === "displayCategory") {
                    setDisplayCategoriesOrder((categories) => move(categories, event));

                    return;
                }

                if (source?.type === "displayItem") {
                    if (displayItemsOrderByCategory && Object.values(displayItemsOrderByCategory).length > 0 && displayItems && displayItems.length > 0) {
                        saveItemPositionsMutation.mutate(
                            Object.values(displayItemsOrderByCategory)
                                .flatMap(displayItemIds => displayItemIds)
                                .map(displayItemId => displayItemForId(displayItemId))
                                .map((displayItem, index) => (displayItem && {
                                    index,
                                    id: displayItem.id,
                                    parentId: displayItem.categoryId,
                                }))
                                .filter(item => !!item)
                        );
                    }
                }
            }}
        >
                <div className="grid grid-cols-1 auto-rows-min sm:grid-cols-2 xl:grid-cols-3 gap-6">
                    <div className="col-span-1 sm:col-span-2 xl:col-span-3">
                        <MinimalCard className={cn("h-fit max-h-30 transition-[max-height] duration-300 ease-in-out flex justify-center", displayCategoryId === 'add' && "max-h-50")} colorVariant={colorMapCards.displayCategory}>
                            {displayCategoryId !== 'add' ? (
                                <BeCircleLink icon={FolderPlus} to="/manage/displayItems/category/add">Kategorie hinzufügen</BeCircleLink>
                            ) : (
                                <DisplayCategoryAdd onSubmit={handleSubmitAddDisplayCategory} onCancel={handleCancel} className="flex-1"/>
                            )}
                        </MinimalCard>
                    </div>
                    {displayCategoriesOrder.map(displayCategoryId => displayCategoryForId(displayCategoryId))
                        .map((displayCategory, index) =>
                            displayCategory && <DisplayCategoryItem key={displayCategory.id}
                                  className="col-span-1 sm:col-span-2 xl:col-span-3 min-h-5"
                                  id={displayCategory.id}
                                  index={index}
                                  displayCategory={displayCategory}
                                  onSubmit={handleSubmitUpdateDisplayCategory}
                                  onDisplayItemSubmit={handleSubmitUpdateDisplayItem}
                                  onDisplayItemDelete={handleDeleteDisplayItemConfirm}
                                  onAddDisplayItemClicked={() => setDisplayCategoryToAddTo(displayCategory.id)}
                                  onDelete={handleDeleteDisplayCategoryConfirm}
                                  onCancel={handleCancel}
                                  isDraggable={areCategoriesDraggable}/>)}
                </div>
                <BeDialog
                    onClose={() => setDisplayCategoryToDelete(undefined)}
                    open={!!displayCategoryToDelete}
                    icon={TriangleAlert}
                    iconClassName="text-danger"
                    className="border border-danger"
                    actions={<>
                        <BeButton onClick={() => setDisplayCategoryToDelete(undefined)} className="btn btn-neutral"><CircleX />Abbrechen</BeButton>
                        <BeButton onClick={() => handleDeleteDisplayCategory(displayCategoryToDelete)} className="btn btn-danger"><Trash />Löschen</BeButton>
                    </>}>
                    Sind Sie sicher, dass Sie die Kategorie {!!countItemsInCategoryToDelete && ("incl. " + countItemsInCategoryToDelete +" Element(en) ")}löschen möchten?
                </BeDialog>
                <BeDialog
                    onClose={() => setDisplayItemToDelete(undefined)}
                    open={!!displayItemToDelete}
                    icon={TriangleAlert}
                    iconClassName="text-danger"
                    className="border border-danger"
                    actions={<>
                        <BeButton onClick={() => setDisplayItemToDelete(undefined)} className="btn btn-neutral"><CircleX />Abbrechen</BeButton>
                        <BeButton onClick={() => handleDeleteDisplayItem(displayItemToDelete)} className="btn btn-danger"><Trash />Löschen</BeButton>
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
    </DragDropProvider>
    );
};

export default DisplayItemsPage;
