import {PageLayoutContextProvider} from "../context/PageLayoutContextProvider.tsx";
import CustomerHeader from "./CustomerHeader.tsx";
import CustomerFooter from "./CustomerFooter.tsx";

function CustomerLayout() {
    return (<PageLayoutContextProvider
        header={<CustomerHeader />}
        footer={<CustomerFooter />}
    >
        Bald mehr Inhalt
    </PageLayoutContextProvider>);
}

export default CustomerLayout;