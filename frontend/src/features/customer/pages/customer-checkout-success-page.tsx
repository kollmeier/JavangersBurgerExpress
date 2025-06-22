import {useCustomerSessionContext} from "@/context/customer-session-context.ts";
import React, {useEffect} from "react";
import BeButton from "@/components/ui/be-button.tsx";
import {Check} from "lucide-react";

export type CustomerCheckoutSuccessPageProps = {
    setButtons: (buttons: React.JSX.Element) => void;
}

const CustomerCheckoutSuccessPage: React.FC<CustomerCheckoutSuccessPageProps> = ({setButtons})=>  {
    const {setRefreshInterval, removeCustomerSession} = useCustomerSessionContext();

    useEffect(() => {
        setButtons(<BeButton onClick={() => removeCustomerSession()}>Bestellung schließen</BeButton>);
        setRefreshInterval(15);
        setTimeout(removeCustomerSession, 1000 * 60);
    }, [setRefreshInterval, removeCustomerSession, setButtons]);

    return (<div className="flex flex-col items-center justify-center">
        <output
            className="flex flex-col items-center justify-center w-full h-64 bg-transparent">
            <Check className="text-green-600 text-3xl -rotate-45 scale-50 animate-zoom-in drop-shadow-sm"/>
            <div className="text-3xl font-bold">236</div>
            <span className="sr-only">Success!</span>
        </output>
        <div className="my-4 p-3 bg-green-100 text-green-800 rounded-md w-fit">
            Zahlung erfolgreich! Deine Bestellung wird jetzt zubereitet.
        </div>
    </div>);
}

export default CustomerCheckoutSuccessPage;