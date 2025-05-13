import axios from "axios";
import {type DishOutputDTO, isDishOutputDTO} from "../types/DishOutputDTO.ts";
import type {DishInputDTO} from "../types/DishInputDTO.ts";

export const DishesApi = {
    baseUrl: '/api/dishes',

    cancelableGetAllRef: null as AbortController | null,
    cancelableSavePositionsRef: null as AbortController | null,
    cancelableSaveDishRef: null as AbortController | null,
    cancelableUpdateDishRef: {} as Record<string, AbortController | null>,
    cancelableDeleteDishRef: {} as Record<string, AbortController | null>,

    async getAllDishes(): Promise<DishOutputDTO[]> {
        this.cancelableGetAllRef?.abort();
        this.cancelableGetAllRef = new AbortController();

        try {
            const response = await axios.get(this.baseUrl, {
                signal: this.cancelableGetAllRef.signal
            });
            if (Array.isArray(response.data) && response.data.every(isDishOutputDTO)) {
                return response.data;
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return [];
            }
        }
        throw new TypeError("Ungültige Antwort beim Laden der Gerichtliste");
    },

    async saveDish(submittedDish: DishInputDTO): Promise<DishOutputDTO | null> {
        this.cancelableSaveDishRef?.abort();
        this.cancelableSaveDishRef = new AbortController();

        try {
            const response = await axios.post(this.baseUrl, submittedDish, {
                signal: this.cancelableSaveDishRef.signal
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

    async updateDish(submittedDish: DishInputDTO, dishId: string): Promise<DishOutputDTO | null> {
        this.cancelableUpdateDishRef[dishId]?.abort();
        this.cancelableUpdateDishRef[dishId] = new AbortController();

        try {
            const response = await axios.put('/api/dishes/' + dishId, submittedDish, {
                signal: this.cancelableUpdateDishRef[dishId]?.signal
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
        this.cancelableDeleteDishRef[dishId]?.abort();
        this.cancelableDeleteDishRef[dishId] = new AbortController();

        try {
            await axios.delete('/api/dishes/' + dishId, {
                signal: this.cancelableDeleteDishRef[dishId]?.signal
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