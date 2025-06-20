import {cn} from "@/util";
import {buttonColors, ButtonVariantsType} from "@/components/ui/index.ts";
import React from "react";
import {Button, ButtonProps} from "@headlessui/react";


export type BeButtonProps = ButtonProps & {
    variant?: ButtonVariantsType
};

const BeButton: React.FC<BeButtonProps> = ({
  className,
  variant,
  ...props
}: BeButtonProps) => {
    return (
        <Button {...props} className={cn("btn disabled:opacity-15 disabled:pointer-events-none", variant ? buttonColors[variant] : buttonColors.neutral, className)} />
    )
}

export default BeButton;