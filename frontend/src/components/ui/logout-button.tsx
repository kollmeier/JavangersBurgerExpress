import React from 'react';
import {useAuth} from "@/context/auth-context.ts";
import {UserCheck, UserX} from "lucide-react";
import BeButton from "@/components/ui/be-button.tsx";

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
      <span className="font-medium"><UserCheck className="px-3"/>Logged in as: {username}</span>
      <BeButton
        onClick={handleLogout}
        disabled={isLoading}
        className="text-white bg-red-600 rounded-md hover:bg-red-700 disabled:bg-red-300"
        icon={UserX}
      >
        {isLoading ? 'Logging out...' : 'Logout'}
      </BeButton>
    </div>
  );
};

export default LogoutButton;