import React, {type ReactNode} from "react";
import "./PageLayout.scss";

export type PageLayoutProps = {
    header?: ReactNode;
    subHeader?: ReactNode;
    footer?: ReactNode;
    children: ReactNode;
};

export const PageLayout: React.FC<PageLayoutProps> = (props) => {
    const { header, subHeader, footer, children } = props;

    return (
        <div className="page-layout">
            {header && <header className="pl-header">
                {header}
                {subHeader && <h2>{subHeader}</h2>}
            </header>}
            <div className="pl-content-area">
                <main className="pl-main">
                    <div className="pl-content">{children}</div>
                </main>
            </div>
            {footer && <footer className="pl-footer">{footer}</footer>}
        </div>
    )
};

export default PageLayout;