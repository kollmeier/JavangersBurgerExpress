import {PageLayoutContextProvider} from "../context/page-layout-context-provider.tsx";
import CustomerHeader from "../components/layout/customer-header.tsx";
import CustomerFooter from "../components/layout/customer-footer.tsx";
import CustomerDisplayPage from "../features/customer/pages/customer-display-page.tsx";
import { Routes, Route, Navigate } from "react-router-dom";
import CustomerCheckoutPage from "@/features/customer/pages/customer-checkout-page.tsx";

function CustomerLayout() {
    return (
        <PageLayoutContextProvider
            header={<CustomerHeader />}
            footer={<CustomerFooter />}
        >
            <Routes>
                <Route path="/" element={<Navigate to="/category" replace />} />
                <Route path="/category/*" element={<CustomerDisplayPage />} />
                <Route path="/category/:categoryId/*" element={<CustomerDisplayPage />} />
                <Route path="/checkout/*" element={<CustomerCheckoutPage />} />
            </Routes>
        </PageLayoutContextProvider>
    );
}

export default CustomerLayout;
