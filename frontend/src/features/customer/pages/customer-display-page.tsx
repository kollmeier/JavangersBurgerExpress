import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { usePageLayoutContext } from '@/context/page-layout-context.ts';
import CategoriesSidebar from '../components/categories-sidebar';
import DisplayItemsGrid from '../components/display-items-grid';
import { useDisplayCategories } from '@/util';
import { DisplayItemOutputDTO } from '@/types/DisplayItemOutputDTO.ts';

const CustomerDisplayPage: React.FC = () => {
  const [displayItems, setDisplayItems] = useState<DisplayItemOutputDTO[]>([]);
  const { categoryId } = useParams<{ categoryId?: string }>();
  const navigate = useNavigate();

  const { data: displayCategories, isLoading, error } = useDisplayCategories();
  const { setSidebar } = usePageLayoutContext();

  // Redirect to first category if no category is specified
  useEffect(() => {
    if (!displayCategories || displayCategories.length === 0) return;

    if (!categoryId) {
      // Redirect to the first category if no category is specified
      navigate(`/category/${displayCategories[0].id}`);
    }
  }, [categoryId, displayCategories, navigate]);

  // Set the sidebar with the categories
  useEffect(() => {
    setSidebar(
      <CategoriesSidebar
        selectedCategoryId={categoryId}
        onCategorySelect={(id) => navigate(`/category/${id}`)}
      />
    );
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [categoryId, displayCategories]);

  // Update display items when categories or selected category changes
  useEffect(() => {
    if (!displayCategories) return;

    if (!categoryId) {
      // If no category is selected yet (before redirect), don't show any items
      setDisplayItems([]);
    } else {
      // Filter items by selected category
      const category = displayCategories.find(c => c.id === categoryId);
      setDisplayItems(category?.displayItems || []);
    }
  }, [displayCategories, categoryId]);

  return (
    <div>
      <h1 className="text-2xl text-black font-bold mb-6">Unser Angebot</h1>
      <DisplayItemsGrid
        displayItems={displayItems}
        isLoading={isLoading}
        error={error}
      />
    </div>
  );
};

export default CustomerDisplayPage;
