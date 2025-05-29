import { type AdditionalInformationDTO, isAdditionalInformationDTO } from "./AdditionalInformationDTO.ts";
import { type DishOutputDTO, isDishOutputDTO } from "./DishOutputDTO.ts";

export type MenuOutputDTO = {
    id: string;
    name: string;
    price: string;
    mainDishes: DishOutputDTO[];
    sideDishes: DishOutputDTO[];
    beverages: DishOutputDTO[];
    additionalInformation: Record<string, AdditionalInformationDTO>;
};

export function isMenuOutputDTO(item: unknown): item is MenuOutputDTO {
    return item !== null
        && typeof item === 'object'
        && 'id' in item
        && 'name' in item
        && 'price' in item
        && 'mainDishes' in item
        && 'sideDishes' in item
        && 'beverages' in item
        && 'additionalInformation' in item
        && typeof item.id === 'string'
        && typeof item.name === 'string'
        && typeof item.price === 'string'
        && Array.isArray(item.mainDishes)
        && Array.isArray(item.sideDishes)
        && Array.isArray(item.beverages)
        && item.mainDishes.every(isDishOutputDTO)
        && item.sideDishes.every(isDishOutputDTO)
        && item.beverages.every(isDishOutputDTO)
        && item.additionalInformation instanceof Object
        && !Array.isArray(item.additionalInformation)
        && Object.values(item.additionalInformation).every(isAdditionalInformationDTO);
}