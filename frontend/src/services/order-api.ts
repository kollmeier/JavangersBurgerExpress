import axios from "axios";
import {isCustomerSessionDTO} from "@/types/CustomerSessionDTO.ts";

export const OrderApi = {
    baseUrl: '/api/orders',
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
    }
    
}