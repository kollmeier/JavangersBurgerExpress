import {useQuery} from "@tanstack/react-query";
import {DishesApi} from "@/services/dishes-api.ts";
import {FilesApi} from "@/services/files-api.ts";
import {MenusApi} from "@/services/menus-api.ts";
import {DisplayCategoriesApi} from "@/services/display-categories-api.ts";
import {DisplayItemsApi} from "@/services/display-items-api.ts";
import {OrderableItemsApi} from "@/services/orderable-items-api.ts";

export function useDishes() {
    return useQuery({
        queryKey: ['dishesData'],
        staleTime: 30000,
        queryFn: DishesApi.getAllDishes,
    }).data;
}

export function useMenus() {
    return useQuery({
        queryKey: ['menusData'],
        staleTime: 30000,
        queryFn: MenusApi.getAllMenus,
    }).data;
}

export function useImages() {
    return useQuery({
        queryKey: ['imagesData'],
        staleTime: 30000,
        queryFn: FilesApi.getAllImages,
    }).data
}




export function useDisplayCategories() {
    return useQuery({
        queryKey: ['displayCategoriesData'],
        staleTime: 30000,
        queryFn: DisplayCategoriesApi.getAllDisplayCategories,
    });
}

export function useDisplayItems() {
    return useQuery({
        queryKey: ['displayItemsData'],
        queryFn: DisplayItemsApi.getAllDisplayItems,
        staleTime: 30000,
    })
}

export function useOrderableItems() {
    return useQuery({
        queryKey: ['orderableItemsData'],
        staleTime: 30000,
        queryFn: OrderableItemsApi.getAllOrderableItems,
    }).data;
}

export function useOrderableMenus() {
    return useQuery({
        queryKey: ['orderableMenusData'],
        staleTime: 30000,
        queryFn: OrderableItemsApi.getAllMenus,
    }).data;
}

export function useOrderableDishes() {
    return useQuery({
        queryKey: ['orderableDishesData'],
        staleTime: 30000,
        queryFn: OrderableItemsApi.getAllDishes,
    }).data;
}
