import {
    Combobox,
    ComboboxInput,
    ComboboxOption,
    ComboboxOptions,
    Description,
    Field,
    Label
} from '@headlessui/react'

import {ChangeEvent, forwardRef, ReactElement, ReactNode, Ref, useEffect, useState} from "react";
import {cn} from "@/util";
import {colorMapCards, colorVariants} from "@/data";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faRemove} from "@fortawesome/free-solid-svg-icons";

interface ValueInterface {
    id: string
    name?: string
    type?: string
}

export type ComboboxWithLabelProps<T extends ValueInterface> = {
    label: string
    name: string
    value?: T | T[]
    displayValue?: (item:T) => string
    options: T[]
    optionElement?: (option:T) => ReactNode
    summaryElement?: (value:T | T[]) => ReactNode
    onChange: (value:T | T[]) => void
    onClose?: () => void
    onBlur?: (e: ChangeEvent<HTMLInputElement>) => void
    placeholder?: string
    multiple?: boolean
    required?: boolean
    className?: string
    fieldClassName?: string
    error?: string
    disabled?: boolean
}

const ComboboxWithLabel = forwardRef(
    <T extends ValueInterface>(
        {
            label,
            name,
            value,
            displayValue = (value) => value?.name ?? '',
            options,
            optionElement = (option) => option.name,
            summaryElement,
            onChange,
            onClose,
            onBlur,
            placeholder,
            multiple = false,
            required = false,
            className,
            fieldClassName,
            error,
            disabled = false,
        }: ComboboxWithLabelProps<T>,
        ref: React.Ref<HTMLInputElement>
    ) => {
        const [filteredOptions, setFilteredOptions] = useState<T[]>([]);

        const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
            const input = event.target.value;
            if (input === '') {
                setFilteredOptions(options);
                return;
            }
            setFilteredOptions(prev => prev.filter(option => option.name?.toLowerCase().includes(input.toLowerCase())));
        }

        useEffect(() => {
            setFilteredOptions(options);
        }, [options]);

        return (
            <Field
                className={cn("flex flex-col flex-1 gap-1 rounded-lg", fieldClassName)}>
                <Label className="text-sm font-medium text-gray-700">
                    {label}
                    {required && ' *'}
                </Label>
                <Combobox
                    name={name}
                    value={value}
                    multiple={multiple}
                    disabled={disabled}
                    onChange={onChange}
                    onClose={onClose}
                    immediate={true}
                >
                    <div
                        className={cn("flex flex-wrap flex-row gap-0.5 items-start justify-start input relative", error && "input--error", disabled && "input--disabled", className)}
                    >
                        {value && multiple && Array.isArray(value) && value.map(v => (
                            <span key={name+ "-option-pill-" + v.id} className={cn("pill", colorVariants[v.type ?? ""] ?? colorVariants[colorMapCards[v.type ?? ""] ?? "neutral"]?.light)}>
                                <button onClick={event => {
                                    event.stopPropagation();
                                    onChange(value.filter(f => f !== v))
                                }}><FontAwesomeIcon icon={faRemove}/></button>
                                {displayValue(v)}
                            </span>
                        ))}
                        {summaryElement && value && (summaryElement(value))}
                        <ComboboxInput
                            ref={ref}
                            aria-label={label}
                            displayValue={displayValue}
                            placeholder={placeholder}
                            onBlur={onBlur}
                            onChange={handleInputChange}
                            className="focus:outline-none w-full"
                        />
                    </div>
                    <ComboboxOptions anchor="bottom start" className="[--anchor-gap:8px] border w-[calc(var(--input-width)+24px)] input px-0! py-0! empty:invisible -ml-3 -mr-3">
                        {filteredOptions.map((value) => (
                            <ComboboxOption key={value.id} value={value} className="data-focus:bg-blue-200 data-selected:bg-gray-200 border-gray-100 data-selected:border-b data-selected:last:border-none text-gray-700 py-1 px-2">
                                {optionElement(value)}
                            </ComboboxOption>
                        ))}
                    </ComboboxOptions>
                </Combobox>
                {error && (
                    <Description className="text-sm text-red-600" role="alert">
                        {error}
                    </Description>
                )}
            </Field>
        );
    }
) as <T extends ValueInterface>(
    props: ComboboxWithLabelProps<T> & { ref?: Ref<HTMLInputElement> }
) => ReactElement | null;


export default ComboboxWithLabel;
