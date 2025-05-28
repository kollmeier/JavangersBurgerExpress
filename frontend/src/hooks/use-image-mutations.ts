import {useMutation, useQueryClient} from "@tanstack/react-query";
import {FilesApi} from "@/services/files-api.ts";

export function useImageMutations() {
    const queryClient = useQueryClient();

    const addImageMutation = useMutation({
        mutationFn: FilesApi.upload,
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        onSuccess: (savedFiles, _submittedFiles) => {
            savedFiles.forEach(savedFile => {
                queryClient.setQueryData(['imagesData', savedFile.id], savedFile);
            })
        },
        onSettled: () => queryClient.invalidateQueries({queryKey: ['imagesData']}),
    });

    return {
        addImageMutation,
    }
}