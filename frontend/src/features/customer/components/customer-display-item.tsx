import React, {useState} from 'react';
import { DisplayItemOutputDTO } from '@/types/DisplayItemOutputDTO.ts';
import Card, {CardProps} from '@/components/shared/card.tsx';
import {cn, getColoredIconElement, getIconColor} from '@/util';
import { colorMapCards } from '@/data';
import BeButton from "@/components/ui/be-button.tsx";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAdd, faSubtract} from "@fortawesome/free-solid-svg-icons";
import DishImages from "@/components/ui/dish-images.tsx";
import {OrderInputDTO} from "@/types/OrderInputDTO.ts";
import {OrderItemInputDTO} from "@/types/OrderItemInputDTO.ts";
import {useCustomerSessionContext} from "@/context/customer-session-context.ts";

type CustomerDisplayItemProps = {
  displayItem: DisplayItemOutputDTO;
} & CardProps;

const CustomerDisplayItem: React.FC<CustomerDisplayItemProps> = ({
    displayItem,
    className,
    ...props
}) => {
  const [amount, setAmount] = useState(1);
  const {customerSession, renewCustomerSession, storeOrder} = useCustomerSessionContext();

  function increaseAmount() {
      renewCustomerSession();
      setAmount(a => a + 1);
  }

  function decreaseAmount() {
      renewCustomerSession();
      setAmount(a => a > 2 ? a - 1 : 1);
  }

  function addToOrder() {
      if (customerSession) {
          const items: OrderItemInputDTO[] = (customerSession.order?.items?.map(i => ({
              id: i.id ?? undefined, amount: i.amount, item: i.item?.id
          })) ?? []);
          const item: OrderItemInputDTO = {
              item: displayItem.orderableItems[0].id,
              amount: amount,
          };
          const order: OrderInputDTO = {
              id: customerSession.order?.id,
              items: items.concat(item),
          };
          storeOrder(order);
          setAmount(1);
      }
  }

  return (
    <Card
      header={displayItem.name}
      headerClassName="self-start"
      className={cn("bg-transparent text-gray-600 drop-shadow-none rounded-none h-auto mb-6",
          className)}
      colorVariant={colorMapCards['displayItem']}
      priceCircle={
        <div className="flex flex-col items-center">
          {displayItem.oldPrice && (
            <span className="text-[0.6em] line-through">
              {displayItem.oldPrice.replace('.', ',')}€
            </span>
          )}
          {displayItem.price.replace('.', ',')}€
        </div>
      }
      image={<DishImages
          className="w-full h-full top-0 object-contain"
          mainImages={displayItem.orderableItems.flatMap(o => o.imageUrls["MAIN"] ?? [])}
          sideImages={displayItem.orderableItems.flatMap(o => o.imageUrls["SIDE"] ?? [])}
          beverageImages={displayItem.orderableItems.flatMap(o => o.imageUrls["BEVERAGE"] ?? [])}
      />}
      imageClassName="w-full h-full row-start-head row-end-actions row-head_actions row-span-2"
      footer={
        <div className="flex flex-wrap gap-1">
          {displayItem.orderableItems.map(orderableItem => (
            <span 
              key={displayItem.id + orderableItem.id} 
              className={cn("pill !text-sm", getIconColor(orderableItem.type, "light"))}
            >
              {getColoredIconElement(orderableItem.type, "bg-transparent")} {orderableItem.name}
            </span>
          ))}
        </div>
      }
      actions={
        <div className="flex flex-wrap gap-2">
            <BeButton onClick={decreaseAmount}><FontAwesomeIcon icon={faSubtract}/></BeButton>
            <span className="pt-1">{amount}</span>
            <BeButton onClick={increaseAmount}><FontAwesomeIcon icon={faAdd}/></BeButton>
            <BeButton variant="primary" onClick={addToOrder}>Bestellen</BeButton>
        </div>
      }
      {...props}
    >
      {displayItem.description && (
        <div>{displayItem.description}</div>
      )}
    </Card>
  );
};

export default CustomerDisplayItem;