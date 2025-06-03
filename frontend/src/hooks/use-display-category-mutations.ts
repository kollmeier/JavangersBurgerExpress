import {useMutation, useQueryClient} from "@tanstack/react-query";
import {DisplayCategoriesApi} from "@/services/display-categories-api.ts";

export function useDisplayCategoryMutations() {
    const queryClient = useQueryClient();

    const savePositionsMutation = useMutation({
        mutationFn: DisplayCategoriesApi.saveDisplayCategoriesPositions,
        onSuccess: (savedDisplayCategories) => {
            queryClient.setQueryData(['displayCategoriesData'], savedDisplayCategories)
        },
    })

    const addDisplayCategoryMutation = useMutation({
        mutationFn: DisplayCategoriesApi.saveDisplayCategory,
        onSuccess: (savedDisplayCategory) => {
            queryClient.setQueryData(['displayCategoriesData', savedDisplayCategory?.id], savedDisplayCategory);
        },
        onSettled: () => queryClient.invalidateQueries({queryKey: ['displayCategoriesData']}),
    });

    const updateDisplayCategoryMutation = useMutation({
        mutationFn: DisplayCategoriesApi.updateDisplayCategory,
        onSuccess: (savedDisplayCategory, submittedDisplayCategory) => {
            queryClient.setQueryData(['displayCategoriesData', submittedDisplayCategory.id], savedDisplayCategory);
        },
        onSettled: () => queryClient.invalidateQueries({queryKey: ['displayCategoriesData']}),
    });

    const deleteDisplayCategoryMutation = useMutation({
        mutationFn: DisplayCategoriesApi.deleteDisplayCategory,
        onSuccess: (_v, deletedId) => {
            queryClient.setQueryData(['displayCategoriesData', deletedId], undefined);
        },
        onSettled: () => queryClient.invalidateQueries({queryKey: ['displayCategoriesData']}),
    });

    return {
        savePositionsMutation,
        addDisplayCategoryMutation,
        updateDisplayCategoryMutation,
        deleteDisplayCategoryMutation,
    }
}