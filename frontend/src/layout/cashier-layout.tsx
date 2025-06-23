import {PageLayoutContextProvider} from "../context/page-layout-context-provider.tsx";
import {Outlet} from "react-router-dom";
import CashierHeader from "../components/layout/cashier-header.tsx";
import {useAuth} from "@/context/auth-context.ts";
import AuthActions from "@/components/ui/auth-actions.tsx";
import CashierFooter from "@/components/layout/cashier-footer.tsx";

function AdministrationLayout() {
    const {isAuthenticated, authorities} = useAuth();

    return (<PageLayoutContextProvider
        header={<CashierHeader />}
        footer={<CashierFooter />}
        mainNav={[
            {label: "Login/Logout", element: <AuthActions />, className: "grow-0 ml-auto"},
        ]}
        {...(isAuthenticated && authorities.includes("ROLE_CASHIER") ? {} : {actions: <></>})}
    >
        {isAuthenticated && authorities.includes("ROLE_CASHIER") ?
            <Outlet /> :
            <div className="text-red-600">Bitte loggen Sie sich als Kassenpersonal ein!</div>
        }

    </PageLayoutContextProvider>);
}

export default AdministrationLayout;
