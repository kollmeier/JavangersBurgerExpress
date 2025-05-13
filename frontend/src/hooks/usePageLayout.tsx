import {type ReactNode, useState} from "react";
import PageLayout, {type NavItem, type PageLayoutProps} from "../components/PageLayout.tsx";

export type PageLayoutApi = ReturnType<typeof usePageLayout>;

export type StateProps = PageLayoutProps

export default function usePageLayout() {
    const [state, setState] = useState<StateProps>({
        subHeader: undefined,
        footer: undefined,
        mainNav: [],
        header: undefined
    });

    const render = (content: ReactNode) => {
        return (
            <PageLayout
                header={state.header}
                subHeader={state.subHeader}
                mainNav={state.mainNav}
                footer={state.footer} >{content}</PageLayout>)
    }

    return {
        header: state.header,
        subHeader: state.subHeader,
        footer: state.footer,
        mainNav: state.mainNav,
        render,
        setHeader: (header: ReactNode) => setState(prev => ({ ...prev, header })),
        setSubHeader: (subHeader?: ReactNode) => setState(prev => ({ ...prev, subHeader })),
        setFooter: (footer: ReactNode) => setState(prev => ({ ...prev, footer })),
        setMainNav: (mainNav: NavItem[]) => setState(prev => ({ ...prev, mainNav })),
    };
}