import {PageLayoutContextProvider} from "../context/page-layout-context-provider.tsx";
import KitchenFooter from "../components/layout/kitchen-footer.tsx";
import {Outlet} from "react-router-dom";
import KitchenHeader from "../components/layout/kitchen-header.tsx";
import {useAuth} from "@/context/auth-context.ts";
import AuthActions from "@/components/ui/auth-actions.tsx";

function AdministrationLayout() {
    const {isAuthenticated, authorities} = useAuth();

    return (<PageLayoutContextProvider
        header={<KitchenHeader />}
        footer={<KitchenFooter />}
        mainNav={[
            {label: "Login/Logout", element: <AuthActions />, className: "grow-0 ml-auto"},
        ]}
        {...(isAuthenticated && authorities.includes("ROLE_KITCHEN") ? {} : {actions: <></>})}
    >
        {isAuthenticated && authorities.includes("ROLE_KITCHEN") ?
            <Outlet /> :
            <div className="text-red-600">Bitte loggen Sie sich als Küchenpersonal ein!</div>
        }

    </PageLayoutContextProvider>);
}

export default AdministrationLayout;
