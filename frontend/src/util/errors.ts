import axios from "axios";

export type ErrorDTO = {
    error: string;
    cause: string | null;
    causeMessage: string | null;
    message: string;
    timestamp: string;
    status: string;
    path: string | null;
};

export function isErrorDTO(item: unknown): item is ErrorDTO {
    return (
        typeof item === 'object' &&
        item !== null &&
        'error' in item &&
        'message' in item &&
        'timestamp' in item &&
        'status' in item &&
        'cause' in item &&
        'causeMessage' in item &&
        'path' in item &&
        typeof item.error === 'string' &&
        (item.cause === null || typeof item.cause === 'string') &&
        (item.causeMessage === null || typeof item.causeMessage === 'string') &&
        typeof item.message === 'string' &&
        typeof item.timestamp === 'string' &&
        typeof item.status === 'string' &&
        (item.path === null || typeof item.path === 'string')
    );
}

export class ApiError extends Error {
    error: ErrorDTO;

    constructor(message: string, error: ErrorDTO) {
        super(message);
        this.name = 'ApiError';
        this.error = error;

        // Maintains proper stack trace for where our error was thrown (only available on V8)
        if (Error.captureStackTrace) {
            Error.captureStackTrace(this, ApiError);
        }
    }
}

export function throwErrorByResponse(error: unknown) {
    if (axios.isAxiosError(error) && isErrorDTO(error.response?.data)) {
        throw new ApiError(error.response.data.message ?? "Unbekannter Fehler", error.response.data);
    }
    throw new Error("Unbekannter Fehler");
}

export function errorMessage(error: unknown) {
    if (error instanceof ApiError) {
        return error.error.message;
    }
    return "Unbekannter Fehler";
}