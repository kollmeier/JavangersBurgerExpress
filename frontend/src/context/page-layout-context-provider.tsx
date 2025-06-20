import React, {type PropsWithChildren, useEffect} from "react";
import PageLayoutContext from "./page-layout-context.ts";
import {type PageLayoutProps} from "../layout/page-layout.tsx";
import usePageLayout from "../hooks/use-page-layout.tsx";

type PageLayoutContextProps = PropsWithChildren<PageLayoutProps>;

export const PageLayoutContextProvider: React.FC<PageLayoutContextProps> = ({
    header,
    subHeader,
    mainNav,
    footer,
    sidebar,
    children
}) => {
    const pageLayout = usePageLayout();
    const { setHeader, setSubHeader, setMainNav, setFooter, setSidebar, render} = pageLayout

    useEffect(() => {
        if (header) {
            setHeader(header);
        }
        if (subHeader) {
            setSubHeader(subHeader);
        }
        if (mainNav) {
            setMainNav(mainNav);
        }
        if (footer) {
            setFooter(footer);
        }
        if (sidebar) {
            setSidebar(sidebar);
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return(
        <PageLayoutContext.Provider value={pageLayout}>
            {render(children)}
        </PageLayoutContext.Provider>
    )
}
