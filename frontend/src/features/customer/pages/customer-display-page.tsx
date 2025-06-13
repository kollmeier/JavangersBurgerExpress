import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { usePageLayoutContext } from '@/context/page-layout-context.ts';
import CategoriesSidebar from '../components/categories-sidebar';
import DisplayItemsGrid from '../components/display-items-grid';
import { useDisplayCategories, useCustomerSession } from '@/util';
import { DisplayItemOutputDTO } from '@/types/DisplayItemOutputDTO.ts';
import {DisplayCategoryOutputDTO} from "@/types/DisplayCategoryOutputDTO.ts";
import Card from "@/components/shared/card.tsx";
import BeDialog from "@/components/shared/be-dialog.tsx";
import {faBurger, faClock} from "@fortawesome/free-solid-svg-icons";

const CustomerDisplayPage: React.FC = () => {
  const [displayItems, setDisplayItems] = useState<DisplayItemOutputDTO[]>([]);
  const [category, setCategory] = useState<DisplayCategoryOutputDTO | undefined>();
  const { categoryId } = useParams<{ categoryId?: string }>();
  const navigate = useNavigate();

  const { data: displayCategories, isLoading, error } = useDisplayCategories();
  const { setSidebar } = usePageLayoutContext();
  const [sessionInterval, setSessionInterval] = useState<number | undefined>(undefined);

  const { customerSession, createCustomerSession, renewCustomerSession, removeCustomerSession } = useCustomerSession(sessionInterval);

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
      setCategory(undefined);
    } else {
      // Filter items by selected category
      const category = displayCategories.find(c => c.id === categoryId);
      setDisplayItems(category?.displayItems ?? []);
      setCategory(category);
    }
  }, [displayCategories, categoryId, renewCustomerSession]);

  useEffect(() => {
    renewCustomerSession()
  }, [displayCategories, categoryId, renewCustomerSession]);

  useEffect(() => {
    if (!customerSession) {
      setSessionInterval(undefined);
      return;
    }
    if (customerSession.expired) {
      removeCustomerSession();
      return;
    }
    if (customerSession.expiresInSeconds < 30) {
      setSessionInterval(1);
      return;
    }
    setSessionInterval(30);
  }, [customerSession, removeCustomerSession]);

  return (
    <div>
      <Card
          colorVariant="primary"
          className="mb-6"
          header={"Unser Angebot: " + category?.name}
          headerClassName={"text-red-900"}
          image={<img src={category?.imageUrl} alt={category?.name} className="w-full h-full" />}
          imageClassName="row-start-head row-end-foot row-head_foot self-center"
      >{category?.description}</Card>
      <DisplayItemsGrid
        displayItems={displayItems}
        isLoading={isLoading}
        error={error}
      />
      <BeDialog
          icon={faBurger}
          className="text-xl"
          title="Jetzt bestellen!"
          onClick={() => createCustomerSession()}
          open={!customerSession || customerSession.expired}
          onClose={() => createCustomerSession()}>
          Bestellen Sie jetzt! Berühren Sie den Bildschirm, um den Bestellvorgang zu starten.
      </BeDialog>
      <BeDialog
          icon={faClock}
          className="text-xl"
          title="Sind Sie noch da?"
          onClick={() => renewCustomerSession()}
          open={!!customerSession && customerSession.expiresInSeconds <= 30}
          onClose={() => renewCustomerSession()}>
        <div>Sind Sie noch da? Berühren Sie den Bildschirm zum fortfahren!</div>
        {customerSession && <div className="text-red-500 text-3xl">Automatisches Abmelden in {customerSession.expiresInSeconds} Sekunden</div>}
      </BeDialog>
    </div>
  );
};

export default CustomerDisplayPage;
