import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faUserCheck, faUserSlash} from "@fortawesome/free-solid-svg-icons";
import {useAuth} from "@/context/auth-context.ts";

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
      <span className="text-sm font-medium"><FontAwesomeIcon icon={faUserCheck} /> Logged in as: {username}</span>
      <button
        onClick={handleLogout}
        disabled={isLoading}
        className="px-3 py-1 text-sm text-white bg-red-600 rounded-md hover:bg-red-700 disabled:bg-red-300"
      >
        <FontAwesomeIcon icon={faUserSlash} />{isLoading ? 'Logging out...' : 'Logout'}
      </button>
    </div>
  );
};

export default LogoutButton;