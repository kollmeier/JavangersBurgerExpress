export type DisplayCategoryInputDTO = {
    name: string;
    description: string;
    imageUrl: string;
    published: boolean | null;
};

export type DisplayCategoryInputDTOWithId = DisplayCategoryInputDTO & {id?: string}