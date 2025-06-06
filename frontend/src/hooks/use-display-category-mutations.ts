import {useMutation, useQueryClient} from "@tanstack/react-query";
import {DisplayCategoriesApi} from "@/services/display-categories-api.ts";
import {DisplayCategoryOutputDTO} from "@/types/DisplayCategoryOutputDTO.ts";

export function useDisplayCategoryMutations() {
    const queryClient = useQueryClient();

    const updateData = (data: DisplayCategoryOutputDTO | DisplayCategoryOutputDTO[] | null | undefined, id?: string): void => {
        queryClient.setQueryData(['displayCategoriesData', ...(id ? [id] : [])], data)
    }

    const savePositionsMutation = useMutation({
        mutationFn: DisplayCategoriesApi.saveDisplayCategoriesPositions,
        onSuccess: (savedDisplayCategories) => {
            updateData(savedDisplayCategories)
        },
    })

    const addDisplayCategoryMutation = useMutation({
        mutationFn: DisplayCategoriesApi.saveDisplayCategory,
        onSuccess: (savedDisplayCategory) => {
            updateData(savedDisplayCategory, savedDisplayCategory?.id);
        },
        onSettled: () => queryClient.invalidateQueries({queryKey: ['displayCategoriesData']}),
    });

    const updateDisplayCategoryMutation = useMutation({
        mutationFn: DisplayCategoriesApi.updateDisplayCategory,
        onSuccess: (savedDisplayCategory, submittedDisplayCategory) => {
            updateData(savedDisplayCategory, submittedDisplayCategory.id);
        },
        onSettled: () => queryClient.invalidateQueries({queryKey: ['displayCategoriesData']}),
    });

    const deleteDisplayCategoryMutation = useMutation({
        mutationFn: DisplayCategoriesApi.deleteDisplayCategory,
        onSuccess: (_v, deletedId) => {
            updateData(undefined, deletedId);
        },
        onSettled: () => queryClient.invalidateQueries({queryKey: ['displayCategoriesData']}),
    });

    return {
        savePositionsMutation,
        updateData,
        addDisplayCategoryMutation,
        updateDisplayCategoryMutation,
        deleteDisplayCategoryMutation,
    }
}