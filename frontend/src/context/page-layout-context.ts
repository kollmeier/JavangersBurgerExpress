import {createContext, useContext} from "react";
import type {PageLayoutApi} from "../hooks/usePageLayout.tsx";

const PageLayoutContext = createContext<PageLayoutApi | undefined>(undefined);

export function usePageLayoutContext() {
    const ctx = useContext(PageLayoutContext);
    if (!ctx) throw new Error("usePageLayoutContext muss innerhalb von PageLayoutProvider verwendet werden!");
    return ctx;
}

export default PageLayoutContext;