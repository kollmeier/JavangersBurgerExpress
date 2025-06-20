import MinimalCard from "@/components/shared/minimal-card.tsx";
import {Navigate, NavLink, Route, Routes, useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {usePageLayoutContext} from "@/context/page-layout-context.ts";
import {useCustomerSessionContext} from "@/context/customer-session-context.ts";
import StepIndicator from "@/components/shared/step-indicator.tsx";
import {OrderOutputDTO} from "@/types/OrderOutputDTO.ts";
import {CustomerCheckoutPaymentPage} from "@/features/customer/pages/customer-checkout-payment-page.tsx";
import {CustomerCheckoutSummaryPage} from "@/features/customer/pages/customer-checkout-summary-page.tsx";
import CustomerCheckoutSuccessPage from "@/features/customer/pages/customer-checkout-success-page.tsx";

type OrderDetailsProps = {
    order: OrderOutputDTO;
}

const OrderDetails: React.FC<OrderDetailsProps> = ({order}) => {
    return <MinimalCard
        className="my-4 grid w-full h-full grid-cols-1 md:grid-cols-2 lg:grid-cols-3 auto-rows-fr gap-3">
        <h2 className="row-span-1 col-span-1 md:col-span-2 lg:col-span-3 row-start-1 text-lg">Bestellung</h2>
        {order.items?.map(item =>
            <dl key={item.id}
                className="row-span-1 col-span-1 text-sm border-t-1 mt-1 pt-1 border-gray-200 grid grid-rows-2 grid-cols-3 gap-0">
                <dt className="row-span-2 col-start-1">{item.item?.name}</dt>
                <dd className="row-span-2 col-start-2 italic">{item.item?.descriptionForCart}</dd>
                <dd className="text-xs text-gray-400 text-right">{item.amount} x {item.item?.price}€</dd>
                <dd className="text-right"><span className="text-xs text-gray-400">=</span> {item.price}€</dd>
            </dl>)}
        <dl className="row-span-1 col-span-1 md:col-span-2 lg:col-span-3 -row-start-0 self-end text-sm font-bold border-t-3 border-double mt-1 pt-1 border-gray-800 grid grid-rows-2 grid-cols-2 gap-0">
            <dt>Gesamt</dt>
            <dd className="text-right">{order.totalPrice}€</dd>
        </dl>
    </MinimalCard>;
}

const CustomerCheckoutLayout = () => {
    const {setSidebar} = usePageLayoutContext();
    const [error, setError] = useState<string | null>(null);
    const [buttons, setButtons] = useState<React.JSX.Element>();
    const [provider, setProvider] = useState<string>("provider");

    const {provider: providerParam} = useParams<{ provider?: string }>();

    const {customerSession} = useCustomerSessionContext();

    // Remove the sidebar with the categories
    useEffect(() => {
        setSidebar(undefined);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        console.log("providerParam", providerParam);
        if (providerParam) {
            setProvider(providerParam);
        }
    }, [providerParam]);

    return <div className="p-4 text-gray-800 flex flex-col gap-2">
        <h2 className="text-lg">Bezahlen</h2>
        <StepIndicator
            stepAs={NavLink}
            steps={[
                <>Bestellung prüfen</>,
                <>QR-Code scannen</>,
                <>Zahlung bestätigen</>,
                <>Zubereitung beginnt</>
            ]}
            stepsLink={[
                "/checkout/",
                "/checkout/" + provider + "/",
                "/checkout/" + provider + "/payment/process/",
                "/checkout/" + provider + "/payment/process/success"
            ]}
        />
        <Routes>
            <Route index element={<Navigate to="summary/" replace />} />
            <Route path="summary/*" element={<CustomerCheckoutSummaryPage setError={setError} setButtons={setButtons} />} />
            <Route path=":provider/*">
                <Route index element={<Navigate to="payment/" replace />} />
                <Route path="payment/*">
                    <Route index element={<CustomerCheckoutPaymentPage setButtons={setButtons}/>} />
                    <Route path="process/*">
                        <Route index element={<Navigate to="waiting/" replace />} />
                        <Route path="success/*" element={<CustomerCheckoutSuccessPage setButtons={setButtons} />} />
                        <Route path=":process/*" element={<CustomerCheckoutPaymentPage setButtons={setButtons}/>} />
                    </Route>
                </Route>
            </Route>
        </Routes>
        {!!customerSession?.order?.items?.length && <OrderDetails order={customerSession.order} />}

        {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-2" role="alert">
                <span className="block sm:inline">{error}</span>
            </div>
        )}

        <div className="flex flex-row gap-2 flex-wrap justify-start">{buttons}</div>

    </div>
}

export default CustomerCheckoutLayout;
