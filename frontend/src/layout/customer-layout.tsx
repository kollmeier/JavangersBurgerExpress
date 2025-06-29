import {PageLayoutContextProvider} from "../context/page-layout-context-provider.tsx";
import CustomerHeader from "../components/layout/customer-header.tsx";
import CustomerFooter from "../components/layout/customer-footer.tsx";
import CustomerDisplayPage from "../features/customer/pages/customer-display-page.tsx";
import {Routes, Route, Navigate, useNavigate} from "react-router-dom";
import CustomerCheckoutLayout from "@/features/customer/layouts/customer-checkout-layout.tsx";
import {useCustomerSessionContext} from "@/context/customer-session-context.ts";
import {useEffect} from "react";
import BeDialog from "@/components/shared/be-dialog.tsx";
import {ClockFading, Pointer} from "lucide-react";

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

    const handleCreateCustomerSession = () => {
        createCustomerSession();
        setRefreshInterval(30);
    }

    const handleRenewCustomerSession = () => {
        renewCustomerSession();
        setRefreshInterval(30);
    }

    return (
        <PageLayoutContextProvider
            header={<CustomerHeader />}
            footer={<CustomerFooter />}
        >
            <Routes>
                <Route path="/" element={<Navigate to="/category" replace />} />
                <Route path="/category/*" element={<CustomerDisplayPage />}/>
                <Route path="/category/:categoryId/*" element={<CustomerDisplayPage />} />
                <Route path="/checkout/*" element={<CustomerCheckoutLayout />} />
            </Routes>
            <BeDialog
                icon={Pointer}
                iconClassName="animate-pulse ease-in-out"
                className="text-xl"
                title="Jetzt bestellen!"
                onClick={handleCreateCustomerSession}
                clickToClose
                open={!customerSession || customerSession.expired}
                onClose={handleCreateCustomerSession}>
                Bestellen Sie jetzt! Berühren Sie den Bildschirm, um den Bestellvorgang zu starten.
            </BeDialog>
            <BeDialog
                icon={ClockFading}
                iconClassName="animate-pulse ease-in-out"
                className="text-xl"
                title="Sind Sie noch da?"
                onClick={handleRenewCustomerSession}
                clickToClose
                open={!!customerSession && customerSession.expiresInSeconds <= 30}
                onClose={handleRenewCustomerSession}>
                <div>Sind Sie noch da? Berühren Sie den Bildschirm zum fortfahren!</div>
                {customerSession && <div className="text-red-500 text-3xl">Automatisches Abmelden in {customerSession.expiresInSeconds} Sekunden</div>}
            </BeDialog>
        </PageLayoutContextProvider>
    );
}

export default CustomerLayout;
