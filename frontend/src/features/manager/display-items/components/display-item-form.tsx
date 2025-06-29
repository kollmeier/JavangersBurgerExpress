import React, {useRef} from "react";
import type {DisplayItemOutputDTO} from "@/types/DisplayItemOutputDTO.ts";
import type {DisplayItemInputDTO} from "@/types/DisplayItemInputDTO.ts";
import {
    Controller,
    ControllerRenderProps,
    useForm,
} from "react-hook-form";
import InputWithLabel from "@/components/ui/input-with-label";
import BeButton from "@/components/ui/be-button.tsx";
import ComboboxWithLabel from "@/components/ui/combobox-with-label.tsx";
import {OrderableItemOutputDTO} from "@/types/OrderableItemOutputDTO.ts";
import {useOrderableItems} from "@/util/queries.ts";
import {colorMapCards} from "@/data";
import {ArrowDownToLine, Camera, CircleX} from "lucide-react";

type Props = {
    displayItem?: DisplayItemOutputDTO;
    categoryId: string;
    onSubmit?:  (displayItem: DisplayItemInputDTO, displayItemId?: string) => Promise<void>;
    onCancel?: () => void;
}

const OrderableItemOption = ({orderableItem}: {orderableItem: OrderableItemOutputDTO}) => {
    return (
        <div className="flex items-center justify-between gap-2">
            <span>{Object.values(orderableItem.imageUrls).length > 0 ?
                <img className="h-fit object-contain" src={Object.values(orderableItem.imageUrls)[0] + '?size=55'} alt="Produktbild"/> :
                <Camera className="h-4 text-xl text-gray-400"/>
            }</span>
            <span className={colorMapCards[orderableItem.type] ?? ""}>{orderableItem.name}, {orderableItem.price}€</span>
        </div>
    )
}

const DisplayItemForm = ({ displayItem, categoryId, onSubmit, onCancel }: Props)=> {
    const {
        control,
        handleSubmit,
        reset,
        setValue,
        formState
    } = useForm<DisplayItemInputDTO>({
        values: {
            name: displayItem?.name ?? '',
            actualPrice: (displayItem?.oldPrice && displayItem.price) ?? '',
            hasActualPrice: !!(displayItem?.oldPrice && displayItem.price),
            description: displayItem?.description ?? '',
            orderableItemIds: displayItem?.orderableItems.map(orderableItem => orderableItem.id) ?? [],
            categoryId: categoryId,
            published: displayItem?.published ?? true,
        },
        defaultValues: {
            name: '',
            actualPrice: '',
            hasActualPrice: false,
            description: '',
            orderableItemIds: [],
            categoryId: categoryId,
            published: true,
        }
    });

    const orderableItems = useOrderableItems();

    const formRef = useRef<HTMLFormElement>(null);

    const autoNameAllowed = displayItem?.orderableItems?.map(o => o.name).join(', ') === displayItem?.name;
    const autoDescriptionAllowed = displayItem?.orderableItems?.flatMap(o => o.descriptionForDisplay).join(', ') === displayItem?.description;

    const handleCancel = (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => {
        event.preventDefault();
        if (onCancel) {
            onCancel();
        }
    }

    const handleItemChange = (selectedOrderableItems: OrderableItemOutputDTO | OrderableItemOutputDTO[], field: ControllerRenderProps<DisplayItemInputDTO, "orderableItemIds">) => {
        const newSelectedOrderableItems = Array.isArray(selectedOrderableItems)
            ? selectedOrderableItems.map(orderableItem => orderableItem.id)
            : [];
        field.onChange(newSelectedOrderableItems)
        if (!formState.dirtyFields.name && autoNameAllowed) {
            if (Array.isArray(selectedOrderableItems)) {
                setValue('name', selectedOrderableItems.map(orderableItem => orderableItem.name).join(', '), {});
            }
            else {
                setValue('name', selectedOrderableItems.name, {});
            }
        }
        if (!formState.dirtyFields.description && autoDescriptionAllowed) {
            if (Array.isArray(selectedOrderableItems)) {
                setValue('description', selectedOrderableItems.flatMap(orderableItem => orderableItem.descriptionForDisplay).join(', '), {});
            }
            else {
                setValue('description', selectedOrderableItems.descriptionForDisplay.join(', '), {});
            }
        }
    }

    const doSubmit = async (submittedDisplayItem: DisplayItemInputDTO) => {
        if (!onSubmit) {
            return;
        }
        // Clean up the form.
        if (submittedDisplayItem.actualPrice === '') {
            submittedDisplayItem.hasActualPrice = false;
        }
        if (!isNaN(parseFloat(submittedDisplayItem.actualPrice))) {
            submittedDisplayItem.hasActualPrice = true;
        }
        await onSubmit(submittedDisplayItem, displayItem?.id);
        reset();
    }

    return (
        <form
            onSubmit={handleSubmit(doSubmit)}
            autoComplete="off"
            noValidate
            ref={formRef}
            className="grid grid-cols-4 grid-rows-card gap-2"
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
                name="actualPrice"
                control={control}
                render={({ field, fieldState }) => (
                    <InputWithLabel
                        label="Preis"
                        placeholder="0,00"
                        type="number"
                        inputMode="decimal"
                        fieldClassName="col-span-1"
                        error={fieldState.error?.message}
                        {...field}
                    />
                )}
            />
            <Controller
                name="orderableItemIds"
                control={control}
                rules={{ required: "Suchen Sie mindestens ein Element aus!" }}
                render={({ field, fieldState }) => (
                    <ComboboxWithLabel<OrderableItemOutputDTO>
                        label="Verknüpfte Artikel"
                        fieldClassName="col-span-3 row-start-2"
                        multiple={true}
                        options={orderableItems ?? []}
                        error={fieldState.error?.message}
                        {...field}
                        onChange={(selectedOrderableItems) => handleItemChange(selectedOrderableItems, field)}
                        optionElement={orderableItem => <OrderableItemOption orderableItem={orderableItem}/>}
                        summaryElement={field.value && orderableItems && ((value: OrderableItemOutputDTO | OrderableItemOutputDTO[]) =>
                            (Array.isArray(value) && value.length > 0 ? <span className="text-gray-300 text-xs">= {value.reduce(
                                (acc, orderableItem) => acc + parseFloat(orderableItem.price ?? "0"),
                                0).toFixed(2)}€</span> : undefined))}
                        value={orderableItems?.filter(orderableItem => field.value.includes(orderableItem.id)) ?? []}
                    />
                )}
            />
            <Controller
                name="description"
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
                <BeButton type="submit" variant="primary" icon={ArrowDownToLine}>Speichern</BeButton>
                <BeButton type="button" onClick={handleCancel} icon={CircleX}>Abbrechen</BeButton>
            </div>
        </form>
    )};

export default DisplayItemForm;