import axios from "axios";
import {type MenuOutputDTO, isMenuOutputDTO} from "../types/MenuOutputDTO.ts";
import type {MenuInputDTO, MenuInputDTOWithId} from "../types/MenuInputDTO.ts";

export const MenusApi = {
    baseUrl: '/api/menus',
    cancelableGetAllRef: null as AbortController | null,
    cancelableSaveMenuRef: null as AbortController | null,
    cancelableUpdateMenuRef: {} as Record<string, AbortController | null>,
    cancelableDeleteMenuRef: {} as Record<string, AbortController | null>,

    async getAllMenus(): Promise<MenuOutputDTO[]> {
        MenusApi.cancelableGetAllRef?.abort();
        MenusApi.cancelableGetAllRef = new AbortController();

        const response = await axios.get(MenusApi.baseUrl, {
            signal: MenusApi.cancelableGetAllRef.signal,
        });
        if (Array.isArray(response.data) && response.data.every(isMenuOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Laden der Menüliste");
    },

    async saveMenusPositions(menusOrder: string[]  ): Promise<MenuOutputDTO[]> {
        MenusApi.cancelableGetAllRef?.abort();
        MenusApi.cancelableGetAllRef = new AbortController();

        const response = await axios.put(
            MenusApi.baseUrl + '/positions',
            menusOrder.map((menuId, index) => ({index, id: menuId})),
            {
                signal: MenusApi.cancelableGetAllRef.signal,
            }
        )
        if (Array.isArray(response.data) && response.data.every(isMenuOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Laden der Menüliste");
    },

    async saveMenu(submittedMenu: MenuInputDTO): Promise<MenuOutputDTO | null> {
        MenusApi.cancelableSaveMenuRef?.abort();
        MenusApi.cancelableSaveMenuRef = new AbortController();

        try {
            const response = await axios.post(MenusApi.baseUrl, submittedMenu, {
                signal: MenusApi.cancelableSaveMenuRef.signal
            });
            if (isMenuOutputDTO(response.data)) {
                return response.data
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return null;
            }
        }
        throw new TypeError("Ungültige Antwort beim Speichern des Menüs");
    },

    async updateMenu(submittedMenu: MenuInputDTOWithId): Promise<MenuOutputDTO | null> {
        const menuId = submittedMenu.id;
        if (!menuId) {
            throw new TypeError("Fehlende Id beim Speichern des Menüs");
        }
        MenusApi.cancelableUpdateMenuRef[menuId]?.abort();
        MenusApi.cancelableUpdateMenuRef[menuId] = new AbortController();

        try {
            const response = await axios.put('/api/menus/' + menuId, submittedMenu, {
                signal: MenusApi.cancelableUpdateMenuRef[menuId]?.signal
            });
            if (isMenuOutputDTO(response.data)) {
                return response.data
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return null;
            }
        }
        throw new TypeError("Ungültige Antwort beim Speichern des Menüs");
    },

    async deleteMenu(menuId: string): Promise<void> {
        MenusApi.cancelableDeleteMenuRef[menuId]?.abort();
        MenusApi.cancelableDeleteMenuRef[menuId] = new AbortController();

        try {
            await axios.delete('/api/menus/' + menuId, {
                signal: MenusApi.cancelableDeleteMenuRef[menuId]?.signal
            });
            return;
        } catch (error) {
            if (axios.isCancel(error)) {
                return;
            }
        }
        throw new TypeError("Ungültige Antwort beim Löschen des Menüs");
    }
}