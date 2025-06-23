import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query';
import {OrderApi} from "@/services/order-api.ts";
import {OrderOutputDTO} from "@/types/OrderOutputDTO.ts";

export type OrdersApi = ReturnType<typeof useOrders>;

/**
 * Hook for managing customer sessions.
 */
export function useOrders(interValInSeconds?:number) {
    const queryClient = useQueryClient();

    // Query for getting the current customer session
    const kitchenOrders = useQuery<OrderOutputDTO[]>({
        queryKey: ['orders', 'kitchen'],
        queryFn: OrderApi.getOrdersForKitchen,

        staleTime: interValInSeconds === undefined ? 5 * 60 * 1000 : interValInSeconds * 1000,
        refetchInterval: interValInSeconds === undefined ? undefined : interValInSeconds * 1000,
    });

    // Query for getting the current customer session
    const customerOrders = useQuery<OrderOutputDTO[]>({
        queryKey: ['orders', 'customer'],
        queryFn: OrderApi.getOrdersForCustomer,

        staleTime: interValInSeconds === undefined ? 5 * 60 * 1000 : interValInSeconds * 1000,
        refetchInterval: interValInSeconds === undefined ? undefined : interValInSeconds * 1000,
    });

    // Query for getting the current customer session
    const cashierOrders = useQuery<OrderOutputDTO[]>({
        queryKey: ['orders', 'cashier'],
        queryFn: OrderApi.getOrdersForCashier,

        staleTime: interValInSeconds === undefined ? 5 * 60 * 1000 : interValInSeconds * 1000,
        refetchInterval: interValInSeconds === undefined ? undefined : interValInSeconds * 1000,
    });

    // Mutation for storing an order in the customer session
    const advanceKitchenOrder = useMutation({
        mutationFn: (orderId: string) => OrderApi.advanceKitchenOrder(orderId),
        onSuccess: (data) => {
            if (data) {
                queryClient.invalidateQueries({ queryKey: ['orders', 'kitchen'] });
            }
        },
    });

    // Mutation for storing an order in the customer session
    const advanceCashierOrder = useMutation({
        mutationFn: (orderId: string) => OrderApi.advanceCashierOrder(orderId),
        onSuccess: (data) => {
            if (data) {
                queryClient.invalidateQueries({ queryKey: ['orders', 'cashier'] });
            }
        },
    });

    return {
        kitchenOrders,
        cashierOrders,
        customerOrders,
        advanceKitchenOrder,
        advanceCashierOrder,
    };
}
