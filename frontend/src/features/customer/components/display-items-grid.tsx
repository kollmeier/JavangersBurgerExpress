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
    return <div className="p-4">Laden...</div>;
  }

  if (error) {
    return <div className="p-4 text-red-500">Fehler beim Laden!</div>;
  }

  if (!displayItems || displayItems.length === 0) {
    return <div className="p-4">Nichts zu bestellen, sorry!</div>;
  }

  return (
    <div className="grid grid-cols-1 auto-rows-min gap-6">
      {displayItems.map((item) => (
        <CustomerDisplayItem
          key={item.id}
          displayItem={item}
          className="h-fit"
        />
      ))}
    </div>
  );
};

export default DisplayItemsGrid;