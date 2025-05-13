import './App.css'
import CustomerLayout from "./components/CustomerLayout.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import AdministrationLayout from "./components/administration/AdministrationLayout.tsx";
import {DishesContextProvider} from "./context/DishesContextProvider.tsx";
import DishesPage from "./components/administration/DishesPage.tsx";
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
