import {createContext, useContext} from "react";
import type {DishesApi} from "../hooks/useDishes.ts";

type DishesContextType = DishesApi;

const DishesContext = createContext<DishesContextType | undefined>(undefined);

export default DishesContext;

export function useDishesContext() {
    const ctx = useContext(DishesContext);
    if (!ctx) throw new Error("useDishesContext muss innerhalb von DishesContextProvider verwendet werden!");
    return ctx;
}

export function useAddDishContext() {
    const ctx = useContext(DishesContext);
    if (!ctx) throw new Error("useAddDishContext muss innerhalb von DishesContextProvider verwendet werden!");
    return ctx.addDish;
}

