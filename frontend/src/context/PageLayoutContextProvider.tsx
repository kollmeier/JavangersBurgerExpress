import React, {type PropsWithChildren, useEffect, useRef} from "react";
import PageLayoutContext from "./PageLayoutContext.ts";
import {type PageLayoutProps} from "../components/PageLayout.tsx";
import usePageLayout, {type PageLayoutApi} from "../hooks/usePageLayout.tsx";

type PageLayoutContextProps = PropsWithChildren<PageLayoutProps>;

export const PageLayoutContextProvider: React.FC<PageLayoutContextProps> = ({
    header,
    subHeader,
    mainNav,
    footer,
    children
}) => {
    const pageLayout = usePageLayout();
    const { setHeader, setSubHeader, setMainNav, setFooter, render} = pageLayout

    const wrappers = useRef<Partial<PageLayoutApi>>({});

    useEffect(() => {
        wrappers.current = {setHeader, setSubHeader, setFooter, setMainNav}; // wrap to aviod render loops
    }, [setFooter, setHeader, setMainNav, setSubHeader]);

    useEffect(() => {
        if (header && wrappers.current?.setHeader) {
            wrappers.current.setHeader(header);
        }
    }, [header, wrappers]);

    useEffect(() => {
        if (subHeader && wrappers.current?.setSubHeader) {
            wrappers.current.setSubHeader(subHeader);
        }
    }, [subHeader, wrappers]);

    useEffect(() => {
        if (mainNav && wrappers.current?.setMainNav) {
            wrappers.current.setMainNav(mainNav);
        }
    }, [mainNav, wrappers]);

    useEffect(() => {
        if (footer && wrappers.current?.setFooter) {
            wrappers.current.setFooter(footer);
        }
    }, [footer, wrappers]);

    return(
        <PageLayoutContext.Provider value={pageLayout}>
            {render(children)}
        </PageLayoutContext.Provider>
    )
}