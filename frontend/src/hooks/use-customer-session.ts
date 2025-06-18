import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import CustomerSessionApi from '@/services/customer-session-api';
import { CustomerSessionDTO } from '@/types/CustomerSessionDTO';
import { OrderInputDTO } from '@/types/OrderInputDTO';

export type CustomerSessionApi = ReturnType<typeof useCustomerSession>;

/**
 * Hook for managing customer sessions.
 */
export function useCustomerSession(interValInSeconds?:number) {
  const queryClient = useQueryClient();

  // Query for getting the current customer session
  const { data: customerSession, isLoading, error, refetch } = useQuery<CustomerSessionDTO | null>({
    queryKey: ['customerSession'],
    queryFn: CustomerSessionApi.getCustomerSession,

    staleTime: interValInSeconds === undefined ? 5 * 60 * 1000 : interValInSeconds * 1000,
    refetchInterval: interValInSeconds === undefined ? undefined : interValInSeconds * 1000,
  });

  // Mutation for creating a new customer session
  const createCustomerSession = useMutation({
    mutationFn: CustomerSessionApi.createCustomerSession,
    onSuccess: (data) => {
      queryClient.setQueryData(['customerSession'], data);
    },
  });

  // Mutation for renewing the customer session
  const renewCustomerSession = useMutation({
    mutationFn: CustomerSessionApi.renewCustomerSession,
    onSuccess: (data) => {
        if (data === undefined) {
          return;
        }
        queryClient.setQueryData(['customerSession'], data);
    },
  });

  // Mutation for removing the customer session
  const removeCustomerSession = useMutation({
    mutationFn: CustomerSessionApi.removeCustomerSession,
    onSuccess: () => {
      queryClient.setQueryData(['customerSession'], null);
      queryClient.invalidateQueries({ queryKey: ['customerSession'] });
    },
  });

  // Mutation for storing an order in the customer session
  const storeOrder = useMutation({
    mutationFn: (order: OrderInputDTO) => CustomerSessionApi.storeOrder(order),
    onSuccess: (data) => {
      if (data) {
        queryClient.setQueryData(['customerSession'], data);
      }
    },
  });

  return {
    customerSession,
    isLoading,
    error,
    refetch,
    createCustomerSession: createCustomerSession.mutate,
    renewCustomerSession: renewCustomerSession.mutate,
    removeCustomerSession: removeCustomerSession.mutate,
    storeOrder: storeOrder.mutate,
  };
}
