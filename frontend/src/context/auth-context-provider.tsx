import React, {createContext, useState, useEffect, ReactNode, useMemo, useCallback} from 'react';
import authApi from '../services/auth-api';
import {AuthContextType} from "@/context/auth-context.ts";

const AuthContextProvider = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [username, setUsername] = useState<string | null>(null);
  const [authorities, setAuthorities] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Check if user is already authenticated on mount
  useEffect(() => {
    const checkAuth = async () => {
      try {
        const userData = await authApi.getCurrentUser();
        if (userData.authenticated) {
          setIsAuthenticated(true);
          setUsername(userData.username);
          setAuthorities(
            userData.authorities.map((auth: { authority: string }) => auth.authority)
          );
        } else {
          setIsAuthenticated(false);
          setUsername(null);
          setAuthorities([]);
        }
      } catch (error) {
        console.error('Error checking authentication:', error);
        setIsAuthenticated(false);
        setUsername(null);
        setAuthorities([]);
      } finally {
        setIsLoading(false);
      }
    };

    checkAuth();
  }, []);

  const login = useCallback(async (username: string, password: string) => {
    setIsLoading(true);
    setError(null);
    try {
      await authApi.login(username, password);
      const userData = await authApi.getCurrentUser();
      setIsAuthenticated(true);
      setUsername(userData.username);
      setAuthorities(
        userData.authorities.map((auth: { authority: string }) => auth.authority)
      );
    } catch (error) {
      setError('Invalid username or password');
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const logout = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      await authApi.logout();
      setIsAuthenticated(false);
      setUsername(null);
      setAuthorities([]);
    } catch (error) {
      console.error('Logout error:', error);
      setError('Failed to logout');
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const value = useMemo(() => ({
    isAuthenticated,
    username,
    authorities,
    login,
    logout,
    isLoading,
    error,
  }), [isAuthenticated, username, authorities, login, logout, isLoading, error]);

  return <AuthContextProvider.Provider value={value}>{children}</AuthContextProvider.Provider>;
};

export default AuthContextProvider;