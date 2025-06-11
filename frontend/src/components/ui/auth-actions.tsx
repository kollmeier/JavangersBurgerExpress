import React, { useState } from 'react';
import LoginForm from './login-form';
import LogoutButton from './logout-button';
import {useAuth} from "@/context/auth-context.ts";

/**
 * Auth actions component for displaying login form or logout button.
 */
const AuthActions: React.FC = () => {
  const { isAuthenticated, username, isLoading } = useAuth();
  const [error, setError] = useState<string | null>(null);

  const handleLoginSuccess = () => {
    setError(null);
  };

  const handleLoginError = (errorMessage: string) => {
    setError(errorMessage);
  };

  const handleLogoutSuccess = () => {
    setError(null);
  };

  const handleLogoutError = (errorMessage: string) => {
    setError(errorMessage);
  };

  if (isLoading) {
    return <div className="text-sm">Loading...</div>;
  }

  return (
    <div className="flex items-start">
      {error && (
        <div className="text-sm text-red-600 mr-4">{error}</div>
      )}

      {isAuthenticated && username ? (
        <LogoutButton 
          username={username} 
          onLogoutSuccess={handleLogoutSuccess} 
          onLogoutError={handleLogoutError} 
        />
      ) : (
        <LoginForm 
          onLoginSuccess={handleLoginSuccess} 
          onLoginError={handleLoginError} 
        />
      )}
    </div>
  );
};

export default AuthActions;
