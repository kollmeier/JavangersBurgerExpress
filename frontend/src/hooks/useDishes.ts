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

    function setDishes(dishesOrSetter: DishOutputDTO[] | ((prev: DishOutputDTO[]) => DishOutputDTO[])) {
        if (Array.isArray(dishesOrSetter)) {
            setState(prev => ({ ...prev, dishes: dishesOrSetter }));
            return
        }
        setState(prev => ({ ...prev, dishes: dishesOrSetter(prev.dishes) }));
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

    const withAddedDishAtFirst = (dishes: DishOutputDTO[], dish: DishOutputDTO) => {
        return [dish, ...dishes.filter(d => d.id !== dish.id)];
    }

    const withUpdatedDish = (dishes: DishOutputDTO[], dish: DishOutputDTO) => {
        return dishes.map(d => d.id === dish.id ? dish : d);
    }

    const withRemovedDish = (dishes: DishOutputDTO[], dishId: string) => {
        return dishes.filter(d => d.id !== dishId);
    }

    const addDish = (newDish: DishInputDTO) => {
        setLoading(true);
        setError(null);
        return DishesApi.saveDish(newDish)
            .then((savedDish) => {
                if (savedDish && isDishOutputDTO(savedDish)) {
                    setDishes(prev => withAddedDishAtFirst(prev, savedDish));
                    return savedDish;
                }
                setError("Ungültige Antwort beim Speichern des Gerichts")
                throw new TypeError("Ungültige Antwort beim Speichern des Gerichts");
            })
            .catch(e => {
                setError(e.message);
                throw e;
            })
            .finally(() => setLoading(false));
    }

    const updateDish = (newDish: DishInputDTO, dishId: string) => {
        setLoading(true);
        setError(null);
        return DishesApi.updateDish(newDish, dishId)
            .then((updatedDish) => {
                if (updatedDish && isDishOutputDTO(updatedDish)) {
                    setDishes(prev => withUpdatedDish(prev, updatedDish));
                    return updatedDish;
                }
                setError("Ungültige Antwort beim Speichern des Gerichts")
                throw new TypeError("Ungültige Antwort beim Speichern des Gerichts");
            })
            .catch(e => {
                setError(e.message);
                throw e;
            })
            .finally(() => setLoading(false));
    }

    const deleteDish = (dishId: string) => {
        setLoading(true);
        setError(null);
        return DishesApi.deleteDish(dishId)
            .then(() => {
                setDishes(prev => withRemovedDish(prev, dishId));
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
        addDish,
        updateDish,
        deleteDish
    };
}