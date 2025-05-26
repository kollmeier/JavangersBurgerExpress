import CustomerLayout from "./layout/customer-layout.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import AdministrationLayout from "./layout/administration-layout.tsx";
import {DishesContextProvider} from "./context/dishes-context-provider.tsx";
import DishesPage from "./features/manager/dishes/pages/dishes-page.tsx";
import {ToastContainer, Zoom} from "react-toastify";

function App() {
  return (
      <>
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<CustomerLayout />} />
                <Route path="manage//*" element={<AdministrationLayout />}>
                    <Route path="dishes//*" element={<DishesContextProvider><DishesPage /></DishesContextProvider>} />
                    <Route path="dishes/:dishId/*" element={<DishesContextProvider><DishesPage /></DishesContextProvider>} />
                </Route>
            </Routes>
        </BrowserRouter>
        <ToastContainer
            position="bottom-center"
            autoClose={4000}
            hideProgressBar={false}
            newestOnTop
            closeOnClick={false}
            rtl={false}
            pauseOnFocusLoss={false}
            draggable
            pauseOnHover
            theme="light"
            transition={Zoom}
        />
    </>
  )
}

export default App
