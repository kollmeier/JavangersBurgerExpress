import './App.css'
import CustomerLayout from "./components/CustomerLayout.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import AdministrationLayout from "./components/administration/AdministrationLayout.tsx";
import {DishesContextProvider} from "./context/DishesContextProvider.tsx";
import DishesPage from "./components/administration/DishesPage.tsx";

function App() {
  return (
    <BrowserRouter>
        <Routes>
            <Route path="/" element={<CustomerLayout />} />
            <Route path="manage//*" element={<AdministrationLayout />}>
                <Route path="dishes//*" element={<DishesContextProvider><DishesPage /></DishesContextProvider>} />
                <Route path="dishes/:dishId/*" element={<DishesContextProvider><DishesPage /></DishesContextProvider>} />
            </Route>
        </Routes>
    </BrowserRouter>
  )
}

export default App
