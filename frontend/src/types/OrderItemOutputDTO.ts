import {OrderableItemOutputDTO, isOrderableItemOutputDTO} from "@/types/OrderableItemOutputDTO.ts";

/**
 * Data transfer object for order items.
 */
export interface OrderItemOutputDTO {
    /**
     * The unique identifier of the order item.
     */
    id: string | null;

    /**
     * The orderable item.
     */
    item: OrderableItemOutputDTO;

    /**
     * The amount of the item.
     */
    amount: number;

    /**
     * The price of the item.
     */
    price: string;
}

/**
 * Type guard for OrderItemOutputDTO.
 * @param item The item to check.
 * @returns Whether the item is an OrderItemOutputDTO.
 */
export function isOrderItemOutputDTO(item: unknown): item is OrderItemOutputDTO {
    return item !== null
        && typeof item === 'object'
        && (!('id' in item) || item.id === null || typeof item.id === 'string')
        && ('item' in item && isOrderableItemOutputDTO(item.item))
        && ('amount' in item && typeof item.amount === 'number')
        && ('price' in item && typeof item.price === 'string');
}
