import React from 'react';
import { DisplayItemOutputDTO } from '@/types/DisplayItemOutputDTO.ts';
import Card from '@/components/shared/card.tsx';
import { cn, getColoredIconElement, getIconColor, getIconElement } from '@/util';
import { colorMapCards } from '@/data';

type CustomerDisplayItemProps = {
  displayItem: DisplayItemOutputDTO;
  className?: string;
};

const CustomerDisplayItem: React.FC<CustomerDisplayItemProps> = ({
  displayItem,
  className,
}) => {
  return (
    <Card
      header={displayItem.name}
      className={cn(
        className,
        "min-h-0 transition-[transform] duration-300 ease-in-out hover:scale-105"
      )}
      colorVariant={colorMapCards['displayItem']}
      typeCircle={getIconElement('displayItem')}
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
    >
      {displayItem.description && (
        <blockquote 
          className={cn(
            "text-sm text-gray-500 bg-white/10 mx-2 p-2 rounded-md shadow-xs relative",
            "before:absolute before:-bottom-6 before:-left-2 before:content-[open-quote] before:text-3xl before:text-shadow-sm",
            "after:absolute after:-top-2 after:-right-2 after:content-[close-quote] after:text-3xl after:text-shadow-sm",
            "hover:bg-gray-50 max-h-9 hover:max-h-24 transition-[max-height,background-color] duration-300 ease-in-out]"
          )}
        >
          <div className="not-hover:text-nowrap not-hover:text-ellipsis overflow-hidden transition-[overflow] hover:delay-300 hover:overflow-y-scroll max-h-20">
            {displayItem.description}
          </div>
        </blockquote>
      )}
    </Card>
  );
};

export default CustomerDisplayItem;