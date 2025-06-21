import React, { useState } from 'react';
import InputWithLabel from "@/components/ui/input-with-label.tsx";
import BeButton from "@/components/ui/be-button.tsx";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faUser} from "@fortawesome/free-solid-svg-icons";
import {useAuth} from "@/context/auth-context.ts";

interface LoginFormProps {
  onLoginSuccess: () => void;
  onLoginError: (error: string) => void;
}

/**
 * Login form component for authenticating users.
 */
const LoginForm: React.FC<LoginFormProps> = ({ onLoginSuccess, onLoginError }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const { login, isLoading } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
        console.log('Logging in with username:', username);
        await login(username, password);
        console.log('Login successful');
        onLoginSuccess();
    } catch (error) {
        console.error('Login error:', error);
        onLoginError('Failed to login');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-row gap-2">
        <InputWithLabel
            label="Benutzername"
            name="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
        />

        <InputWithLabel
            label="Passwort"
            name="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
        />
      <BeButton
        type="submit"
        disabled={isLoading}
        className="h-8.5!"
      >
          <FontAwesomeIcon icon={faUser} /> {isLoading ? 'Logge ein...' : 'Einloggen'}
      </BeButton>
    </form>
  );
};

export default LoginForm;
