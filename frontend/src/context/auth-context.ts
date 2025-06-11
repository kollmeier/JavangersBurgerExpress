import {useContext} from "react";
import AuthContextProvider from "@/context/auth-context-provider.tsx";

export interface AuthContextType {
    isAuthenticated: boolean;
    username: string | null;
    authorities: string[];
    login: (username: string, password: string) => Promise<void>;
    logout: () => Promise<void>;
    isLoading: boolean;
    error: string | null;
}

export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContextProvider);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

