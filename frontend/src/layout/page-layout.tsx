import React, { type PropsWithChildren, type ReactNode } from "react";

export type NavItem = {
    label: string;
    href: string;
};

export type PageLayoutProps = {
    header?: ReactNode;
    subHeader?: ReactNode;
    mainNav?: NavItem[];
    footer?: ReactNode;
};

export const PageLayout: React.FC<PropsWithChildren<PageLayoutProps>> = ({
    header,
    subHeader,
    mainNav,
    footer,
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
            <nav className="bg-[#ececec] px-8 py-2">
                <ul className="flex gap-6 list-none m-0 p-0">
                    {mainNav.map((item, index) => (
                        <li key={item.label + index}>
                            <a
                                href={item.href}
                                className="text-[#292c36] bg-[#f1f1f1] border-none font-inherit cursor-pointer no-underline px-4 py-2 rounded-md hover:bg-[#dbdbdb] transition"
                            >
                                {item.label}
                            </a>
                        </li>
                    ))}
                </ul>
            </nav>
        )}
        <div className="flex-1 flex justify-center items-stretch bg-[#f7f7f9]">
            <main className="flex flex-col max-w-[1378px] flex-1 p-4">
                <div className="flex-1 bg-white rounded-xl p-4 shadow-[0_2px_8px_rgba(80,90,110,0.04)]">
                    {children}
                </div>
            </main>
        </div>
        {footer && (
            <footer className="text-center bg-[#292c36] text-white px-8 py-4">
                {footer}
            </footer>
        )}
    </div>
);

export default PageLayout;