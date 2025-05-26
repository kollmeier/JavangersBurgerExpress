import {PageLayoutContextProvider} from "../context/page-layout-context-provider.tsx";
import CustomerHeader from "../components/layout/customer-header.tsx";
import CustomerFooter from "../components/layout/customer-footer.tsx";

function CustomerLayout() {
    return (<PageLayoutContextProvider
        header={<CustomerHeader />}
        footer={<CustomerFooter />}
    >
        Bald mehr Inhalt
    </PageLayoutContextProvider>);
}

export default CustomerLayout;