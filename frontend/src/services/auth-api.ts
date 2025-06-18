import axios from 'axios';
import { UserDTO, isUserDTO, LoginResponseDTO, isLoginResponseDTO } from '@/types/AuthDTO';
import { throwErrorByResponse } from '@/util/errors';

/**
 * Authentication API service for handling login, logout, and user information.
 */
export const AuthApi = {
  baseUrl: '/api/auth',
  cancelableGetCurrentUserRef: null as AbortController | null,
  cancelableLoginRef: null as AbortController | null,
  cancelableLogoutRef: null as AbortController | null,

  /**
   * Get the current user information.
   * @returns The current user information.
   */
  async getCurrentUser(): Promise<UserDTO> {
    AuthApi.cancelableGetCurrentUserRef?.abort();
    AuthApi.cancelableGetCurrentUserRef = new AbortController();

    try {
      const response = await axios.get(`${AuthApi.baseUrl}/user`, {
        withCredentials: true,
        signal: AuthApi.cancelableGetCurrentUserRef.signal
      });

      if (isUserDTO(response.data)) {
        return response.data;
      }
    } catch (error) {
      if (axios.isCancel(error)) {
        return { authenticated: false };
      }
      console.error('Error getting current user:', error);
      return { authenticated: false };
    }

    return { authenticated: false };
  },

  /**
   * Login with the given username and password.
   * @param username The username.
   * @param password The password.
   * @returns The login response.
   */
  async login(username: string, password: string): Promise<LoginResponseDTO> {
    AuthApi.cancelableLoginRef?.abort();
    AuthApi.cancelableLoginRef = new AbortController();

    try {
      const formData = new FormData();
      formData.append('username', username);
      formData.append('password', password);

      const response = await axios.post(`${AuthApi.baseUrl}/login`, formData, {
        withCredentials: true,
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        signal: AuthApi.cancelableLoginRef.signal
      });

      if (isLoginResponseDTO(response.data)) {
        return response.data;
      }
    } catch (error) {
      if (axios.isCancel(error)) {
        return { success: false, error: 'Request canceled' };
      }
      throwErrorByResponse(error);
    }

    throw new TypeError('Invalid response from login');
  },

  /**
   * Logout the current user.
   * @returns The logout response.
   */
  async logout(): Promise<void> {
    AuthApi.cancelableLogoutRef?.abort();
    AuthApi.cancelableLogoutRef = new AbortController();

    try {
      await axios.post(`${AuthApi.baseUrl}/logout`, {}, {
        withCredentials: true,
        signal: AuthApi.cancelableLogoutRef.signal
      });
      return;
    } catch (error) {
      if (axios.isCancel(error)) {
        return;
      }
      throwErrorByResponse(error);
    }
  }
};

export default AuthApi;
