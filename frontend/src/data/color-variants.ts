export type ColorVariantsType = keyof typeof colorVariants;

export type ColorVariantStyle = keyof ColorVariant;

export type ColorVariant = {
    light: string,
    normal: string,
    dark: string,
}

export const colorVariants: Record<string, ColorVariant> = {
    "red": {light: "bg-red-100 text-red-900", normal: "bg-red-50 text-red-900", dark: "bg-red-700 text-red-100"},
    "green": {light: "bg-green-100 text-green-900", normal: "bg-green-50 text-green-900", dark: "bg-green-700 text-green-100"},
    "blue": {light: "bg-blue-100 text-blue-900", normal: "bg-blue-50 text-blue-900", dark: "bg-blue-700 text-blue-100"},
    "yellow": {light: "bg-yellow-100 text-yellow-900", normal: "bg-yellow-50 text-yellow-900", dark: "bg-yellow-700 text-yellow-100"},
    "purple": {light: "bg-purple-100 text-purple-900", normal: "bg-purple-50 text-purple-900", dark: "bg-purple-700 text-purple-100"},
    "pink": {light: "bg-pink-100 text-pink-900", normal: "bg-pink-50 text-pink-900", dark: "bg-pink-700 text-pink-100"},
    "gray": {light: "bg-gray-100 text-gray-900", normal: "bg-gray-50 text-gray-900", dark: "bg-gray-700 text-gray-100"},
    "orange": {light: "bg-orange-100 text-orange-900", normal: "bg-orange-50 text-orange-900", dark: "bg-orange-700 text-orange-100"},
    "teal": {light: "bg-teal-100 text-teal-900", normal: "bg-teal-50 text-teal-900", dark: "bg-teal-700 text-teal-100"},
    "indigo": {light: "bg-indigo-100 text-indigo-900", normal: "bg-indigo-50 text-indigo-900", dark: "bg-indigo-700 text-indigo-100"},
    "amber": {light: "bg-amber-100 text-amber-900", normal: "bg-amber-50 text-amber-900", dark: "bg-amber-700 text-amber-100"},
    "lime": {light: "bg-lime-100 text-lime-900", normal: "bg-lime-50 text-lime-900", dark: "bg-lime-700 text-lime-100"},
    "cyan": {light: "bg-cyan-100 text-cyan-900", normal: "bg-cyan-50 text-cyan-900", dark: "bg-cyan-700 text-cyan-100"},
    "sky": {light: "bg-sky-100 text-sky-900", normal: "bg-sky-50 text-sky-900", dark: "bg-sky-700 text-sky-100"},
    "primary": {light: "bg-primary-100 text-primary-900", normal: "bg-primary text-primary-text", dark: "bg-primary-700 text-primary-100"},
    "secondary": {light: "bg-secondary-100 text-secondary-900", normal: "bg-secondary text-secondary-text", dark: "bg-secondary-700 text-secondary-100"},
    "danger": {light: "bg-danger-100 text-danger-900", normal: "bg-danger text-danger-text", dark: "bg-danger-700 text-danger-100"},
    "neutral": {light: "bg-neutral-100 text-neutral-900", normal: "bg-neutral text-neutral-text", dark: "bg-neutral-700 text-neutral-50"},
}

