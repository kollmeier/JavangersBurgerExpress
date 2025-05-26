import {type AdditionalInformationInputDTO} from "./AdditionalInformationInputDTO.ts";

export type DishInputDTO = {
    name: string;
    price: string;
    type: string;
    additionalInformation: Record<string, AdditionalInformationInputDTO>;
}

export type DishInputDTOWithId = DishInputDTO & {
    id?: string;
}