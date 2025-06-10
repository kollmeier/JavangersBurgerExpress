import {PageLayoutContextProvider} from "../context/page-layout-context-provider.tsx";
import CustomerHeader from "../components/layout/customer-header.tsx";
import CustomerFooter from "../components/layout/customer-footer.tsx";
import CustomerDisplayPage from "../features/customer/pages/customer-display-page.tsx";

function CustomerLayout() {
    return (<PageLayoutContextProvider
        header={<CustomerHeader />}
        footer={<CustomerFooter />}
    >
        <CustomerDisplayPage />
    </PageLayoutContextProvider>);
}

export default CustomerLayout;
