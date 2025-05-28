import {useMutation, useQueryClient} from "@tanstack/react-query";
import {DishesApi} from "@/services/dishes-api.ts";

export function useDishMutations() {
    const queryClient = useQueryClient();

    const savePositionsMutation = useMutation({
        mutationFn: DishesApi.saveDishesPositions,
        onSuccess: (savedDishes) => {
            queryClient.setQueryData(['dishesData'], savedDishes)
        },
    })

    const addDishMutation = useMutation({
        mutationFn: DishesApi.saveDish,
        onSuccess: (savedDish) => {
            queryClient.setQueryData(['dishesData', savedDish?.id], savedDish);
        },
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
        savePositionsMutation,
        addDishMutation,
        updateDishMutation,
        deleteDishMutation,
    }
}