import {Description, Field, Input, Label, Textarea} from '@headlessui/react'

import {ChangeEvent, forwardRef, KeyboardEvent, MouseEvent, PropsWithChildren} from "react";
import {cn} from "@/util";

export type InputWithLabelProps = PropsWithChildren<{
    label: string
    name: string
    value?: string | number
    onChange: (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void
    onBlur?: (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void
    onClick?: (e: MouseEvent<HTMLInputElement | HTMLTextAreaElement>) => void
    onKeyDown?: (e: KeyboardEvent<HTMLInputElement | HTMLTextAreaElement>) => void
    type?: string
    placeholder?: string
    required?: boolean
    className?: string
    fieldClassName?: string
    error?: string
    disabled?: boolean
    step?: string;
    min?: number;
    max?: number;
    inputMode?: "text" | "decimal" | "search" | "none" | "email" | "tel" | "url" | "numeric";
    /** HTML `pattern` attribute "[0-9]*[\\.,]?[0-9]{0,2}" */
    pattern?: string;
}>

const InputWithLabel = forwardRef<HTMLInputElement, InputWithLabelProps>(
    (
        {
            label,
            name,
            value,
            onChange,
            onBlur,
            onClick,
            onKeyDown,
            type = "text",
            placeholder,
            required,
            className,
            fieldClassName,
            error,
            disabled = false,
            step,
            min,
            max,
            inputMode,
            pattern,
            children,
        },
        ref
    ) => {
        const decimalInputMode = inputMode ?? 'decimal';
        const decimalPattern = pattern ?? '[0-9]*[\\.,]?[0-9]{0,2}';
        const showNumericHints = type === "number" || (inputMode ?? pattern);
        return (
            <Field className={fieldClassName}>
                <div className="relative h-[inherit]">
                    {type === "textarea" ? (
                            <Textarea
                                id={name}
                                name={name}
                                value={value}
                                onChange={onChange}
                                onBlur={onBlur}
                                onClick={onClick}
                                onKeyDown={onKeyDown}
                                placeholder={placeholder ?? " "}
                                required={required}
                                disabled={disabled}
                                ref={ref}
                                className={cn(
                                    "bg-white",
                                    "z-1 block pt-3 pb-0.5 px-2 w-full text-sm text-gray-900 rounded-lg border-1 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer",
                                    "placeholder:invisible focus:placeholder:visible",
                                    error && "input--error",
                                    disabled && "input--disabled",
                                    className
                                )}
                            />) :
                        <Input
                            id={name}
                            name={name}
                            type={type}
                            value={value}
                            onChange={onChange}
                            onBlur={onBlur}
                            onClick={onClick}
                            onKeyDown={onKeyDown}
                            placeholder={placeholder ?? " "}
                            required={required}
                            disabled={disabled}
                            ref={ref}
                            step={step}
                            min={min}
                            max={max}
                            inputMode={showNumericHints ? decimalInputMode : undefined}
                            pattern={showNumericHints ? decimalPattern : undefined}
                            className={cn(
                                "bg-white",
                                "z-1 block pt-3 pb-0.5 px-2 w-full text-sm text-gray-900 rounded-lg border-1 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer",
                                "placeholder:invisible focus:placeholder:visible",
                                error && "input--error",
                                disabled && "input--disabled",
                                className
                            )}
                        />}{children}
                    <Label
                        className="absolute pointer-events-none px-1.5 pt-0 text-sm text-gray-500 duration-300 transform -translate-y-4 scale-75 top-3.5 origin-[0] rounded-sm peer-focus:text-blue-600  peer-placeholder-shown:scale-100 peer-placeholder-shown:-translate-y-1/2 peer-placeholder-shown:top-5 peer-focus:top-3.5 peer-focus:scale-75 peer-focus:-translate-y-4 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto start-1">
                        {label}
                        {required && '*'}
                    </Label>
                    {error && (
                        <Description className="text-sm text-red-600" role="alert">
                            {error}
                        </Description>
                    )}
                </div>
            </Field>
        );
    }
);

export default InputWithLabel;
