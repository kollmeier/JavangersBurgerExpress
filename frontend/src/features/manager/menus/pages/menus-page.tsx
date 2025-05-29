import React, {useEffect, useState} from 'react';
import { useNavigate, useParams} from 'react-router-dom';
import { faPlus } from '@fortawesome/free-solid-svg-icons/faPlus';
import MenuAdd from "../components/menu-add.tsx";
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
import MenuItem from "../components/menu-item.tsx";
import type {MenuInputDTO} from "@/types/MenuInputDTO.ts";

import {BeCircleLink} from "@/components/ui/be-circle-link.tsx";
import MinimalCard from "@/components/shared/minimal-card.tsx";
import {isAxiosError} from "axios";
import BeButton from "@/components/ui/be-button.tsx";
import {faWarning} from "@fortawesome/free-solid-svg-icons";
import {useMenus} from "@/util";
import {useMenuMutations} from "@/hooks/use-menu-mutations.ts";
import BeDialog from "@/components/shared/be-dialog.tsx";
import {MenuOutputDTO} from "@/types/MenuOutputDTO.ts";

const MenusPage: React.FC = () => {
    const menus = useMenus();
    const [menusOrder, setMenusOrder] = useState<string[]>([]);

    const {savePositionsMutation, addMenuMutation, updateMenuMutation, deleteMenuMutation} = useMenuMutations();

    const sensors = useSensors(
        useSensor(PointerSensor),
        useSensor(KeyboardSensor, {
            coordinateGetter: sortableKeyboardCoordinates,
        })
    );

    const menuId = useParams().menuId;

    const [menuToDelete, setMenuToDelete] = React.useState<string | undefined>(undefined);

    const navigate = useNavigate();

    const {setSubHeader} = usePageLayoutContext();

    useEffect(() => {
        setSubHeader("Menüs");
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        if (menus && menus.length > 0) {
            setMenusOrder(menus.map((menu: MenuOutputDTO) => menu.id));
        }
    }, [menus]);

    useEffect(() => {
        savePositionsMutation.mutate(menusOrder, {
            onError: () => toast.error('Fehler beim Speichern der Positionen.')
        });
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [menusOrder]);

    const handleSubmitAddMenu = async (submittedMenu: MenuInputDTO) => {
        const toastId = toast.loading('Menü wird gespeichert...');
        return addMenuMutation.mutate(submittedMenu, {
            onSuccess: () => {
                toast.update(toastId, {
                    render: 'Menü erfolgreich gespeichert',
                    type: 'success',
                    isLoading: false,
                    autoClose: 5000,
                });
                navigate("/manage/menus");
            },
            onError: (error: unknown) => {
                toast.update(toastId, {
                    render: 'Fehler beim Speichern des Menüs: ' + (isAxiosError(error) && error.message),
                    type: 'error',
                    isLoading: false,
                    autoClose: 5000,
                });
            }
        });
    };

    const handleSubmitUpdateMenu = async (submittedMenu: MenuInputDTO, menuId: string) => {
        const toastId = toast.loading('Menü wird gespeichert...');
        return updateMenuMutation.mutate({...submittedMenu, id: menuId}, {
            onSuccess: () => {
                toast.update(toastId, {
                    render: 'Menü erfolgreich gespeichert',
                    type: 'success',
                    isLoading: false,
                    autoClose: 5000,
                });
                navigate("/manage/menus");
            },
            onError: (error: unknown) => {
                toast.update(toastId, {
                    render: 'Fehler beim Speichern des Menüs: ' + (isAxiosError(error) && error.message),
                    type: 'error',
                    isLoading: false,
                    autoClose: 5000,
                });
            }
        });
    };

    const handleDeleteMenu = async (id?: string) => {
        if (!id) {
            return;
        }
        setMenuToDelete(undefined);
        const toastId = toast.loading('Menü wird gelöscht...');
        return deleteMenuMutation.mutate(id, {
            onSuccess: () => {
                toast.update(toastId, {
                    render: 'Menü erfolgreich gelöscht',
                    type: 'success',
                    isLoading: false,
                    autoClose: 5000,
                });
                navigate("/manage/menus");
            },
            onError: () => (error: unknown) => {
                toast.update(toastId, {
                    render: 'Fehler beim Löschen des Menüs: ' + (isAxiosError(error) && error.message),
                    type: 'error',
                    isLoading: false,
                    autoClose: 5000,
                });
            }
        });
    }

    const handleDeleteMenuConfirm = async (menuId: string) => {
        setMenuToDelete(menuId);
    }

    const handleCancel = () => {
        navigate("/manage/menus");
    }

    function handleDragEnd(event: DragEndEvent) {
        const {active, over} = event;
        if (!active || !over) {
            return;
        }

        if (active.id !== over.id) {
            setMenusOrder((menusOrder) => {
                const oldIndex = menusOrder.indexOf(active.id + "");
                const newIndex = menusOrder.indexOf(over.id + "");

                return arrayMove(menusOrder, oldIndex, newIndex);
            });
        }
    }

    return (
        <DndContext collisionDetection={closestCenter} sensors={sensors} onDragEnd={handleDragEnd}>
                <SortableContext items={menusOrder} strategy={rectSortingStrategy}>
                <div className="grid grid-cols-1 auto-rows-fr sm:grid-cols-2 xl:grid-cols-3 gap-6">
            <MinimalCard className={"grow-1 basis-30 min-h-64"}  colorVariant="red">
                {menuId !== 'add-main' ? (
                    <BeCircleLink icon={faPlus} to="/manage/menus/add-main">Menü hinzufügen</BeCircleLink>
                ) : (
                    <MenuAdd onSubmit={handleSubmitAddMenu} onCancel={handleCancel}/>
                )}
            </MinimalCard>
            {menus?.map((menu) => <MenuItem key={menu.id}
                                             className="grow-1 basis-30"
                                             id={menu.id}
                                             menu={menu}
                                             onSubmit={handleSubmitUpdateMenu}
                                             onDelete={handleDeleteMenuConfirm}
                                             onCancel={handleCancel}/>)}
        </div>
            <BeDialog
                onClose={() => setMenuToDelete(undefined)}
                open={!!menuToDelete}
                icon={faWarning}
                iconClassName="text-danger"
                className="border border-danger"
                actions={<>
                    <BeButton onClick={() => setMenuToDelete(undefined)} className="btn btn-neutral">Abbrechen</BeButton>
                    <BeButton onClick={() => handleDeleteMenu(menuToDelete)} className="btn btn-danger">Löschen</BeButton>
                </>}>
                    Sind Sie sicher, dass Sie das Menü löschen möchten?
            </BeDialog>
                </SortableContext>
        </DndContext>
    );
};

export default MenusPage;