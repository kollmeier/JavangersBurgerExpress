import {type AdditionalInformationDTO, isAdditionalInformationDTO} from "./AdditionalInformationDTO.ts";

export type DishOutputDTO = {
    id: string;
    name: string;
    price: string;
    type: 'main' | 'side' | 'beverage';
    additionalInformation: Record<string, AdditionalInformationDTO>;
    imageUrl: string;
}

export function isDishOutputDTO(item: unknown): item is DishOutputDTO {
    return item !== null
        && typeof item === 'object'
        && 'id' in item
        && 'name' in item
        && 'price' in item
        && 'type' in item
        && 'additionalInformation' in item
        && typeof item.id === 'string'
        && typeof item.name === 'string'
        && typeof item.price === 'string'
        && typeof item.type === 'string'
        && (item.type === 'main' || item.type === 'side' || item.type === 'beverage')
        && item.additionalInformation instanceof Object && !Array.isArray(item.additionalInformation)
        && Object.values(item.additionalInformation).every(isAdditionalInformationDTO)
}
