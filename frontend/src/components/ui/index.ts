import {colorVariants} from "@/data";

export type ButtonVariantsType = keyof typeof buttonColors;

export type ButtonVariants = Record<ButtonVariantsType, string>;

export const buttonColors: Record<string, string> = {
    primary: colorVariants.primary.normal,
    secondary: colorVariants.secondary.normal,
    danger: colorVariants.danger.normal,
    success: colorVariants.green.normal,
    warning: colorVariants.yellow.normal,
    info: colorVariants.indigo.normal,
    neutral: colorVariants.sky.normal,
    dark: colorVariants.sky.dark,
    light: colorVariants.sky.light,
}

export const buttonVariants: ButtonVariants = {
}

