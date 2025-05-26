import React, {useRef} from "react";
import type {DishOutputDTO} from "@/types/DishOutputDTO.ts";
import type {DishInputDTO} from "@/types/DishInputDTO.ts";
import {Controller, useForm} from "react-hook-form";
import InputWithLabel from "@/components/ui/input-with-label";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSave} from "@fortawesome/free-solid-svg-icons/faSave";
import {faClose} from "@fortawesome/free-solid-svg-icons/faClose";
import BeButton from "@/components/ui/be-button.tsx";
import {Input} from "@headlessui/react";

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
        defaultValues: {
            name: dish?.name ?? '',
            price: dish?.price ?? '',
            type: dishType,
            additionalInformation: {
                description: {
                    type: 'PLAIN_TEXT',
                    value: dish?.additionalInformation?.description?.value ?? ''
                },
                size: dishType === 'beverage' ? {
                    type: 'SIZE_IN_LITER',
                    value: dish?.additionalInformation?.size?.value ?? ''
                } : undefined
            }
        }
    });

    const formRef = useRef<HTMLFormElement>(null);

    const handleCancel = (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => {
        event.preventDefault();
        if (onCancel) {
            onCancel();
        }
    }

    const doSubmit = async (submittedDish: DishInputDTO) => {
        if (onSubmit) {
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
                        fieldClassName="col-span-4"
                        error={fieldState.error?.message}
                        {...field}
                    />
                )}
            />
            <div className="row-actions col-start-1 -col-end-1 flex gap-2 justify-end border-t pt-2 w-full">
                <BeButton type="submit" variant="primary"><FontAwesomeIcon icon={faSave}/> Speichern</BeButton>
                <BeButton type="button" onClick={handleCancel}><FontAwesomeIcon icon={faClose}/> Abbrechen</BeButton>
            </div>
        </form>

);
}

export default DishForm;