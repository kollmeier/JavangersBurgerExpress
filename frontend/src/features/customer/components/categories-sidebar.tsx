import React from 'react';
import { Link } from 'react-router-dom';
import { cn, useDisplayCategories } from '@/util';

type CategoriesSidebarProps = {
  selectedCategoryId?: string;
  onCategorySelect: (categoryId: string) => void;
};

const CategoriesSidebar: React.FC<CategoriesSidebarProps> = ({
  selectedCategoryId,
  onCategorySelect,
}) => {
  const { data: displayCategories, isLoading, error } = useDisplayCategories();

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
    <div className="bg-white rounded-xl p-4 shadow-[0_2px_8px_rgba(80,90,110,0.04)] h-full">
      <h2 className="text-xl font-bold mb-4">Kategorien</h2>
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
    </div>
  );
};

export default CategoriesSidebar;
