import {PageLayoutContextProvider} from "../../context/PageLayoutContextProvider.tsx";
import AdministrationHeader from "./AdministrationHeader.tsx";
import AdministrationFooter from "./AdministrationFooter.tsx";
import {Outlet} from "react-router-dom";

function AdministrationLayout() {
    return (<PageLayoutContextProvider
        header={<AdministrationHeader />}
        footer={<AdministrationFooter />}
        mainNav={[
            {label: "Gerichte", href: "/manage/dishes"},
        ]}
    >
        <Outlet />
    </PageLayoutContextProvider>);
}

export default AdministrationLayout;