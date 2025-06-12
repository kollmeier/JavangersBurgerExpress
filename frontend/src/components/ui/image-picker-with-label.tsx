import InputWithLabel from "@/components/ui/input-with-label.tsx";
import {Button} from "@headlessui/react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCamera} from "@fortawesome/free-solid-svg-icons";
import {faClose} from "@fortawesome/free-solid-svg-icons/faClose";
import ImagePickerDialog from "@/components/shared/image-picker-dialog.tsx";
import {ControllerFieldState, ControllerRenderProps, FieldValues} from "react-hook-form";
import React from "react";
import {ImagesSelection} from "@/hooks/use-image-picker.ts";

type ImagePickerWithLabelProps<T extends FieldValues> = {
    field: ControllerRenderProps<T>;
    fieldState: ControllerFieldState;
    setImages: React.Dispatch<ImagesSelection>;
    className?: string;
}

const ImagePickerWithLabel = <T extends FieldValues> ({
    field,
    fieldState,
    setImages,
    className
}: ImagePickerWithLabelProps<T>) => {
    const [imagePickerDialogOpen, setImagePickerDialogOpen] = React.useState<boolean>(false);

    const handleImagesSelected = (onChange: (value: string) => void, files: File[], existingImageUrls: string[]) => {
        setImages({imageFiles: files, existingImageUrls: existingImageUrls});
        setImagePickerDialogOpen(false);
        onChange(existingImageUrls[0] ?? URL.createObjectURL(files[0]) ?? '');
    }

    return (
        <InputWithLabel
            label="Bild"
            type="hidden"
            fieldClassName={className}
            error={fieldState.error?.message}
            {...field}
        >
            <div className="p-0.5 bg-gray-200 h-full rounded-xl ">
                <Button
                    type="button"
                    className="flex flex-col justify-center items-center rounded-xl border-2 w-full h-full border-dotted border-gray-400 p-0.5"
                    onClick={() => setImagePickerDialogOpen(true)}
                >
                    {field.value ?
                        <img className="object-contain max-h-full max-w-full" src={field.value} alt="Produktbild"/> :
                        <>
                            <FontAwesomeIcon icon={faCamera} className="h-full text-3xl text-gray-400"/>
                            <span className="text-sm text-gray-600">Klicken zur Auswahl</span>
                        </>}
                </Button>
                {field.value && <Button
                    className="absolute top-0 right-0 m-2 text-lg text-gray-700"
                    onClick={(e) => {
                        e.preventDefault();
                        field.onChange('');
                    }}
                >
                    <FontAwesomeIcon icon={faClose}/>
                </Button>}
            </div>
            <ImagePickerDialog
                open={imagePickerDialogOpen}
                onClose={() => {setImagePickerDialogOpen(false);}}
                values={[field.value]}
                multiple={false}
                onImagesSelected={(...args) => handleImagesSelected(field.onChange, ...args)}
            />
        </InputWithLabel>
    )
}

export default ImagePickerWithLabel;