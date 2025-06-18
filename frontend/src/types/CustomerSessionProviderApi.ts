import {CustomerSessionApi} from "@/hooks/use-customer-session.ts";

export type CustomerSessionProviderApi = CustomerSessionApi & {
    setRefreshInterval: (interval?: number) => void;
}