import React, {useEffect} from 'react';
import { Link } from 'react-router-dom';
import {cn, useDisplayCategories} from '@/util';
import BeButton from "@/components/ui/be-button.tsx";
import {useCustomerSessionContext} from "@/context/customer-session-context.ts";
import {Ban, CreditCard} from "lucide-react";
import BeButtonLink from "@/components/ui/be-button-link.tsx";
import {OrderOutputDTO} from "@/types/OrderOutputDTO.ts";
import ScrollableWithIndicators from "@/components/ui/ScrollableWithIndicators.tsx";

type CategoriesSidebarProps = {
  selectedCategoryId?: string;
  onCategorySelect: (categoryId: string) => void;
};

type SmallorderSummaryProps = {
  order: OrderOutputDTO;
}

const SmallOrderSummary: React.FC<SmallorderSummaryProps> = ({order}) => {
  return <>
    <div className="flex-0 text-center text-lg font-bold font-heading">Bestellung</div>
    <ScrollableWithIndicators
        post={<dl className="text-sm font-bold border-t-3 border-double -mt-1 pt-1 border-gray-800 grid grid-rows-1 grid-cols-2 gap-0">
          <dt>Gesamt</dt>
          <dd className="text-right font-mono">{order.totalPrice}€</dd>
        </dl>
    }
    >
      {order.items?.map(item =>
          <dl key={item.id}
              className="text-sm border-t-1 mt-1 pt-1 border-gray-200 grid grid-rows-2 grid-cols-2 gap-0">
            <dt className="row-span-2">{item.item?.name}</dt>
            <dd className="text-xs text-gray-400 text-right font-mono">{item.amount} x {item.item?.price}€</dd>
            <dd className="text-right font-mono"><span className="text-xs text-gray-400 ">=</span> {item.price}€</dd>
          </dl>
      )}
    </ScrollableWithIndicators>
    <BeButtonLink to="/checkout" className="flex-0 btn btn-primary flex items-center justify-between w-full mt-2" icon={CreditCard} iconClassName="!mt-0 !h-6">Bestellung bezahlen</BeButtonLink>
  </>;
}

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
      <div className="flex flex-col justify-between gap-3 h-full">
        <ul className="flex-1 space-y-2">
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
        {!!customerSession?.order?.items?.length &&
          <SmallOrderSummary order={customerSession.order} />
        }
        <BeButton className="flex-0 h-min w-full flex items-center justify-between" onClick={() => removeCustomerSession()} icon={Ban} iconClassName="!mt-0 !h-5">Bestellung abbrechen</BeButton>
      </div>
  );
};

export default CategoriesSidebar;
