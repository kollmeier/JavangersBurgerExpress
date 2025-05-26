import {useQuery} from "@tanstack/react-query";
import {DishesApi} from "@/services/dishes-api.ts";

export function useDishes() {
    return useQuery({
        queryKey: ['dishesData'],
        queryFn: DishesApi.getAllDishes,
    }).data;
}
