export type AdditionalInformationDTO = {
    type: 'SIZE_IN_LITER' | 'PLAIN_TEXT';
    value: string;
    displayString: string;
    shortDisplayString: string;
}

export function isAdditionalInformationDTO(item: unknown): item is AdditionalInformationDTO {
    return item !== null
        && typeof item === 'object'
        && 'type' in item
        && 'value' in item
        && 'displayString' in item
        && 'shortDisplayString' in item
        && typeof item.type === 'string'
        && (item.type === 'SIZE_IN_LITER' || item.type === 'PLAIN_TEXT')
        && typeof item.value === 'string'
        && typeof item.displayString === 'string'
        && typeof item.shortDisplayString === 'string';
}