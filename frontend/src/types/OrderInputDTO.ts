import {OrderItemInputDTO} from "@/types/OrderItemInputDTO.ts";

/**
 * Data transfer object for order input.
 */
export interface OrderInputDTO {
  /**
   * The unique identifier of the order.
   */
  id?: string;

  /**
   * The items in the order.
   */
  items?: Array<OrderItemInputDTO>;
}

