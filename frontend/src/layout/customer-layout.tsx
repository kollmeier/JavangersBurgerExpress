import {PageLayoutContextProvider} from "../context/page-layout-context-provider.tsx";
import CustomerHeader from "../components/layout/customer-header.tsx";
import CustomerFooter from "../components/layout/customer-footer.tsx";
import CustomerDisplayPage from "../features/customer/pages/customer-display-page.tsx";
import {Routes, Route, Navigate, useNavigate} from "react-router-dom";
import CustomerCheckoutPage from "@/features/customer/pages/customer-checkout-page.tsx";
import CustomerPaymentPage from "@/features/customer/pages/customer-payment-page.tsx";
import {useCustomerSessionContext} from "@/context/customer-session-context.ts";
import {useEffect} from "react";
import BeDialog from "@/components/shared/be-dialog.tsx";
import {faBurger, faClock} from "@fortawesome/free-solid-svg-icons";

function CustomerLayout() {
    const {customerSession, renewCustomerSession, createCustomerSession, removeCustomerSession, setRefreshInterval} = useCustomerSessionContext();

    const navigate = useNavigate();

    useEffect(() => {
        if (!customerSession) {
            setRefreshInterval(undefined);
            navigate("/category");
            return;
        }
        if (customerSession.expired) {
            removeCustomerSession();
            return;
        }
        if (customerSession.expiresInSeconds < 30) {
            setRefreshInterval(1);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [customerSession]);

    return (
        <PageLayoutContextProvider
            header={<CustomerHeader />}
            footer={<CustomerFooter />}
        >
            <Routes>
                <Route path="/" element={<Navigate to="/category" replace />} />
                <Route path="/category/*" element={<CustomerDisplayPage />}/>
                <Route path="/category/:categoryId/*" element={<CustomerDisplayPage />} />
                <Route path="/checkout" element={<CustomerCheckoutPage />} />
                <Route path="/checkout/payment" element={<CustomerPaymentPage />} />
            </Routes>
            <BeDialog
                icon={faBurger}
                className="text-xl"
                title="Jetzt bestellen!"
                onClick={() => createCustomerSession()}
                open={!customerSession || customerSession.expired}
                onClose={() => createCustomerSession()}>
                Bestellen Sie jetzt! Berühren Sie den Bildschirm, um den Bestellvorgang zu starten.
            </BeDialog>
            <BeDialog
                icon={faClock}
                className="text-xl"
                title="Sind Sie noch da?"
                onClick={() => renewCustomerSession()}
                open={!!customerSession && customerSession.expiresInSeconds <= 30}
                onClose={() => renewCustomerSession()}>
                <div>Sind Sie noch da? Berühren Sie den Bildschirm zum fortfahren!</div>
                {customerSession && <div className="text-red-500 text-3xl">Automatisches Abmelden in {customerSession.expiresInSeconds} Sekunden</div>}
            </BeDialog>
        </PageLayoutContextProvider>
    );
}

export default CustomerLayout;
