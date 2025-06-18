import React, {useEffect, useMemo, useState} from "react";
import {useCustomerSession} from "@/util";
import CustomerSessionContext from "@/context/customer-session-context.ts";
import {CustomerSessionProviderApi} from "@/types/CustomerSessionProviderApi.ts";

type CustomerSessionProviderProps = {
    children: React.ReactNode;
}

const CustomerSessionProvider: React.FC<CustomerSessionProviderProps> = ({children}) => {
    const [refreshInterval, setRefreshInterval] = useState<number | undefined>(1000 * 30); // 30 seconds

    const customerSessionApi = useCustomerSession(refreshInterval);

    useEffect(() => {
        console.log("Refresh interval changed to", refreshInterval);
    }, [refreshInterval]);

    const customerSessionProviderApi: CustomerSessionProviderApi = useMemo(() => ({
        ...customerSessionApi,
        setRefreshInterval,
    }), [customerSessionApi, setRefreshInterval]);

    return (<CustomerSessionContext.Provider value={customerSessionProviderApi} >
            {children}
        </CustomerSessionContext.Provider>
    )
}

export default CustomerSessionProvider;