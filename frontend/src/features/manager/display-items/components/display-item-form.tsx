import React, {useRef} from "react";
import type {MenuOutputDTO} from "@/types/MenuOutputDTO.ts";
import type {MenuInputDTO} from "@/types/MenuInputDTO.ts";
import {Controller, useForm} from "react-hook-form";
import InputWithLabel from "@/components/ui/input-with-label";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSave} from "@fortawesome/free-solid-svg-icons/faSave";
import {faClose} from "@fortawesome/free-solid-svg-icons/faClose";
import BeButton from "@/components/ui/be-button.tsx";
import ComboboxWithLabel from "@/components/ui/combobox-with-label.tsx";
import {useDishes} from "@/util";
import {DishOutputDTO} from "@/types/DishOutputDTO.ts";
import {faCamera} from "@fortawesome/free-solid-svg-icons";

type Props = {
    menu?: MenuOutputDTO;
    onSubmit?:  (menu: MenuInputDTO, menuId?: string) => Promise<void>;
    onCancel?: () => void;
}

const DishOption = ({dish}: {dish: DishOutputDTO}) => {
    return (
        <div className="flex items-center justify-between gap-2">
            <span>{dish.imageUrl ?
                <img className="h-fit object-contain" src={dish.imageUrl + '?size=55'} alt="Produktbild"/> :
                <FontAwesomeIcon icon={faCamera} className="h-4 text-xl text-gray-400"/>
            }</span>
            <span>{dish.name}, {dish.price}€</span>
        </div>
    )
}

const DisplayItemForm = ({ menu, onSubmit, onCancel }: Props)=> {
    const {
        control,
        handleSubmit,
        reset,
    } = useForm<MenuInputDTO>({
        values: {
            name: menu?.name ?? '',
            price: menu?.price ?? '',
            dishIds: menu?.dishes.map(dish => dish.id) ?? [],
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
            dishIds: [],
            additionalInformation: {
                description: {
                    type: 'PLAIN_TEXT',
                    value: ''
                },
            }
        }
    });

    const dishes = useDishes();

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
                        fieldClassName="col-span-1"
                        error={fieldState.error?.message}
                    />
                )}
            />
            <Controller
                name="dishIds"
                control={control}
                rules={{ required: "Suchen Sie mindestens ein Gericht aus!" }}
                render={({ field, fieldState }) => (
                    <ComboboxWithLabel<DishOutputDTO>
                        label="Gerichte"
                        fieldClassName="col-span-3 row-start-2"
                        multiple={true}
                        options={dishes ?? []}
                        error={fieldState.error?.message}
                        {...field}
                        onChange={(selectedDishes) => field.onChange(
                            Array.isArray(selectedDishes)
                                ? selectedDishes.map(dish => dish.id)
                                : []
                        )}
                        optionElement={dish => <DishOption dish={dish}/>}
                        summaryElement={field.value && dishes && ((value: DishOutputDTO | DishOutputDTO[]) =>
                            (Array.isArray(value) && value.length > 0 ? <span className="text-gray-300 text-xs">= {value.reduce(
                                (acc, dish) => acc + parseFloat(dish.price ?? "0"),
                                0).toFixed(2)}€</span> : undefined))}
                        value={dishes?.filter(dish => field.value.includes(dish.id)) ?? []}
                    />
                )}
            />
            <Controller
                name="additionalInformation.description.value"
                control={control}
                render={({ field, fieldState }) => (
                    <InputWithLabel
                        label="Beschreibung"
                        type="textarea" // This is the fix. The tag was not closed properly.
                        className="h-24"
                        fieldClassName="col-span-4 row-start-3"
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
    )};

export default DisplayItemForm;