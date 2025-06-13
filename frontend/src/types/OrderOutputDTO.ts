import {OrderItemOutputDTO} from "@/types/OrderItemOutputDTO.ts";

/**
 * Data transfer object for orders.
 */
export interface OrderOutputDTO {
  /**
   * The unique identifier of the order.
   */
  id?: string;

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
