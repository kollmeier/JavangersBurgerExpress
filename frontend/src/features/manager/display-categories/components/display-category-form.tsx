import React, {useRef} from "react";
import type {DisplayCategoryOutputDTO} from "@/types/DisplayCategoryOutputDTO.ts";
import type {DisplayCategoryInputDTO} from "@/types/DisplayCategoryInputDTO.ts";
import {Controller, useForm} from "react-hook-form";
import InputWithLabel from "@/components/ui/input-with-label";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSave} from "@fortawesome/free-solid-svg-icons/faSave";
import {faClose} from "@fortawesome/free-solid-svg-icons/faClose";
import BeButton from "@/components/ui/be-button.tsx";
import {toast} from "react-toastify";
import useImagePicker from "@/hooks/use-image-picker.ts";
import ImagePickerWithLabel from "@/components/ui/image-picker-with-label.tsx";

type Props = {
    displayCategory?: DisplayCategoryOutputDTO;
    onSubmit?:  (displayCategory: DisplayCategoryInputDTO, displayCategoryId?: string) => Promise<void>;
    onCancel?: () => void;
}

const DisplayCategoryForm = ({ displayCategory, onSubmit, onCancel }: Props)=> {
    const {
        control,
        handleSubmit,
        reset,
    } = useForm<DisplayCategoryInputDTO>({
        values: {
            name: displayCategory?.name ?? '',
            description: displayCategory?.description ?? '',
            imageUrl: displayCategory?.imageUrl ?? '',
            published: displayCategory?.published ?? false,
        },
        defaultValues: {
            name: '',
            description: '',
            imageUrl: '',
            published: false,
        }
    });

    const {images, setImages, addImageMutation} = useImagePicker();

    const formRef = useRef<HTMLFormElement>(null);

    const handleCancel = (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => {
        event.preventDefault();
        if (onCancel) {
            onCancel();
        }
    }
    const doSubmit = async (submittedDisplayCategory: DisplayCategoryInputDTO) => {
        if (images.imageFiles.length > 0) {
            const toastId = toast.loading('Bild wird hochgeladen...');
            addImageMutation.mutate(images.imageFiles, {
                onSuccess: (files) => {
                    toast.update(toastId, {
                        render: 'Bild erfolgreich hochgeladen',
                        type: 'success',
                        isLoading: false,
                        autoClose: 5000,
                    });
                    const editedDisplayCategory = {...submittedDisplayCategory, imageUrl: files[0]?.uri ?? ''};
                    if (onSubmit) {
                        (async () => {
                            await onSubmit(editedDisplayCategory, displayCategory?.id);
                            reset();
                        })();
                    }
                },
                onError: (error) => {
                    toast.update(toastId, {
                        render: 'Fehler beim Hochladen des Bildes: ' + error.message,
                        type: 'error',
                        isLoading: false,
                        autoClose: 5000,
                    });
                }
            });
        } else if (onSubmit) {
            await onSubmit(submittedDisplayCategory, displayCategory?.id);
            reset();
        }
    }

    return (
        <form
            onSubmit={handleSubmit(doSubmit)}
            autoComplete="off"
            noValidate
            ref={formRef}
            className="grid grid-cols-4 grid-rows-card gap-2 grow-1"
        >
            <Controller
                name="name"
                control={control}
                rules={{ required: "Name ist erforderlich" }}
                render={({ field, fieldState }) => (
                    <InputWithLabel
                        label="Name"
                        fieldClassName="col-start-1 -col-end-2"
                        error={fieldState.error?.message}
                        {...field}
                    />
                )}
            />

            <Controller
                name="description"
                control={control}
                render={({ field, fieldState }) => (
                    <InputWithLabel
                        label="Beschreibung"
                        type="textarea"
                        className="h-36"
                        fieldClassName="col-span-3 row-start-2"
                        error={fieldState.error?.message}
                        {...field}
                    />
                )}
            />
            <Controller
                name="imageUrl"
                control={control}
                render={({ field, fieldState }) => (
                    <ImagePickerWithLabel
                        field={field}
                        fieldState={fieldState}
                        setImages={setImages}
                        className="col-span-1 row-span-2 row-start-1 h-34 min-h-fit min-w-fit"/>)}
            />
            <div className="row-actions col-start-1 -col-end-1 flex gap-2 justify-end border-t pt-2 w-full">
                <BeButton type="submit" variant="primary"><FontAwesomeIcon icon={faSave}/> Speichern</BeButton>
                <BeButton type="button" onClick={handleCancel}><FontAwesomeIcon icon={faClose}/> Abbrechen</BeButton>
            </div>
        </form>
    )};

export default DisplayCategoryForm;