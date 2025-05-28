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
            <Field className={cn("flex flex-col flex-1 gap-1 rounded-lg", fieldClassName)}>
                <Label className="text-sm font-medium text-gray-700">
                    {label}
                    {required && ' *'}
                </Label>
                {type === "textarea" ? (
                    <Textarea
                        id={name}
                        name={name}
                        value={value}
                        onChange={onChange}
                        onBlur={onBlur}
                        onClick={onClick}
                        onKeyDown={onKeyDown}
                        placeholder={placeholder}
                        required={required}
                        disabled={disabled}
                        ref={ref}
                        className={cn(
                            "rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 bg-white",
                            error ? "border-red-500 focus:ring-red-500" : "border-secondary focus:ring-blue-500",
                            disabled && "bg-primary cursor-not-allowed",
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
                    placeholder={placeholder}
                    required={required}
                    disabled={disabled}
                    ref={ref}
                    step={step}
                    min={min}
                    max={max}
                    inputMode={showNumericHints ? decimalInputMode : undefined}
                    pattern={showNumericHints ? decimalPattern : undefined}
                    className={cn(
                        "rounded-lg border px-3 py-2 text-sm focus:outline-none focus:ring-2 bg-white",
                        error ? "border-red-500 focus:ring-red-500" : "border-secondary focus:ring-blue-500",
                        disabled && "bg-primary cursor-not-allowed",
                        className
                    )}
                />}{children}
                {error && (
                    <Description className="text-sm text-red-600" role="alert">
                        {error}
                    </Description>
                )}
            </Field>
        );
    }
);

export default InputWithLabel;
