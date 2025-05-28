export type FileInfoDTO = {
    id: string
    uri: string
    fileName: string
    contentType: string
}

export function isFileInfoDTO(obj: unknown): obj is FileInfoDTO {
    return (
        obj !== null
        && typeof obj === 'object'
        && 'id' in obj
        && typeof obj.id === 'string'
        && 'uri' in obj
        && typeof obj.uri === 'string'
        && 'fileName' in obj
        && typeof obj.fileName === 'string'
        && 'contentType' in obj
        && typeof obj.contentType === 'string'
    );
}