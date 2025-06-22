import React from 'react';
import {useAuth} from "@/context/auth-context.ts";
import {UserCheck, UserX} from "lucide-react";

interface LogoutButtonProps {
  onLogoutSuccess: () => void;
  onLogoutError: (error: string) => void;
  username: string;
}

/**
 * Logout button component for logging out users.
 */
const LogoutButton: React.FC<LogoutButtonProps> = ({ 
  onLogoutSuccess, 
  onLogoutError,
  username 
}) => {
  const {logout, isLoading} = useAuth();

  const handleLogout = async () => {
    try {
      await logout();
      onLogoutSuccess();
    } catch (error) {
      console.error('Logout error:', error);
      onLogoutError('Failed to logout');
    }
  };

  return (
    <div className="flex items-center gap-2">
      <span className="text-sm font-medium"><UserCheck /> Logged in as: {username}</span>
      <button
        onClick={handleLogout}
        disabled={isLoading}
        className="px-3 py-1 text-sm text-white bg-red-600 rounded-md hover:bg-red-700 disabled:bg-red-300"
      >
        <UserX />{isLoading ? 'Logging out...' : 'Logout'}
      </button>
    </div>
  );
};

export default LogoutButton;