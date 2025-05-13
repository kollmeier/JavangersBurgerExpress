import {useEffect, useState} from "react";
import {type DishOutputDTO, isDishOutputDTO} from "../types/DishOutputDTO.ts";
import { DishesApi } from "../services/DishesApi.ts";
import type {DishInputDTO} from "../types/DishInputDTO.ts";

export type DishesApi = ReturnType<typeof useDishes>;

type StateProps = {
    dishes: DishOutputDTO[];
    loading: boolean;
    error: string | null;
}

export default function useDishes() {
    const [state, setState] = useState<StateProps>({
        dishes: [],
        loading: false,
        error: null
    });

    function setDishes(dishes: DishOutputDTO[]) {
        setState(prev => ({ ...prev, dishes }));
    }

    function setError(error: string | null) {
        setState(prev => ({ ...prev, error }));
    }

    function setLoading(loading: boolean) {
        setState(prev => ({ ...prev, loading }));
    }

    useEffect(() => {
        setLoading(true);
        DishesApi.getAllDishes()
            .then(setDishes)
            .catch(e => setError(e.message))
            .finally(() => setLoading(false));
    }, []);

    const addDish = (newDish: DishInputDTO) => {
        setLoading(true);
        setError(null);
        return DishesApi.saveDish(newDish)
            .then((savedDish) => {
                if (savedDish && isDishOutputDTO(savedDish)) {
                    const newDishes = state.dishes.filter(m => m.id !== savedDish.id);
                    newDishes.unshift(savedDish);
                    setDishes(newDishes);
                    return savedDish;
                }
                setError("Ungültige Antwort beim Speichern des Gerichts")
                throw "Ungültige Antwort beim Speichern des Gerichts";
            })
            .catch(e => {
                setError(e.message);
                throw e;
            })
            .finally(() => setLoading(false));
    }

    return {
        dishes: state.dishes,
        loading: state.loading,
        error: state.error,
        addDish
    };
}