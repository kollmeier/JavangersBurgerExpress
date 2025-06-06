import { DisplayItemOutputDTO, isDisplayItemOutputDTO } from './DisplayItemOutputDTO';

export type DisplayCategoryOutputDTO = {
    id: string;
    name: string;
    description: string;
    displayItems: DisplayItemOutputDTO[];
    imageUrl: string;
    published: boolean;
};

export function isDisplayCategoryOutputDTO(item: unknown): item is DisplayCategoryOutputDTO {
    return (
        typeof item === 'object' &&
        item !== null &&
        'id' in item &&
        'name' in item &&
        'description' in item &&
        'displayItems' in item &&
        'imageUrl' in item &&
        'published' in item &&
        typeof item.id === 'string' &&
        typeof item.name === 'string' &&
        typeof item.description === 'string' &&
        Array.isArray(item.displayItems) &&
        (item.displayItems.length === 0 || item.displayItems.every(isDisplayItemOutputDTO)) &&
        typeof item.imageUrl === 'string' &&
        typeof item.published === 'boolean'
    );
}
