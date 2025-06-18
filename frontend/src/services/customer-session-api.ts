import axios from 'axios';
import { CustomerSessionDTO, isCustomerSessionDTO } from '@/types/CustomerSessionDTO';
import { OrderInputDTO } from '@/types/OrderInputDTO';
import { throwErrorByResponse } from '@/util/errors';

/**
 * CustomerSession API service for handling customer sessions.
 */
export const CustomerSessionApi = {
  /**
   * Base URL for the customer session API.
   */
  baseUrl: '/api/customer-sessions',
  cancelableGetSessionRef: null as AbortController | null,
  cancelableCreateSessionRef: null as AbortController | null,
  cancelableRenewSessionRef: null as AbortController | null,
  cancelableRemoveSessionRef: null as AbortController | null,
  cancelableStoreOrderRef: null as AbortController | null,

  /**
   * Gets the current customer session or creates a new one if none exists.
   * @returns The customer session.
   */
  async getCustomerSession(): Promise<CustomerSessionDTO | null> {
    CustomerSessionApi.cancelableGetSessionRef?.abort();
    CustomerSessionApi.cancelableGetSessionRef = new AbortController();

    try {
      const response = await axios.get(CustomerSessionApi.baseUrl, {
        withCredentials: true,
        signal: CustomerSessionApi.cancelableGetSessionRef.signal
      });

      if (response.status === 204) {
        return null;
      }

      if (isCustomerSessionDTO(response.data)) {
        return response.data;
      }
    } catch (error) {
      if (axios.isCancel(error)) {
        throw new Error('Request canceled');
      }
      throwErrorByResponse(error);
    }

    throw new TypeError('Invalid response when getting customer session');
  },

  /**
   * Creates a new customer session.
   * @returns The created customer session.
   */
  async createCustomerSession(): Promise<CustomerSessionDTO> {
    CustomerSessionApi.cancelableCreateSessionRef?.abort();
    CustomerSessionApi.cancelableCreateSessionRef = new AbortController();

    try {
      const response = await axios.post(CustomerSessionApi.baseUrl, {}, {
        withCredentials: true,
        signal: CustomerSessionApi.cancelableCreateSessionRef.signal
      });

      if (isCustomerSessionDTO(response.data)) {
        return response.data;
      }
    } catch (error) {
      if (axios.isCancel(error)) {
        throw new Error('Request canceled');
      }
      throwErrorByResponse(error);
    }

    throw new TypeError('Invalid response when creating customer session');
  },

  /**
   * Renews an existing customer session.
   * @returns The renewed customer session or null if no session exists.
   */
  async renewCustomerSession(): Promise<CustomerSessionDTO | undefined | null> {
    CustomerSessionApi.cancelableRenewSessionRef?.abort();
    CustomerSessionApi.cancelableRenewSessionRef = new AbortController();

    try {
      const response = await axios.put(CustomerSessionApi.baseUrl, {}, {
        withCredentials: true,
        signal: CustomerSessionApi.cancelableRenewSessionRef.signal
      });

      if (response.status === 204) {
        return null;
      }

      if (isCustomerSessionDTO(response.data)) {
        return response.data;
      }
    } catch (error) {
      if (axios.isCancel(error)) {
        return undefined;
      }
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        return undefined;
      }
      throwErrorByResponse(error);
    }

    throw new TypeError('Invalid response when renewing customer session');
  },

  /**
   * Removes the current customer session.
   */
  async removeCustomerSession(): Promise<void> {
    CustomerSessionApi.cancelableRemoveSessionRef?.abort();
    CustomerSessionApi.cancelableRemoveSessionRef = new AbortController();

    try {
      await axios.delete(CustomerSessionApi.baseUrl, {
        withCredentials: true,
        signal: CustomerSessionApi.cancelableRemoveSessionRef.signal
      });
    } catch (error) {
      if (axios.isCancel(error)) {
        return;
      }
      throwErrorByResponse(error);
    }
  },

  /**
   * Stores an order in the customer session.
   * @param order The order to store.
   * @returns The updated customer session or null if no session exists.
   */
  async storeOrder(order: OrderInputDTO): Promise<CustomerSessionDTO | null> {
    CustomerSessionApi.cancelableStoreOrderRef?.abort();
    CustomerSessionApi.cancelableStoreOrderRef = new AbortController();

    try {
      const response = await axios.patch(CustomerSessionApi.baseUrl, order, {
        withCredentials: true,
        signal: CustomerSessionApi.cancelableStoreOrderRef.signal
      });

      if (isCustomerSessionDTO(response.data)) {
        return response.data;
      }
    } catch (error) {
      if (axios.isCancel(error)) {
        return null;
      }
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        return null;
      }
      throwErrorByResponse(error);
    }

    throw new TypeError('Invalid response when storing order in customer session');
  }
};

export default CustomerSessionApi;
