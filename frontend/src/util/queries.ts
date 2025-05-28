import {useQuery} from "@tanstack/react-query";
import {DishesApi} from "@/services/dishes-api.ts";
import {FilesApi} from "@/services/files-api.ts";

export function useDishes() {
    return useQuery({
        queryKey: ['dishesData'],
        queryFn: DishesApi.getAllDishes,
    }).data;
}

export function useImages() {
    return useQuery({
        queryKey: ['imagesData'],
        queryFn: FilesApi.getAllImages,
    }).data
}
