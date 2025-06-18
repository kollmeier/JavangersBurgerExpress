/**
 * Data transfer object for customer sessions.
 */
import { OrderOutputDTO, isOrderOutputDTO } from './OrderOutputDTO.ts';

export interface CustomerSessionDTO {
  /**
   * The date and time when the session was created.
   */
  createdAt: string;

  /**
   * The date and time when the session will expire.
   */
  expiresAt: string;

  /**
   * The number of seconds until the session expires.
   */
  expiresInSeconds: number;

  /**
   * Whether the session has expired.
   */
  expired: boolean;

  /**
   * The order associated with this session.
   */
  order?: OrderOutputDTO;
}

export function isCustomerSessionDTO(item: unknown): item is CustomerSessionDTO {
  return item !== null
    && typeof item === 'object'
    && 'createdAt' in item
    && 'expiresAt' in item
    && 'expiresInSeconds' in item
    && 'expired' in item
    && typeof item.createdAt === 'string'
    && typeof item.expiresAt === 'string'
    && typeof item.expiresInSeconds === 'number'
    && typeof item.expired === 'boolean'
    && (!('order' in item) || item.order === undefined || isOrderOutputDTO(item.order));
}
