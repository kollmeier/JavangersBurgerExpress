import axios from "axios";
import {isFileInfoDTO} from "../types/FileInfoDTO.ts";
import {QueryFunctionContext} from "@tanstack/react-query";
import {throwErrorByResponse} from "@/util/errors.ts";

export const FilesApi = {
    baseUrl: '/api/files',

    cancelableUpload: {} as Record<string, AbortController | null>,

    async getAllImages({signal}: QueryFunctionContext) {
        try {
            const response = await axios.get(FilesApi.baseUrl, {signal: signal});
            if (Array.isArray(response.data) && response.data.every(isFileInfoDTO)) {
                return response.data;
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return [];
            }
            throwErrorByResponse(error);
        }
        throw new TypeError("Ungültige Antwort beim Laden der Bilder");
    },

    async upload(files: File[]){
        const uploadPromises = files.map(file => {
            FilesApi.cancelableUpload[file.name]?.abort();
            FilesApi.cancelableUpload[file.name] = new AbortController();

            const formData = new FormData();
            formData.append('file', file);

            return axios.post(FilesApi.baseUrl + '/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
                signal: FilesApi.cancelableUpload[file.name]?.signal
            }).then((response) => {
                if (isFileInfoDTO(response.data)) {
                    return response.data;
                }
                throw new TypeError("Ungültige Antwort beim Upload");
            }).catch((error) => {
                if (axios.isCancel(error)) {
                    return null;
                }
                throwErrorByResponse(error);
            });
        });
        return await Promise.all(uploadPromises);
    }
}