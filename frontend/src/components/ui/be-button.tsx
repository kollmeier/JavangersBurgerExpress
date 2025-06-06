import {Button, ButtonProps} from "@headlessui/react";
import {cn} from "@/util";
import {buttonColors, ButtonVariantsType} from "@/components/ui/index.ts";


export type BeButtonProps = ButtonProps & {
    variant?: ButtonVariantsType
};

const BeButton = ({className, variant, ...props}: BeButtonProps) => {
    return (
        <Button {...props} className={cn("btn btn-" + variant, variant ? buttonColors[variant] : buttonColors.neutral, className)} />
    )
}

export default BeButton;