import {useMutation, useQueryClient} from "@tanstack/react-query";
import {DishesApi} from "@/services/dishes-api.ts";

export function useDishMutations() {
    const queryClient = useQueryClient();

    const addDishMutation = useMutation({
        mutationFn: DishesApi.saveDish,
        onSettled: () => queryClient.invalidateQueries({queryKey: ['dishesData']}),
    });

    const updateDishMutation = useMutation({
        mutationFn: DishesApi.updateDish,
        onSuccess: (savedDish, submittedDish) => {
            queryClient.setQueryData(['dishesData', submittedDish.id], savedDish);
        },
        onSettled: () => queryClient.invalidateQueries({queryKey: ['dishesData']}),
    });

    const deleteDishMutation = useMutation({
        mutationFn: DishesApi.deleteDish,
        onSuccess: (_v, deletedId) => {
            queryClient.setQueryData(['dishesData', deletedId], undefined);
        },
        onSettled: () => queryClient.invalidateQueries({queryKey: ['dishesData']}),
    });

    return {
        addDishMutation,
        updateDishMutation,
        deleteDishMutation,
    }
}