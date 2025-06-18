import React, {useEffect} from 'react';
import { Link } from 'react-router-dom';
import {cn, useDisplayCategories} from '@/util';
import BeButton from "@/components/ui/be-button.tsx";
import {useCustomerSessionContext} from "@/context/customer-session-context.ts";

type CategoriesSidebarProps = {
  selectedCategoryId?: string;
  onCategorySelect: (categoryId: string) => void;
};

const CategoriesSidebar: React.FC<CategoriesSidebarProps> = ({
  selectedCategoryId,
  onCategorySelect,
}) => {
  const { data: displayCategories, isLoading, error } = useDisplayCategories();

  const { customerSession, removeCustomerSession } = useCustomerSessionContext();

  useEffect(() => {
    if (!selectedCategoryId) {
      onCategorySelect(displayCategories?.[0]?.id ?? '');
    }
  }, [displayCategories, onCategorySelect, selectedCategoryId]);

  if (isLoading) {
    return <div className="p-4">Loading categories...</div>;
  }

  if (error) {
    return <div className="p-4 text-red-500">Error loading categories</div>;
  }

  if (!displayCategories || displayCategories.length === 0) {
    return <div className="p-4">No categories available</div>;
  }

  return (
      <div className="flex flex-col gap-2 h-full overflow-hidden justify-between">
        <ul className="space-y-2">
          {displayCategories.map((category) => (
              <li key={category.id}>
                <Link
                    to={`/category/${category.id}`}
                    className={cn(
                        "block w-full text-left px-4 py-2 rounded-md transition-colors",
                        selectedCategoryId === category.id ? "bg-[#292c36] text-white" : "hover:bg-gray-100"
                    )}
                    onClick={(e) => {
                      e.preventDefault();
                      onCategorySelect(category.id);
                    }}
                >
                  {category.name}
                </Link>
              </li>
          ))}
        </ul>
        {!!customerSession?.order?.items?.length && <div className="mt-auto">
          <div className="mt-2 text-center text-lg font-bold">Bestellung</div>
          <div className="mb-4">
            {customerSession.order.items.length < 6 ?
                customerSession.order.items.map(item => (
                    <dl key={item.id} className="text-sm border-t-1 mt-1 pt-1 border-gray-200 grid grid-rows-2 grid-cols-2 gap-0">
                      <dt className="row-span-2">{item.item?.name}</dt>
                      <dd className="text-xs text-gray-400 text-right">{item.amount} x {item.item?.price}€</dd>
                      <dd className="text-right"><span className="text-xs text-gray-400">=</span> {item.price}€</dd>
                    </dl>
                )) :
                <>{customerSession.order.items.reduce((acc, item) => acc + (item.amount ?? 0), 0)} Gerichte</>
            }
            <dl className="text-sm font-bold border-t-3 border-double mt-1 pt-1 border-gray-800 grid grid-rows-2 grid-cols-2 gap-0">
              <dt>Gesamt</dt>
              <dd className="text-right">{customerSession.order.totalPrice}€</dd>
            </dl>
            <Link to="/checkout" className="btn btn-primary block text-center w-full mt-2" >Bestellung bezahlen</Link>
          </div>
        </div>}
        <BeButton className="w-full" onClick={() => removeCustomerSession()}>Bestellung abbrechen</BeButton>
      </div>
  );
};

export default CategoriesSidebar;
