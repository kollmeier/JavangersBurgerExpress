import {type ReactNode, useState} from "react";
import PageLayout from "../components/PageLayout.tsx";

export type PageLayoutApi = ReturnType<typeof usePageLayout>;

type StateProps = {
    header?: ReactNode;
    subHeader?: ReactNode;
    footer?: ReactNode;
}

export default function usePageLayout() {
    const [state, setState] = useState<StateProps>({
        subHeader: undefined,
        footer: undefined,
        header: undefined
    });

    const render = (content: ReactNode) => {
        return (
            <PageLayout
                header={state.header}
                subHeader={state.subHeader}
                footer={state.footer} >{content}</PageLayout>)
    }

    return {
        header: state.header,
        subHeader: state.subHeader,
        footer: state.footer,
        render,
        setHeader: (header: ReactNode) => setState(prev => ({ ...prev, header })),
        setSubHeader: (subHeader?: ReactNode) => setState(prev => ({ ...prev, subHeader })),
        setFooter: (footer: ReactNode) => setState(prev => ({ ...prev, footer })),
    };
}