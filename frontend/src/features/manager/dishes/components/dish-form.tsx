import React, {useRef} from "react";
import type {DishOutputDTO} from "@/types/DishOutputDTO.ts";
import type {DishInputDTO} from "@/types/DishInputDTO.ts";
import {Controller, useForm} from "react-hook-form";
import InputWithLabel from "@/components/ui/input-with-label";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSave} from "@fortawesome/free-solid-svg-icons/faSave";
import {faClose} from "@fortawesome/free-solid-svg-icons/faClose";
import BeButton from "@/components/ui/be-button.tsx";
import {Button, Input} from "@headlessui/react";
import ImagePickerDialog from "@/components/shared/image-picker-dialog.tsx";
import {toast} from "react-toastify";
import {faCamera} from "@fortawesome/free-solid-svg-icons";
import useImagePicker from "@/hooks/use-image-picker.ts";

type Props = {
    dish?: DishOutputDTO;
    dishType: 'main' | 'side' | 'beverage';
    onSubmit?:  (dish: DishInputDTO, dishId?: string) => Promise<void>;
    onCancel?: () => void;
}

const DishForm = ({ dish, dishType, onSubmit, onCancel }: Props)=> {
    const {
        control,
        handleSubmit,
        reset,
    } = useForm<DishInputDTO>({
        values: dish,
        defaultValues: {
            name: '',
            price: '',
            type: dishType,
            imageUrl: '',
            additionalInformation: {
                description: {
                    type: 'PLAIN_TEXT',
                    value: ''
                },
                size: dishType === 'beverage' ? {
                    type: 'SIZE_IN_LITER',
                    value: ''
                } : undefined
            }
        }
    });

    const {images, addImageMutation, imagePickerDialogOpen, setImagePickerDialogOpen, handleImagesSelected} = useImagePicker();

    const formRef = useRef<HTMLFormElement>(null);

    const handleCancel = (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => {
        event.preventDefault();
        if (onCancel) {
            onCancel();
        }
    }
    const doSubmit = async (submittedDish: DishInputDTO) => {
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
                    const editedDish = {...submittedDish, imageUrl: files[0].uri ?? null};
                    if (onSubmit) {
                        (async () => {
                            await onSubmit(editedDish, dish?.id);
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
            await onSubmit(submittedDish, dish?.id);
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
                        fieldClassName="col-start-1 -col-end-1"
                        error={fieldState.error?.message}
                        {...field}
                    />
                )}
            />
            <Controller
                name="price"
                control={control}
                rules={{ required: "Bitte geben Sie einen Preis an!" }}
                render={({ field, fieldState }) => (
                    <InputWithLabel
                        {...field}
                        label="Preis"
                        value={field.value}
                        placeholder="0,00"
                        type="number"
                        inputMode="decimal"
                        fieldClassName="col-span-2"
                        error={fieldState.error?.message}
                    />
                )}
            />
            {dishType === 'beverage' &&
                <Controller
                    name="additionalInformation.size.value"
                    control={control}
                    rules={{ required: "Bitte geben Sie eine Größe an!" }}
                    render={({ field, fieldState }) => (
                        <InputWithLabel
                            {...field}
                            label="Größe in Liter"
                            value={field.value}
                            placeholder="0,00"
                            type="number"
                            inputMode="decimal"
                            fieldClassName="col-span-2"
                            error={fieldState.error?.message}
                        />
                    )}
                />
            }
            <Controller
                name="type"
                control={control}
                render={({ field }) => (
                <Input type="hidden" {...field} />)} />
            <Controller
                name="additionalInformation.description.value"
                control={control}
                render={({ field, fieldState }) => (
                    <InputWithLabel
                        label="Beschreibung"
                        type="textarea"
                        className="h-24"
                        fieldClassName="col-span-3 row-start-3"
                        error={fieldState.error?.message}
                        {...field}
                    />
                )}
            />
            <Controller
                name="imageUrl"
                control={control}
                render={({ field, fieldState }) => (
                    <InputWithLabel
                        label="Bild"
                        type="hidden"
                        fieldClassName="col-span-1 row-start-3"
                        error={fieldState.error?.message}
                        {...field}
                    >
                        <div className="p-0.5 relative w-24 h-24 bg-gray-200 rounded-xl ">
                            <Button
                                type="button"
                                className="rounded-xl border-2 border-dotted border-gray-400 p-0.5 h-full w-full"
                                onClick={() => setImagePickerDialogOpen(true)}
                            >
                                {field.value ?
                                    <img className="w-full h-full object-contain" src={field.value} alt="Produktbild"/> :
                                    <>
                                        <FontAwesomeIcon icon={faCamera} className="w-full h-full text-xl text-gray-400"/>
                                        <span className="text-xs text-gray-600">Klicken zur Auswahl</span>
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
                    </InputWithLabel>)}
            />
            <div className="row-actions col-start-1 -col-end-1 flex gap-2 justify-end border-t pt-2 w-full">
                <BeButton type="submit" variant="primary"><FontAwesomeIcon icon={faSave}/> Speichern</BeButton>
                <BeButton type="button" onClick={handleCancel}><FontAwesomeIcon icon={faClose}/> Abbrechen</BeButton>
            </div>
        </form>

);
}

export default DishForm;