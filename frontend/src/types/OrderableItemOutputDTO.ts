export type OrderableItemOutputDTO = {
    id: string;
    name: string;
    oldPrice: string | null;
    price: string;
    type: string;
    imageUrls: Record<string, string[]>;
    descriptionForDisplay: string[];
    descriptionForCart: string[];
};

export function isOrderableItemOutputDTO(item: unknown): item is OrderableItemOutputDTO {
    return (
        typeof item === 'object' &&
        item !== null &&
        'id' in item &&
        'name' in item &&
        'oldPrice' in item &&
        'price' in item &&
        'type' in item &&
        'imageUrls' in item &&
        'descriptionForDisplay' in item &&
        'descriptionForCart' in item &&
        typeof item.id === 'string' &&
        typeof item.name === 'string' &&
        (item.oldPrice === null || typeof item.oldPrice === 'string') &&
        typeof item.price === 'string' &&
        typeof item.type === 'string' &&
        typeof item.imageUrls === 'object' &&
        Array.isArray(item.descriptionForDisplay) &&
        Array.isArray(item.descriptionForCart)
    );
}