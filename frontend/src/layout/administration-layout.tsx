import {PageLayoutContextProvider} from "../context/page-layout-context-provider.tsx";
import ManagerFooter from "../components/layout/manager-footer.tsx";
import {Outlet} from "react-router-dom";
import ManagerHeader from "../components/layout/manager-header.tsx";

function AdministrationLayout() {
    return (<PageLayoutContextProvider
        header={<ManagerHeader />}
        footer={<ManagerFooter />}
        mainNav={[
            {label: "Gerichte", href: "/manage/dishes"},
            {label: "Menüs", href: "/manage/menus"},
            {label: "Anzeige-Elemente", href: "/manage/displayItems"},
        ]}
    >
        <Outlet />
    </PageLayoutContextProvider>);
}

export default AdministrationLayout;