import React, {useEffect, useState} from 'react';
import { useNavigate, useParams} from 'react-router-dom';
import MenuAdd from "../components/menu-add.tsx";
import {toast} from "react-toastify";
import {usePageLayoutContext} from "@/context/page-layout-context.ts";
import MenuItem from "../components/menu-item.tsx";
import type {MenuInputDTO} from "@/types/MenuInputDTO.ts";

import {BeCircleLink} from "@/components/ui/be-circle-link.tsx";
import MinimalCard from "@/components/shared/minimal-card.tsx";
import {isAxiosError} from "axios";
import BeButton from "@/components/ui/be-button.tsx";
import {useMenus} from "@/util";
import {useMenuMutations} from "@/hooks/use-menu-mutations.ts";
import BeDialog from "@/components/shared/be-dialog.tsx";
import {MenuOutputDTO} from "@/types/MenuOutputDTO.ts";
import {colorMapCards} from "@/data";
import {DragDropProvider} from "@dnd-kit/react";
import {move} from "@dnd-kit/helpers";
import {CircleX, ClipboardPlus, Trash, TriangleAlert} from "lucide-react";

const MenusPage: React.FC = () => {
    const menus = useMenus();
    const [menusOrder, setMenusOrder] = useState<string[]>([]);

    const {savePositionsMutation, addMenuMutation, updateMenuMutation, deleteMenuMutation} = useMenuMutations();

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

    return (
        <DragDropProvider
            onDragEnd={(event) => {
                setMenusOrder(order => move(order, event))
            }
            }>
            <div className="grid grid-cols-1 auto-rows-fr sm:grid-cols-2 xl:grid-cols-3 gap-6">
                <MinimalCard className={"min-h-64"} colorVariant={colorMapCards.menu}>
                    {menuId !== 'add-main' ? (
                        <BeCircleLink icon={ClipboardPlus} to="/manage/menus/add-main">Menü hinzufügen</BeCircleLink>
                    ) : (
                        <MenuAdd onSubmit={handleSubmitAddMenu} onCancel={handleCancel}/>
                    )}
                </MinimalCard>
                {menus?.map((menu, index) => <MenuItem key={menu.id}
                                                       index={index}
                                                       menu={menu}
                                                       onSubmit={handleSubmitUpdateMenu}
                                                       onDelete={handleDeleteMenuConfirm}
                                                       onCancel={handleCancel}/>)}
            </div>
            <BeDialog
                onClose={() => setMenuToDelete(undefined)}
                open={!!menuToDelete}
                icon={TriangleAlert}
                iconClassName="text-danger"
                className="border border-danger"
                actions={<>
                    <BeButton onClick={() => setMenuToDelete(undefined)} className="btn btn-neutral"><CircleX />Abbrechen</BeButton>
                    <BeButton onClick={() => handleDeleteMenu(menuToDelete)} className="btn btn-danger"><Trash />Löschen</BeButton>
                </>}>
                Sind Sie sicher, dass Sie das Menü löschen möchten?
            </BeDialog>
        </DragDropProvider>
    );
};

export default MenusPage;