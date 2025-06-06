import axios from "axios";
import {type OrderableItemOutputDTO, isOrderableItemOutputDTO} from "../types/OrderableItemOutputDTO.ts";

export const OrderableItemsApi = {
    baseUrl: '/api/orderable-items',
    cancelableGetAllRef: null as AbortController | null,
    cancelableGetMenusRef: null as AbortController | null,
    cancelableGetDishesRef: null as AbortController | null,

    async getAllOrderableItems(): Promise<OrderableItemOutputDTO[]> {
        OrderableItemsApi.cancelableGetAllRef?.abort();
        OrderableItemsApi.cancelableGetAllRef = new AbortController();

        const response = await axios.get(OrderableItemsApi.baseUrl, {
            signal: OrderableItemsApi.cancelableGetAllRef.signal,
        });
        if (Array.isArray(response.data) && response.data.every(isOrderableItemOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Invalid response when loading orderable items");
    },

    async getAllMenus(): Promise<OrderableItemOutputDTO[]> {
        OrderableItemsApi.cancelableGetMenusRef?.abort();
        OrderableItemsApi.cancelableGetMenusRef = new AbortController();

        const response = await axios.get(`${OrderableItemsApi.baseUrl}/menus`, {
            signal: OrderableItemsApi.cancelableGetMenusRef.signal,
        });
        if (Array.isArray(response.data) && response.data.every(isOrderableItemOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Invalid response when loading menus");
    },

    async getAllDishes(): Promise<OrderableItemOutputDTO[]> {
        OrderableItemsApi.cancelableGetDishesRef?.abort();
        OrderableItemsApi.cancelableGetDishesRef = new AbortController();

        const response = await axios.get(`${OrderableItemsApi.baseUrl}/dishes`, {
            signal: OrderableItemsApi.cancelableGetDishesRef.signal,
        });
        if (Array.isArray(response.data) && response.data.every(isOrderableItemOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Invalid response when loading dishes");
    }
}