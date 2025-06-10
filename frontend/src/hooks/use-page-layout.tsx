import {type ReactNode, useState} from "react";
import PageLayout, {type NavItem, type PageLayoutProps} from "../layout/page-layout.tsx";

export type PageLayoutApi = ReturnType<typeof usePageLayout>;

export type StateProps = PageLayoutProps

export default function usePageLayout() {
    const [state, setState] = useState<StateProps>({
        subHeader: undefined,
        footer: undefined,
        mainNav: [],
        actions: undefined,
        header: undefined
    });

    const render = (content: ReactNode) => {
        return (
            <PageLayout
                header={state.header}
                subHeader={state.subHeader}
                mainNav={state.mainNav}
                actions={state.actions}
                footer={state.footer} >{content}</PageLayout>)
    }

    return {
        header: state.header,
        subHeader: state.subHeader,
        actions: state.actions,
        footer: state.footer,
        mainNav: state.mainNav,
        render,
        setHeader: (header: ReactNode) => setState(prev => ({ ...prev, header })),
        setSubHeader: (subHeader?: ReactNode) => setState(prev => ({ ...prev, subHeader })),
        setFooter: (footer: ReactNode) => setState(prev => ({ ...prev, footer })),
        setMainNav: (mainNav: NavItem[]) => setState(prev => ({ ...prev, mainNav })),
        setActions: (actions: ReactNode) => setState(prev => ({ ...prev, actions })),
    };
}