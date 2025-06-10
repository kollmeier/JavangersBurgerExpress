import {useMutation, useQueryClient} from "@tanstack/react-query";
import {DisplayItemsApi} from "@/services/display-items-api.ts";
import {DisplayItemOutputDTO} from "@/types/DisplayItemOutputDTO.ts";
import {DisplayCategoryOutputDTO} from "@/types/DisplayCategoryOutputDTO.ts";

export function useDisplayItemMutations(updateCategoryData?:(data: DisplayCategoryOutputDTO | DisplayCategoryOutputDTO[] | null | undefined, id?: string) => void) {
    const queryClient = useQueryClient();

    const sortedAndOrderedDisplayItemsForCategory = (data: DisplayItemOutputDTO, category: DisplayCategoryOutputDTO): DisplayItemOutputDTO[] => {
        const inCategory = !!category.displayItems.find(item => data.id === item.id);
        const changedCategory = category.id !== data.categoryId;
        if (inCategory && changedCategory) {
            return category.displayItems.filter(item => item.id !== data.id);
        }
        if (inCategory) {
            return category.displayItems.map(item => item.id === data.id ? data : item);
        }
        if (!inCategory && changedCategory) {
            return category.displayItems;
        }
        return [data, ...category.displayItems];
    }

    const updateData = (data: DisplayItemOutputDTO | DisplayItemOutputDTO[] | null | undefined, id?: string): void => {
        queryClient.setQueryData(['displayItemsData', ...(id ? [{id}] : [])], data)
        if (updateCategoryData) {
            const categoryData = queryClient.getQueryData<DisplayCategoryOutputDTO[]>(['displayCategoriesData']);
            if (!categoryData) {
                return;
            }
            if (!data) {
                updateCategoryData(categoryData.map(category =>
                    ({...category, displayItems: category.displayItems.filter(item => item.id !== id)})
                ));
                return;
            }
            if (Array.isArray(data)) {
                updateCategoryData(categoryData.map(category =>
                    ({...category, displayItems: data.filter(item => item.categoryId === category.id)})
                ));
                return;
            }
            updateCategoryData(categoryData.map(category =>
                ({...category, displayItems: sortedAndOrderedDisplayItemsForCategory(data, category)
                })

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