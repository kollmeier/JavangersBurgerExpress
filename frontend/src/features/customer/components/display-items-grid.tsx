import React from 'react';
import { DisplayItemOutputDTO } from '@/types/DisplayItemOutputDTO.ts';
import CustomerDisplayItem from './customer-display-item';

type DisplayItemsGridProps = {
  displayItems: DisplayItemOutputDTO[];
  isLoading?: boolean;
  error?: unknown;
};

const DisplayItemsGrid: React.FC<DisplayItemsGridProps> = ({
  displayItems,
  isLoading,
  error,
}) => {
  if (isLoading) {
    return <div className="p-4">Loading items...</div>;
  }

  if (error) {
    return <div className="p-4 text-red-500">Error loading items</div>;
  }

  if (!displayItems || displayItems.length === 0) {
    return <div className="p-4">No items available</div>;
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {displayItems.map((item) => (
        <CustomerDisplayItem
          key={item.id}
          displayItem={item}
          className="h-full"
        />
      ))}
    </div>
  );
};

export default DisplayItemsGrid;