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
                className={cn("flex flex-col flex-1 gap-1 rounded-lg relative", fieldClassName)}>
                <Combobox
                    name={name}
                    value={value}
                    multiple={multiple}
                    disabled={disabled}
                    onChange={onChange}
                    onClose={onClose}
                    immediate={true}
                >
                    <div>
                        {value && multiple && Array.isArray(value) &&
                            <div className="px-2 pt-2.5 z-1 relative bg-transparent">
                                {value.map(v => (
                                    <span key={name + "-option-pill-" + v.id}
                                          className={cn("pill", colorVariants[v.type ?? ""] ?? colorVariants[colorMapCards[v.type ?? ""] ?? "neutral"]?.light)}>
                                    <button onClick={event => {
                                        event.stopPropagation();
                                        onChange(value.filter(f => f !== v))
                                    }}><FontAwesomeIcon icon={faRemove}/></button>
                                        {displayValue(v)}
                                </span>
                                ))}
                                {summaryElement && value && (summaryElement(value))}
                            </div>}
                        <ComboboxInput
                            ref={ref}
                            aria-label={label}
                            displayValue={displayValue}
                            placeholder={placeholder ?? " "}
                            onBlur={onBlur}
                            onChange={handleInputChange}
                            className="pt-3 pb-0.5 px-2 w-full text-sm text-gray-900 focus:outline-none peer"
                        />
                        <Label
                            className={cn("absolute z-2 pointer-events-none px-1.5 pt-0 text-sm text-gray-500 duration-300 transform -translate-y-4 scale-75 top-3.5 origin-[0] rounded-sm",
                                "peer-focus:text-blue-600 peer-placeholder-shown:scale-100 peer-placeholder-shown:-translate-y-1/2 peer-placeholder-shown:top-5",
                                "peer-focus:top-3.5 peer-focus:scale-75 peer-focus:-translate-y-4 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto start-1",
                                (value && multiple && Array.isArray(value) && value.length > 0) && "top-3.5! scale-75! -translate-y-4! rtl:translate-x-1/4! rtl:left-auto!"
                            )}>
                            {label}
                            {required && '*'}
                        </Label>
                        <div
                            className={cn(
                                "absolute z-0 top-0 pointer-events-none w-full h-full",
                                "bg-white",
                                "block rounded-lg border-1 border-gray-300 appearance-none",
                                "peer-focus:outline-none peer-focus:ring-0 peer-focus:border-blue-600",
                                error && "input--error",
                                disabled && "input--disabled",
                                className
                            )}
                        />
                        <ComboboxOptions anchor="bottom start" className="[--anchor-gap:1px] border w-[--input-width] rounded-lg bg-white border-blue-600 px-0 py-0 empty:invisible z-11">
                            {filteredOptions.map((value) => (
                                <ComboboxOption key={value.id} value={value} className="data-focus:bg-blue-200 data-selected:bg-gray-200 border-gray-100 data-selected:border-b data-selected:last:border-none text-gray-700 py-1 px-2">
                                    {optionElement(value)}
                                </ComboboxOption>
                            ))}
                        </ComboboxOptions>
                    </div>
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
