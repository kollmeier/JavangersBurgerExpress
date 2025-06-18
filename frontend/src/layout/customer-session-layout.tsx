import {Outlet} from "react-router-dom";
import CustomerSessionProvider from "@/context/customer-session-context-provider.tsx";

const CustomerSessionLayout = () => (
    <CustomerSessionProvider>
            <Outlet />
    </CustomerSessionProvider>
)

export default CustomerSessionLayout;