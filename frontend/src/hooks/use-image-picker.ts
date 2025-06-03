import {useState} from "react";
import {useImageMutations} from "@/hooks/use-image-mutations.ts";

export type ImagesSelection = {
    imageFiles: File[]
    existingImageUrls: string[]
}

export default function useImagePicker() {
    const [images, setImages] = useState<ImagesSelection>({imageFiles: [], existingImageUrls: []});

    const {addImageMutation} = useImageMutations();

    return {
        images,
        setImages,
        addImageMutation,
    }
}