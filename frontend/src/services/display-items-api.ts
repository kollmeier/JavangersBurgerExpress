import axios from "axios";
import {type DisplayItemOutputDTO, isDisplayItemOutputDTO} from "../types/DisplayItemOutputDTO.ts";
import type {DisplayItemInputDTO, DisplayItemInputDTOWithId} from "../types/DisplayItemInputDTO.ts";
import type {SortedInputDTO} from "../types/SortedInputDTO.ts";
import {throwErrorByResponse} from "@/util/errors.ts";

export const DisplayItemsApi = {
    baseUrl: '/api/displayItems',
    cancelableGetAllRef: null as AbortController | null,
    cancelableSaveDisplayItemRef: null as AbortController | null,
    cancelableUpdateDisplayItemRef: {} as Record<string, AbortController | null>,
    cancelableDeleteDisplayItemRef: {} as Record<string, AbortController | null>,

    async getAllDisplayItems(): Promise<DisplayItemOutputDTO[]> {
        DisplayItemsApi.cancelableGetAllRef?.abort();
        DisplayItemsApi.cancelableGetAllRef = new AbortController();

        const response = await axios.get(DisplayItemsApi.baseUrl, {
            signal: DisplayItemsApi.cancelableGetAllRef.signal,
        });
        if (Array.isArray(response.data) && response.data.every(isDisplayItemOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Laden der Anzeigeelement-Liste");
    },

    async saveDisplayItemsPositions(sortedItems: SortedInputDTO[]): Promise<DisplayItemOutputDTO[]> {
        DisplayItemsApi.cancelableGetAllRef?.abort();
        DisplayItemsApi.cancelableGetAllRef = new AbortController();

        const response = await axios.put(
            DisplayItemsApi.baseUrl + '/positions',
            sortedItems,
            {
                signal: DisplayItemsApi.cancelableGetAllRef.signal,
            }
        )
        if (Array.isArray(response.data) && response.data.every(isDisplayItemOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Laden der Anzeigeelement-Liste");
    },

    async saveDisplayItem(submittedDisplayItem: DisplayItemInputDTO): Promise<DisplayItemOutputDTO | null> {
        DisplayItemsApi.cancelableSaveDisplayItemRef?.abort();
        DisplayItemsApi.cancelableSaveDisplayItemRef = new AbortController();

        try {
            const response = await axios.post(DisplayItemsApi.baseUrl, submittedDisplayItem, {
                signal: DisplayItemsApi.cancelableSaveDisplayItemRef.signal
            });
            if (isDisplayItemOutputDTO(response.data)) {
                return response.data
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return null;
            }
            throwErrorByResponse(error);
        }
        throw new TypeError("Ungültige Antwort beim Speichern des Anzeigeelements");
    },

    async updateDisplayItem(submittedDisplayItem: DisplayItemInputDTOWithId): Promise<DisplayItemOutputDTO | null> {
        const displayItemId = submittedDisplayItem.id;
        if (!displayItemId) {
            throw new TypeError("Fehlende Id beim Speichern des Anzeigeelements");
        }
        DisplayItemsApi.cancelableUpdateDisplayItemRef[displayItemId]?.abort();
        DisplayItemsApi.cancelableUpdateDisplayItemRef[displayItemId] = new AbortController();

        try {
            const response = await axios.put('/api/displayItems/' + displayItemId, submittedDisplayItem, {
                signal: DisplayItemsApi.cancelableUpdateDisplayItemRef[displayItemId]?.signal
            });
            if (isDisplayItemOutputDTO(response.data)) {
                return response.data
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return null;
            }
            throwErrorByResponse(error);
        }
        throw new TypeError("Ungültige Antwort beim Speichern des Anzeigeelements");
    },

    async deleteDisplayItem(displayItemId: string): Promise<void> {
        DisplayItemsApi.cancelableDeleteDisplayItemRef[displayItemId]?.abort();
        DisplayItemsApi.cancelableDeleteDisplayItemRef[displayItemId] = new AbortController();

        try {
            await axios.delete('/api/displayItems/' + displayItemId, {
                signal: DisplayItemsApi.cancelableDeleteDisplayItemRef[displayItemId]?.signal
            });
            return;
        } catch (error) {
            if (axios.isCancel(error)) {
                return;
            }
            throwErrorByResponse(error);
        }
        throw new TypeError("Ungültige Antwort beim Löschen des Anzeigeelements");
    }
}
