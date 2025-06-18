import {createContext, useContext} from "react";
import {CustomerSessionProviderApi} from "@/context/customer-session-context-provider.tsx";

const CustomerSessionContext = createContext<CustomerSessionProviderApi | undefined>(undefined);

export function useCustomerSessionContext() {
    const ctx = useContext(CustomerSessionContext);
    if (!ctx) throw new Error("useCustomerSessionContext muss innerhalb von PageLayoutProvider verwendet werden!");
    return ctx;
}

export default CustomerSessionContext;