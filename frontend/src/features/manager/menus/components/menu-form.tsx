import React, {useRef} from "react";
import type {MenuOutputDTO} from "@/types/MenuOutputDTO.ts";
import type {MenuInputDTO} from "@/types/MenuInputDTO.ts";
import {Controller, useForm} from "react-hook-form";
import InputWithLabel from "@/components/ui/input-with-label";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSave} from "@fortawesome/free-solid-svg-icons/faSave";
import {faClose} from "@fortawesome/free-solid-svg-icons/faClose";
import BeButton from "@/components/ui/be-button.tsx";

type Props = {
    menu?: MenuOutputDTO;
    onSubmit?:  (menu: MenuInputDTO, menuId?: string) => Promise<void>;
    onCancel?: () => void;
}

const MenuForm = ({ menu, onSubmit, onCancel }: Props)=> {
    const {
        control,
        handleSubmit,
        reset,
    } = useForm<MenuInputDTO>({
        values: {
            name: menu?.name ?? '',
            price: menu?.price ?? '',
            mainDishIds: menu?.mainDishes.map(dish => dish.id) ?? [],
            sideDishIds: menu?.sideDishes.map(dish => dish.id) ?? [],
            beverageIds: menu?.beverages.map(dish => dish.id) ?? [],
            additionalInformation: {
                description: {
                    type: 'PLAIN_TEXT',
                    value: menu?.additionalInformation.description.value ?? ''
                }
            }
        },
        defaultValues: {
            name: '',
            price: '',
            mainDishIds: [],
            sideDishIds: [],
            beverageIds: [],
            additionalInformation: {
                description: {
                    type: 'PLAIN_TEXT',
                    value: ''
                },
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
    const doSubmit = async (submittedMenu: MenuInputDTO) => {
        if (!onSubmit) {
            return;
        }
        await onSubmit(submittedMenu, menu?.id);
        reset();
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
            <div className="row-actions col-start-1 -col-end-1 flex gap-2 justify-end border-t pt-2 w-full">
                <BeButton type="submit" variant="primary"><FontAwesomeIcon icon={faSave}/> Speichern</BeButton>
                <BeButton type="button" onClick={handleCancel}><FontAwesomeIcon icon={faClose}/> Abbrechen</BeButton>
            </div>
        </form>

);
}

export default MenuForm;