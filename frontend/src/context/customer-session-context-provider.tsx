import React, {useEffect, useState} from "react";
import {useCustomerSession} from "@/util";
import CustomerSessionContext from "@/context/customer-session-context.ts";
import {CustomerSessionApi} from "@/hooks/use-customer-session.ts";

type CustomerSessionProviderProps = {
    children: React.ReactNode;
}

export type CustomerSessionProviderApi = CustomerSessionApi & {
    setRefreshInterval: (interval?: number) => void;
}

const CustomerSessionProvider: React.FC<CustomerSessionProviderProps> = ({children}) => {
    const [refreshInterval, setRefreshInterval] = useState<number | undefined>(1000 * 30); // 30 seconds

    const customerSessionApi = useCustomerSession(refreshInterval);

    useEffect(() => {
        console.log("Refresh interval changed to", refreshInterval);
    }, [refreshInterval]);

    const customerSessionProviderApi: CustomerSessionProviderApi = {
        ...customerSessionApi,
        setRefreshInterval,
    };

    return (<CustomerSessionContext.Provider value={customerSessionProviderApi} >
            {children}
        </CustomerSessionContext.Provider>
    )
}

export default CustomerSessionProvider;