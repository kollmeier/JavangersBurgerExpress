import CustomerLayout from "./layout/customer-layout.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import AdministrationLayout from "./layout/administration-layout.tsx";
import DishesPage from "./features/manager/dishes/pages/dishes-page.tsx";
import {ToastContainer, Zoom} from "react-toastify";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {ReactQueryDevtools} from "@tanstack/react-query-devtools";
import MenusPage from "@/features/manager/menus/pages/menus-page.tsx";
import DisplayItemsPage from "@/features/manager/display-items/pages/display-items-page.tsx";

const queryClient = new QueryClient();

function App() {
  return (
      <>
          <QueryClientProvider client={queryClient}>
              <ReactQueryDevtools initialIsOpen={true} />
              <BrowserRouter>
                <Routes>
                    <Route path="/" element={<CustomerLayout />} />
                    <Route path="manage//*" element={<AdministrationLayout />}>
                        <Route path="dishes//*" element={<DishesPage />} />
                        <Route path="dishes/:dishId/*" element={<DishesPage />} />
                        <Route path="menus//*" element={<MenusPage />} />
                        <Route path="menus/:menuId/*" element={<MenusPage />} />
                        <Route path="displayItems//*" element={<DisplayItemsPage />} />
                        <Route path="displayItems/:displayItemId/*" element={<DisplayItemsPage />} />
                        <Route path="displayItems/category/:displayCategoryId/*" element={<DisplayItemsPage />} />
                    </Route>
                </Routes>
            </BrowserRouter>
          </QueryClientProvider>
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
