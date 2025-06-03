import { type AdditionalInformationInputDTO } from "./AdditionalInformationInputDTO.ts";

export type MenuInputDTO = {
    name: string;
    price: string;
    dishIds: string[];
    additionalInformation: Record<string, AdditionalInformationInputDTO>;
};

export type MenuInputDTOWithId = MenuInputDTO & {
    id?: string;
};