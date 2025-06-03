import React, {useState} from "react";
import {useImageMutations} from "@/hooks/use-image-mutations.ts";

type ImagesSelection = {
    imageFiles: File[]
    existingImageUrls: string[]
}

export default function useImagePicker() {
    const [images, setImages] = useState<ImagesSelection>({imageFiles: [], existingImageUrls: []});

    const {addImageMutation} = useImageMutations();

    const [imagePickerDialogOpen, setImagePickerDialogOpen] = React.useState<boolean>(false);

    const handleImagesSelected = (onChange: (value: string) => void, files: File[], existingImageUrls: string[]) => {
        setImages({imageFiles: files, existingImageUrls: existingImageUrls});
        setImagePickerDialogOpen(false);
        onChange(existingImageUrls[0] ?? URL.createObjectURL(files[0]) ?? '');
    }

    return {
        imagePickerDialogOpen,
        setImagePickerDialogOpen,
        handleImagesSelected,
        images,
        setImages,
        addImageMutation,
    }
}