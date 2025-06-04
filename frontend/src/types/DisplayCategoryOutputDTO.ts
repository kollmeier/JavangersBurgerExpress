export type DisplayCategoryOutputDTO = {
    id: string;
    name: string;
    description: string;
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
        'imageUrl' in item &&
        'published' in item &&
        typeof item.id === 'string' &&
        typeof item.name === 'string' &&
        typeof item.description === 'string' &&
        typeof item.imageUrl === 'string' &&
        typeof item.published === 'boolean'
    );
}