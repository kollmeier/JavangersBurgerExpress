import {useMutation, useQueryClient} from "@tanstack/react-query";
import {DisplayItemsApi} from "@/services/display-items-api.ts";
import {DisplayItemOutputDTO} from "@/types/DisplayItemOutputDTO.ts";
import {DisplayCategoryOutputDTO} from "@/types/DisplayCategoryOutputDTO.ts";

export function useDisplayItemMutations(updateCategoryData?:(data: DisplayCategoryOutputDTO | DisplayCategoryOutputDTO[] | null | undefined, id?: string) => void) {
    const queryClient = useQueryClient();

    const updateData = (data: DisplayItemOutputDTO | DisplayItemOutputDTO[] | null | undefined, id?: string): void => {
        queryClient.setQueryData(['displayItemsData', ...(id ? [{id}] : [])], data)
        if (updateCategoryData && Array.isArray(data)) {
            const categoryData = queryClient.getQueryData<DisplayCategoryOutputDTO[]>(['displayCategoriesData']);
            if (!categoryData) {
                return;
            }
            updateCategoryData(categoryData.map(category =>
                 ({...category, item: data.filter(item => item.categoryId === category.id)})
            ));
        }
    }

    const handleSettled = (data: DisplayItemOutputDTO | null | undefined) => {
        return Promise.all(
            [queryClient.invalidateQueries({queryKey: ['displayItemsData']}),
            queryClient.invalidateQueries({queryKey: ['displayCategoriesData', ...(data ? [{id: data.categoryId}] : [])]}) ]
        )
    }
    
    const savePositionsMutation = useMutation({
        mutationFn: DisplayItemsApi.saveDisplayItemsPositions,
        onSuccess: (savedDisplayItems) => {
            updateData(savedDisplayItems)
        },
    })

    const addDisplayItemMutation = useMutation({
        mutationFn: DisplayItemsApi.saveDisplayItem,
        onSuccess: (savedDisplayItem) => {
            updateData(savedDisplayItem, savedDisplayItem?.id);
        },
        onSettled: handleSettled,
    });

    const updateDisplayItemMutation = useMutation({
        mutationFn: DisplayItemsApi.updateDisplayItem,
        onSuccess: (savedDisplayItem, submittedDisplayItem) => {
            updateData(savedDisplayItem, submittedDisplayItem.id);
        },
        onSettled: handleSettled,
    });

    const deleteDisplayItemMutation = useMutation({
        mutationFn: DisplayItemsApi.deleteDisplayItem,
        onSuccess: (_v, deletedId) => {
            updateData(undefined, deletedId);
        },
        onSettled: () => handleSettled(undefined),
    });

    return {
        updateData,
        savePositionsMutation,
        addDisplayItemMutation,
        updateDisplayItemMutation,
        deleteDisplayItemMutation,
    }
}