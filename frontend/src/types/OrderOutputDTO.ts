import {OrderItemOutputDTO, isOrderItemOutputDTO} from "@/types/OrderItemOutputDTO.ts";

/**
 * Data transfer object for orders.
 */
export interface OrderOutputDTO {
  /**
   * The unique identifier of the order.
   */
  id?: string;

  /**
   * Represents the unique identifier for an order,
   * readable by the customer to track its order.
   */
  orderNumber: number;

  /**
   * The items in the order.
   */
  items?: Array<OrderItemOutputDTO>;

  /**
   * The total price of the order.
   */
  totalPrice?: string;

  /**
   * The date and time when the order was created.
   */
  createdAt?: string;

  /**
   * The date and time when the order was last updated.
   */
  updatedAt?: string;

  /**
   * The status of the order.
   */
  status?: string;
}

/**
 * Type guard for OrderOutputDTO.
 * @param item The item to check.
 * @returns Whether the item is an OrderOutputDTO.
 */
export function isOrderOutputDTO(item: unknown): item is OrderOutputDTO {
  return item !== null
    && typeof item === 'object'
    && (!('id' in item) || item.id === undefined || typeof item.id === 'string')
    && ('orderNumber' in item && typeof item.orderNumber === 'number')
    && (!('items' in item) || item.items === undefined || 
        (Array.isArray(item.items) && item.items.every(isOrderItemOutputDTO)))
    && (!('totalPrice' in item) || item.totalPrice === undefined || typeof item.totalPrice === 'string')
    && (!('createdAt' in item) || item.createdAt === undefined || typeof item.createdAt === 'string')
    && (!('updatedAt' in item) || item.updatedAt === null || typeof item.updatedAt === 'string')
    && (!('status' in item) || item.status === undefined || typeof item.status === 'string');
}
