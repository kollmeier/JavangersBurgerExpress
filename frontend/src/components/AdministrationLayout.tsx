import {PageLayoutContextProvider} from "../context/PageLayoutContextProvider.tsx";
import AdministrationHeader from "./AdministrationHeader.tsx";
import AdministrationFooter from "./AdministrationFooter.tsx";

function AdministrationLayout() {
    return (<PageLayoutContextProvider
        header={<AdministrationHeader />}
        footer={<AdministrationFooter />}
    >
        <>Bald mehr Inhalt für den Administrator</>
    </PageLayoutContextProvider>);
}

export default AdministrationLayout;