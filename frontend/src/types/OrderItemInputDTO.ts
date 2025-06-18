/**
 * Data transfer object for order item input.
 */
export interface OrderItemInputDTO {
    /**
     * The unique identifier of the order item.
     */
    id?: string;

    /**
     * The unique identifier of the orderable item.
     */
    item?: string;

    /**
     * The amount of the item.
     */
    amount?: number;
}