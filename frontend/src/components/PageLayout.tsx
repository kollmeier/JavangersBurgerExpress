import React, {type PropsWithChildren, type ReactNode} from "react";
import "./PageLayout.scss";

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
    <div className="page-layout">
        {header && <header className="pl-header">
            {header}
            {subHeader && <h2>{subHeader}</h2>}
        </header>}
        {mainNav && (<nav className="pl-nav">
            <ul>
                {mainNav.map((item, index) => (
                    <li key={item.label + index}>
                        <a href={item.href}>{item.label}</a>
                    </li>
                ))}
            </ul>
        </nav>)}
        <div className="pl-content-area">
            <main className="pl-main">
                <div className="pl-content">{children}</div>
            </main>
        </div>
        {footer && <footer className="pl-footer">{footer}</footer>}
    </div>
);

export default PageLayout;