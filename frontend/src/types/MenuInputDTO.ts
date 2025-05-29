import { type AdditionalInformationInputDTO } from "./AdditionalInformationInputDTO.ts";

export type MenuInputDTO = {
    name: string;
    price: string;
    mainDishIds: string[];
    sideDishIds: string[];
    beverageIds: string[];
    additionalInformation: Record<string, AdditionalInformationInputDTO>;
};

export type MenuInputDTOWithId = MenuInputDTO & {
    id?: string;
};