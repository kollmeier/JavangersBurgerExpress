import axios from 'axios';

/**
 * Authentication API service for handling login, logout, and user information.
 */
export const authApi = {
  /**
   * Get the current user information.
   * @returns The current user information.
   */
  getCurrentUser: async () => {
    try {
      const response = await axios.get('/api/auth/user', {
        withCredentials: true
      });
      return response.data;
    } catch (error) {
      console.error('Error getting current user:', error);
      return { authenticated: false };
    }
  },

  /**
   * Login with the given username and password.
   * @param username The username.
   * @param password The password.
   * @returns The login response.
   */
  login: async (username: string, password: string) => {
    try {
      const formData = new FormData();
      formData.append('username', username);
      formData.append('password', password);

      const response = await axios.post('/api/auth/login', formData, {
        withCredentials: true,
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      });
      return response.data;
    } catch (error) {
      console.error('Error logging in:', error);
      throw error;
    }
  },

  /**
   * Logout the current user.
   * @returns The logout response.
   */
  logout: async () => {
    try {
      const response = await axios.post('/api/auth/logout', {}, {
        withCredentials: true
      });
      return response.data;
    } catch (error) {
      console.error('Error logging out:', error);
      throw error;
    }
  }
};

export default authApi;