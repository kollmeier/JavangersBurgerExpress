import './App.css'
import CustomerLayout from "./components/CustomerLayout.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import AdministrationLayout from "./components/AdministrationLayout.tsx";

function App() {
  return (
    <BrowserRouter>
        <Routes>
            <Route path="/" element={<CustomerLayout />} />
            <Route path="manage//*" element={<AdministrationLayout />}>
            </Route>
        </Routes>
    </BrowserRouter>
  )
}

export default App
