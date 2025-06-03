import axios from "axios";
import {type DisplayCategoryOutputDTO, isDisplayCategoryOutputDTO} from "../types/DisplayCategoryOutputDTO.ts";
import type {DisplayCategoryInputDTO, DisplayCategoryInputDTOWithId} from "../types/DisplayCategoryInputDTO.ts";

export const DisplayCategoriesApi = {
    baseUrl: '/api/displayCategories',
    cancelableGetAllRef: null as AbortController | null,
    cancelableSaveDisplayCategoryRef: null as AbortController | null,
    cancelableUpdateDisplayCategoryRef: {} as Record<string, AbortController | null>,
    cancelableDeleteDisplayCategoryRef: {} as Record<string, AbortController | null>,

    async getAllDisplayCategories(): Promise<DisplayCategoryOutputDTO[]> {
        DisplayCategoriesApi.cancelableGetAllRef?.abort();
        DisplayCategoriesApi.cancelableGetAllRef = new AbortController();

        const response = await axios.get(DisplayCategoriesApi.baseUrl, {
            signal: DisplayCategoriesApi.cancelableGetAllRef.signal,
        });
        if (Array.isArray(response.data) && response.data.every(isDisplayCategoryOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Laden der Kategorieliste");
    },

    async saveDisplayCategoriesPositions(displayCategoriesOrder: string[]  ): Promise<DisplayCategoryOutputDTO[]> {
        DisplayCategoriesApi.cancelableGetAllRef?.abort();
        DisplayCategoriesApi.cancelableGetAllRef = new AbortController();

        const response = await axios.put(
            DisplayCategoriesApi.baseUrl + '/positions',
            displayCategoriesOrder.map((displayCategoryId, index) => ({index, id: displayCategoryId})),
            {
                signal: DisplayCategoriesApi.cancelableGetAllRef.signal,
            }
        )
        if (Array.isArray(response.data) && response.data.every(isDisplayCategoryOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Laden der Kategorieliste");
    },

    async saveDisplayCategory(submittedDisplayCategory: DisplayCategoryInputDTO): Promise<DisplayCategoryOutputDTO | null> {
        DisplayCategoriesApi.cancelableSaveDisplayCategoryRef?.abort();
        DisplayCategoriesApi.cancelableSaveDisplayCategoryRef = new AbortController();

        try {
            const response = await axios.post(DisplayCategoriesApi.baseUrl, submittedDisplayCategory, {
                signal: DisplayCategoriesApi.cancelableSaveDisplayCategoryRef.signal
            });
            if (isDisplayCategoryOutputDTO(response.data)) {
                return response.data
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return null;
            }
        }
        throw new TypeError("Ungültige Antwort beim Speichern der Kategorien");
    },

    async updateDisplayCategory(submittedDisplayCategory: DisplayCategoryInputDTOWithId): Promise<DisplayCategoryOutputDTO | null> {
        const displayCategoryId = submittedDisplayCategory.id;
        if (!displayCategoryId) {
            throw new TypeError("Fehlende Id beim Speichern der Kategorien");
        }
        DisplayCategoriesApi.cancelableUpdateDisplayCategoryRef[displayCategoryId]?.abort();
        DisplayCategoriesApi.cancelableUpdateDisplayCategoryRef[displayCategoryId] = new AbortController();

        try {
            const response = await axios.put('/api/displayCategories/' + displayCategoryId, submittedDisplayCategory, {
                signal: DisplayCategoriesApi.cancelableUpdateDisplayCategoryRef[displayCategoryId]?.signal
            });
            if (isDisplayCategoryOutputDTO(response.data)) {
                return response.data
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return null;
            }
        }
        throw new TypeError("Ungültige Antwort beim Speichern der Kategorien");
    },

    async deleteDisplayCategory(displayCategoryId: string): Promise<void> {
        DisplayCategoriesApi.cancelableDeleteDisplayCategoryRef[displayCategoryId]?.abort();
        DisplayCategoriesApi.cancelableDeleteDisplayCategoryRef[displayCategoryId] = new AbortController();

        try {
            await axios.delete('/api/displayCategories/' + displayCategoryId, {
                signal: DisplayCategoriesApi.cancelableDeleteDisplayCategoryRef[displayCategoryId]?.signal
            });
            return;
        } catch (error) {
            if (axios.isCancel(error)) {
                return;
            }
        }
        throw new TypeError("Ungültige Antwort beim Löschen der Kategorien");
    }
}