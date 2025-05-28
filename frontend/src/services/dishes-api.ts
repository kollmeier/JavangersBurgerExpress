import axios from "axios";
import {type DishOutputDTO, isDishOutputDTO} from "../types/DishOutputDTO.ts";
import type {DishInputDTO, DishInputDTOWithId} from "../types/DishInputDTO.ts";

export const DishesApi = {
    baseUrl: '/api/dishes',
    cancelableGetAllRef: null as AbortController | null,
    cancelableSaveDishRef: null as AbortController | null,
    cancelableUpdateDishRef: {} as Record<string, AbortController | null>,
    cancelableDeleteDishRef: {} as Record<string, AbortController | null>,

    async getAllDishes(): Promise<DishOutputDTO[]> {
        DishesApi.cancelableGetAllRef?.abort();
        DishesApi.cancelableGetAllRef = new AbortController();

        const response = await axios.get(DishesApi.baseUrl, {
            signal: DishesApi.cancelableGetAllRef.signal,
        });
        if (Array.isArray(response.data) && response.data.every(isDishOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Laden der Gerichtliste");
    },

    async saveDishesPositions(dishesOrder: string[]  ): Promise<DishOutputDTO[]> {
        DishesApi.cancelableGetAllRef?.abort();
        DishesApi.cancelableGetAllRef = new AbortController();

        const response = await axios.put(
            DishesApi.baseUrl + '/positions',
            dishesOrder.map((dishId, index) => ({index, id: dishId})),
            {
                signal: DishesApi.cancelableGetAllRef.signal,
            }
        )
        if (Array.isArray(response.data) && response.data.every(isDishOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Laden der Gerichtliste");
    },

    async saveDish(submittedDish: DishInputDTO): Promise<DishOutputDTO | null> {
        DishesApi.cancelableSaveDishRef?.abort();
        DishesApi.cancelableSaveDishRef = new AbortController();

        try {
            const response = await axios.post(DishesApi.baseUrl, submittedDish, {
                signal: DishesApi.cancelableSaveDishRef.signal
            });
            if (isDishOutputDTO(response.data)) {
                return response.data
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return null;
            }
        }
        throw new TypeError("Ungültige Antwort beim Speichern des Gerichts");
    },

    async updateDish(submittedDish: DishInputDTOWithId): Promise<DishOutputDTO | null> {
        const dishId = submittedDish.id;
        if (!dishId) {
            throw new TypeError("Fehlende Id beim Speichern des Gerichts");
        }
        DishesApi.cancelableUpdateDishRef[dishId]?.abort();
        DishesApi.cancelableUpdateDishRef[dishId] = new AbortController();

        try {
            const response = await axios.put('/api/dishes/' + dishId, submittedDish, {
                signal: DishesApi.cancelableUpdateDishRef[dishId]?.signal
            });
            if (isDishOutputDTO(response.data)) {
                return response.data
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return null;
            }
        }
        throw new TypeError("Ungültige Antwort beim Speichern des Gerichts");
    },

    async deleteDish(dishId: string): Promise<void> {
        DishesApi.cancelableDeleteDishRef[dishId]?.abort();
        DishesApi.cancelableDeleteDishRef[dishId] = new AbortController();

        try {
            await axios.delete('/api/dishes/' + dishId, {
                signal: DishesApi.cancelableDeleteDishRef[dishId]?.signal
            });
            return;
        } catch (error) {
            if (axios.isCancel(error)) {
                return;
            }
        }
        throw new TypeError("Ungültige Antwort beim Löschen des Gerichts");
    }
}