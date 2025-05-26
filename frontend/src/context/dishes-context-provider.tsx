import React from "react";
import DishesContext from "./dishes-context.ts";
import useDishes from "../hooks/useDishes.ts";

export const DishesContextProvider: React.FC<{children: React.ReactNode}> = ({ children }) => {
  return (
    <DishesContext.Provider value={useDishes()}>
      {children}
    </DishesContext.Provider>
  );
};