export type DisplayItemInputDTO = {
    name: string;
    description: string;
    categoryId: string;
    hasActualPrice: boolean;
    actualPrice: string;
    orderableItemIds: string[];
    published: boolean;
};

export type DisplayItemInputDTOWithId = DisplayItemInputDTO & {id?: string}