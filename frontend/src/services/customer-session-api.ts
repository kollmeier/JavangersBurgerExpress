import axios from 'axios';
import { CustomerSessionDTO } from '@/types/CustomerSessionDTO';

/**
 * CustomerSession API service for handling customer sessions.
 */
export const CustomerSessionApi = {
  /**
   * Base URL for the customer session API.
   */
  baseUrl: '/api/customer-sessions',

  /**
   * Gets the current customer session or creates a new one if none exists.
   * @returns The customer session.
   */
  getCustomerSession: async (): Promise<CustomerSessionDTO> => {
    try {
      const response = await axios.get(CustomerSessionApi.baseUrl, {
      });
      return response.data;
    } catch (error) {
      console.error('Error getting customer session:', error);
      throw error;
    }
  },

  /**
   * Creates a new customer session.
   * @returns The created customer session.
   */
  createCustomerSession: async (): Promise<CustomerSessionDTO> => {
    try {
      const response = await axios.post(CustomerSessionApi.baseUrl, {}, {
        withCredentials: true
      });
      return response.data;
    } catch (error) {
      console.error('Error creating customer session:', error);
      throw error;
    }
  },

  /**
   * Renews an existing customer session.
   * @returns The renewed customer session or null if no session exists.
   */
  renewCustomerSession: async (): Promise<CustomerSessionDTO | null> => {
    try {
      const response = await axios.put(CustomerSessionApi.baseUrl, {}, {
        withCredentials: true
      });
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        return null;
      }
      console.error('Error renewing customer session:', error);
      throw error;
    }
  },

  /**
   * Removes the current customer session.
   */
  removeCustomerSession: async () => {
    try {
      await axios.delete(CustomerSessionApi.baseUrl, {
        withCredentials: true
      });
    } catch (error) {
      console.error('Error removing customer session:', error);
      throw error;
    }
  }
};

export default CustomerSessionApi;
