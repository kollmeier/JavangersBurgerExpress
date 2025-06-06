import { OrderableItemOutputDTO, isOrderableItemOutputDTO } from './OrderableItemOutputDTO';

export type DisplayItemOutputDTO = {
    id: string;
    categoryId: string;
    name: string;
    description: string;
    orderableItems: OrderableItemOutputDTO[];
    price: string;
    oldPrice: string | null;
    published: boolean;
};

export function isDisplayItemOutputDTO(item: unknown): item is DisplayItemOutputDTO {
    return (
        typeof item === 'object' &&
        item !== null &&
        'id' in item &&
        'categoryId' in item &&
        'name' in item &&
        'description' in item &&
        'orderableItems' in item &&
        'price' in item &&
        'published' in item &&
        typeof item.id === 'string' &&
        typeof item.categoryId === 'string' &&
        typeof item.name === 'string' &&
        typeof item.description === 'string' &&
        Array.isArray(item.orderableItems) &&
        (item.orderableItems.length === 0 || item.orderableItems.every(isOrderableItemOutputDTO)) &&
        typeof item.price === 'string' &&
        ('oldPrice' in item ? item.oldPrice === null || typeof item.oldPrice === 'string' : true) &&
        typeof item.published === 'boolean'
    );
}
