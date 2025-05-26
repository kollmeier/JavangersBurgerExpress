import {Button, ButtonProps} from "@headlessui/react";
import {cn} from "@/util";
import {buttonColors, ButtonVariantsType} from "@/components/ui/index.ts";


export type BeButtonProps = ButtonProps & {
    variant?: ButtonVariantsType
};

const BeButton = ({className, variant, ...props}: BeButtonProps) => {
    return (
        <Button {...props} className={cn("rounded-2xl h-2xl px-4 py-1 shadow-xs hover:shadow-sm", variant ? buttonColors[variant] : buttonColors.neutral, className)} />
    )
}

export default BeButton;