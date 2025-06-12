import {PageLayoutContextProvider} from "../context/page-layout-context-provider.tsx";
import ManagerFooter from "../components/layout/manager-footer.tsx";
import {Outlet} from "react-router-dom";
import ManagerHeader from "../components/layout/manager-header.tsx";
import AuthActions from "@/components/ui/auth-actions.tsx";
import {useAuth} from "@/context/auth-context.ts";

function AdministrationLayout() {
    const {isAuthenticated, authorities} = useAuth();

    return (<PageLayoutContextProvider
        header={<ManagerHeader />}
        footer={<ManagerFooter />}
        mainNav={[
            {label: "Gerichte", href: "/manage/dishes"},
            {label: "Menüs", href: "/manage/menus"},
            {label: "Anzeige-Elemente", href: "/manage/displayItems"},
            {label: "Login/Logout", element: <AuthActions />, className: "grow-0 ml-auto"},
        ]}
        {...(isAuthenticated && authorities.includes("ROLE_MANAGER") ? {} : {actions: <></>})}
    >
        {isAuthenticated && authorities.includes("ROLE_MANAGER") ?
            <Outlet /> :
            <div className="text-red-600">Bitte loggen Sie sich als Manager ein!</div>
        }

    </PageLayoutContextProvider>);
}

export default AdministrationLayout;
