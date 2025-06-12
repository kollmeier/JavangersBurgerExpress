import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import CustomerSessionApi from '@/services/customer-session-api';
import { CustomerSessionDTO } from '@/types/CustomerSessionDTO';

/**
 * Hook for managing customer sessions.
 */
export function useCustomerSession(interValInSeconds = 30) {
  const queryClient = useQueryClient();

  // Query for getting the current customer session
  const { data: customerSession, isLoading, error, refetch } = useQuery<CustomerSessionDTO>({
    queryKey: ['customerSession'],
    queryFn: CustomerSessionApi.getCustomerSession,
    staleTime: interValInSeconds * 1000, // 30 seconds
    refetchInterval: interValInSeconds * 1000, // 30 seconds
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
      if (data) {
        queryClient.setQueryData(['customerSession'], data);
      }
    },
  });

  // Mutation for removing the customer session
  const removeCustomerSession = useMutation({
    mutationFn: CustomerSessionApi.removeCustomerSession,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['customerSession'] });
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
  };
}
