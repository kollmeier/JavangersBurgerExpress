import { type AdditionalInformationDTO, isAdditionalInformationDTO } from "./AdditionalInformationDTO.ts";
import { type DishOutputDTO, isDishOutputDTO } from "./DishOutputDTO.ts";

export type MenuOutputDTO = {
    id: string;
    name: string;
    price: string;
    dishes: DishOutputDTO[];
    additionalInformation: Record<string, AdditionalInformationDTO>;
};

export function isMenuOutputDTO(item: unknown): item is MenuOutputDTO {
    return item !== null
        && typeof item === 'object'
        && 'id' in item
        && 'name' in item
        && 'price' in item
        && 'dishes' in item
        && 'additionalInformation' in item
        && typeof item.id === 'string'
        && typeof item.name === 'string'
        && typeof item.price === 'string'
        && Array.isArray(item.dishes)
        && item.dishes.every(isDishOutputDTO)
        && item.additionalInformation instanceof Object
        && !Array.isArray(item.additionalInformation)
        && Object.values(item.additionalInformation).every(isAdditionalInformationDTO);
}