import {useMutation, useQueryClient} from "@tanstack/react-query";
import {MenusApi} from "@/services/menus-api.ts";

export function useMenuMutations() {
    const queryClient = useQueryClient();

    const savePositionsMutation = useMutation({
        mutationFn: MenusApi.saveMenusPositions,
        onSuccess: (savedMenus) => {
            queryClient.setQueryData(['menusData'], savedMenus)
        },
    })

    const addMenuMutation = useMutation({
        mutationFn: MenusApi.saveMenu,
        onSuccess: (savedMenu) => {
            queryClient.setQueryData(['menusData', savedMenu?.id], savedMenu);
        },
        onSettled: () => queryClient.invalidateQueries({queryKey: ['menusData']}),
    });

    const updateMenuMutation = useMutation({
        mutationFn: MenusApi.updateMenu,
        onSuccess: (savedMenu, submittedMenu) => {
            queryClient.setQueryData(['menusData', submittedMenu.id], savedMenu);
        },
        onSettled: () => queryClient.invalidateQueries({queryKey: ['menusData']}),
    });

    const deleteMenuMutation = useMutation({
        mutationFn: MenusApi.deleteMenu,
        onSuccess: (_v, deletedId) => {
            queryClient.setQueryData(['menusData', deletedId], undefined);
        },
        onSettled: () => queryClient.invalidateQueries({queryKey: ['menusData']}),
    });

    return {
        savePositionsMutation,
        addMenuMutation,
        updateMenuMutation,
        deleteMenuMutation,
    }
}