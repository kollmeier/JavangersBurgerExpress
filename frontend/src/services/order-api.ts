import axios from "axios";
import {isCustomerSessionDTO} from "@/types/CustomerSessionDTO.ts";
import {isOrderOutputDTO} from "@/types/OrderOutputDTO.ts";

export const OrderApi = {
    baseUrl: '/api/orders',
    cancelableGetOrdersForCustomer: null as AbortController | null,
    cancelableGetOrdersForKitchen: null as AbortController | null,
    cancelableAdvanceOrdersForKitchen: null as AbortController | null,
    cancelableGetOrdersForCashier: null as AbortController | null,
    cancelableAdvanceOrdersForCashier: null as AbortController | null,
    cancelablePlaceOrder: null as AbortController | null,
    cancelableDeleteOrder: null as AbortController | null,

    async placeOrder() {
        OrderApi.cancelablePlaceOrder?.abort();
        OrderApi.cancelablePlaceOrder = new AbortController();

        const response = await axios.post(
            OrderApi.baseUrl,
            {
                signal: OrderApi.cancelablePlaceOrder.signal,
            }
        )
        if (isCustomerSessionDTO(response.data)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Speichern der Bestellung");
        
    },
    
    async deleteOrder() {
        OrderApi.cancelableDeleteOrder?.abort();
        OrderApi.cancelableDeleteOrder = new AbortController();

        const response = await axios.delete(
            OrderApi.baseUrl,
            {
                signal: OrderApi.cancelableDeleteOrder.signal,
            }
        )
        if (isCustomerSessionDTO(response.data)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Löschen der Bestellung");
    },

    async getOrdersForKitchen() {
        OrderApi.cancelableGetOrdersForKitchen?.abort();
        OrderApi.cancelableGetOrdersForKitchen = new AbortController();

        const response = await axios.get(
            OrderApi.baseUrl + "/kitchen",
            {
                signal: OrderApi.cancelableGetOrdersForKitchen.signal,
            }
        )
        if (Array.isArray(response.data) && response.data.every(isOrderOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Laden der Bestellungen");
    },

    async getOrdersForCustomer() {
        OrderApi.cancelableGetOrdersForCustomer?.abort();
        OrderApi.cancelableGetOrdersForCustomer = new AbortController();

        const response = await axios.get(
            OrderApi.baseUrl + "/customer",
            {
                signal: OrderApi.cancelableGetOrdersForCustomer.signal,
            }
        )
        if (Array.isArray(response.data) && response.data.every(isOrderOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Laden der Bestellungen");
    },

    async advanceKitchenOrder(orderId: string) {
        OrderApi.cancelableAdvanceOrdersForKitchen?.abort();
        OrderApi.cancelableAdvanceOrdersForKitchen = new AbortController();

        const response = await axios.patch(
            OrderApi.baseUrl + "/kitchen/" + orderId,
            {
                signal: OrderApi.cancelableAdvanceOrdersForKitchen.signal,
            }
        )
        if (isOrderOutputDTO(response.data)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Laden der Bestellungen");
    },

    async getOrdersForCashier() {
        OrderApi.cancelableGetOrdersForCashier?.abort();
        OrderApi.cancelableGetOrdersForCashier = new AbortController();

        const response = await axios.get(
            OrderApi.baseUrl + "/cashier",
            {
                signal: OrderApi.cancelableGetOrdersForCashier.signal,
            }
        )
        if (Array.isArray(response.data) && response.data.every(isOrderOutputDTO)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Laden der Bestellungen");
    },

    async advanceCashierOrder(orderId: string) {
        OrderApi.cancelableAdvanceOrdersForCashier?.abort();
        OrderApi.cancelableAdvanceOrdersForCashier = new AbortController();

        const response = await axios.patch(
            OrderApi.baseUrl + "/cashier/" + orderId,
            {
                signal: OrderApi.cancelableAdvanceOrdersForCashier.signal,
            }
        )
        if (isOrderOutputDTO(response.data)) {
            return response.data;
        }
        throw new TypeError("Ungültige Antwort beim Laden der Bestellungen");
    }
}