import {PageLayoutContextProvider} from "../context/page-layout-context-provider.tsx";
import CustomerFooter from "../components/layout/customer-footer.tsx";
import {Outlet} from "react-router-dom";
import CustomerHeader from "../components/layout/customer-header.tsx";

function CustomerNoSessionLayout() {
    return (<PageLayoutContextProvider
        header={<CustomerHeader />}
        footer={<CustomerFooter />}
    >
        <Outlet />
    </PageLayoutContextProvider>);
}

export default CustomerNoSessionLayout;
