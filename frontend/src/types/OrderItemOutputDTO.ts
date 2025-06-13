import {OrderableItemOutputDTO} from "@/types/OrderableItemOutputDTO.ts";

/**
 * Data transfer object for order items.
 */
export interface OrderItemOutputDTO {
    /**
     * The unique identifier of the order item.
     */
    id?: string;

    /**
     * The orderable item.
     */
    item?: OrderableItemOutputDTO;

    /**
     * The amount of the item.
     */
    amount?: number;

    /**
     * The price of the item.
     */
    price?: string;
}