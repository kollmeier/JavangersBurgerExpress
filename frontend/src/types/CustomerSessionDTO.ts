/**
 * Data transfer object for customer sessions.
 */
import { OrderOutputDTO } from './OrderOutputDTO.ts';

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
