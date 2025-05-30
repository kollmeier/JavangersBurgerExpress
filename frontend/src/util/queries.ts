import {useQuery} from "@tanstack/react-query";
import {DishesApi} from "@/services/dishes-api.ts";
import {FilesApi} from "@/services/files-api.ts";
import {MenusApi} from "@/services/menus-api.ts";

export function useDishes() {
    return useQuery({
        queryKey: ['dishesData'],
        queryFn: DishesApi.getAllDishes,
    }).data;
}

export function useMenus() {
    return useQuery({
        queryKey: ['menusData'],
        queryFn: MenusApi.getAllMenus,
    }).data;
}

export function useImages() {
    return useQuery({
        queryKey: ['imagesData'],
        queryFn: FilesApi.getAllImages,
    }).data
}
