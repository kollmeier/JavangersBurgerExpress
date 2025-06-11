import React, { type PropsWithChildren, type ReactNode } from "react";
import {NavLink} from "react-router-dom";
import {cn} from "@/util";

export type NavItem = {
    label: string;
    href?: string;
    className?: string;
    element?: ReactNode;
};

export type PageLayoutProps = {
    header?: ReactNode;
    subHeader?: ReactNode;
    actions?: ReactNode;
    mainNav?: NavItem[];
    footer?: ReactNode;
    sidebar?: ReactNode;
};

export const PageLayout: React.FC<PropsWithChildren<PageLayoutProps>> = ({
    header,
    subHeader,
    actions,
    mainNav,
    footer,
    sidebar,
    children
}) => (
    <div className="flex flex-col min-h-screen">
        {header && (
            <header className="text-center bg-[#292c36] text-white px-8 py-4">
                {header}
                {subHeader && <h2>{subHeader}</h2>}
            </header>
        )}
        {mainNav && (
            <nav className="bg-[#ececec] px-0 py-1 flex justify-center items-stretch">
                <ul className="flex flex-row flex-1 justify-start max-w-[1378px] gap-6 list-none m-0 px-4 py-1">
                    {mainNav.map((item, index) => (
                        <li key={item.label + index} className={cn("block", item.className)}>
                            {item.element ?? (<NavLink
                                to={item.href ?? "#"}
                                className="text-[#292c36] bg-[#f1f1f1] block border-none font-inherit cursor-pointer no-underline px-3 py-1.5 rounded-md hover:bg-[#dbdbdb] transition"
                            >
                                {item.label}
                            </NavLink>)}
                        </li>
                        ))}
                </ul>
            </nav>
        )}
        <div className="flex-1 flex flex-wrap justify-center items-stretch bg-[#f7f7f9]">
            <div className="flex flex-row max-w-[1378px] flex-1">
                {sidebar && (
                    <aside className="w-64 p-4 text-black">
                        {sidebar}
                    </aside>
                )}
                <main className={`flex gap-2 flex-col flex-1 p-4 ${sidebar ? 'w-[calc(100%-16rem)]' : 'w-full'}`}>
                    {actions && <div
                        className="flex flex-row justify-end shrink-1 bg-white text-black rounded-xl p-2 shadow-[0_2px_8px_rgba(80,90,110,0.04)]">{actions}</div>}
                    <div className="grow-1 bg-white rounded-xl p-4 shadow-[0_2px_8px_rgba(80,90,110,0.04)]">
                        {children}
                    </div>
                </main>
            </div>
        </div>
        {footer && (
            <footer className="text-center bg-[#292c36] text-white px-8 py-4">
                {footer}
            </footer>
        )}
    </div>
);

export default PageLayout;
