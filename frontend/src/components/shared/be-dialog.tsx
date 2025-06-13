import {PropsWithChildren, ReactNode} from "react";
import {CloseButton, Dialog, DialogBackdrop, DialogPanel, DialogProps} from "@headlessui/react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {IconProp} from "@fortawesome/fontawesome-svg-core";
import {ClassValue} from "clsx";
import {cn} from "@/util";

type BeDialogProps = DialogProps & PropsWithChildren<{
    icon?: IconProp
    iconClassName?: ClassValue;
    actions?: ReactNode;
    className?: ClassValue;
    clickToClose?: boolean;
}>

type DialogPanelProps = PropsWithChildren<{
    className?: ClassValue;
    actions?: ReactNode;
}>

type DialogPanelWithIconProps = DialogPanelProps & {
    icon: IconProp;
    iconClassName?: ClassValue;
}

const DialogPanelWithIcon = ({icon, iconClassName, children, actions, className}: DialogPanelWithIconProps) => {
    return (
        <DialogPanel className={cn("grid gap-6 min-w-0", actions && "grid-rows-2", className)}>
            <FontAwesomeIcon icon={icon} className={cn("w-14 text-6xl", iconClassName)}/>
            <div className="col-start-2 min-w-0 w-auto">{children}</div>
            {actions && <div className="flex min-w-0 justify-end gap-2 row-start-2 col-span-2 place-self-end">
                {actions}
            </div>}
        </DialogPanel>
    )
}

const DialogPanelWithoutIcon = ({children, actions, className}: DialogPanelProps) => {
    return (
        <DialogPanel className={cn(actions && "grid gap-6 auto-rows-min", className)}>
            <div className="row-start-1 min-w-0">{children}</div>
            {actions && <div className="flex justify-end gap-2 row-start-2 place-self-end">
                {actions}
            </div>}
        </DialogPanel>
    )
}

const BeDialog = ({icon, clickToClose, iconClassName, actions, className, children, ...props}: BeDialogProps) => {
    const defaultClassName = "max-w-xl space-y-4 bg-neutral-600 shadow-2xl rounded-xl p-10";
    return (
        <Dialog {...props}>
            <DialogBackdrop className="fixed inset-0 bg-black/50 backdrop-blur-md z-10" />
            <div className={cn("fixed inset-0 flex w-screen items-center justify-center p-4 z-11", clickToClose && "relative")}>
                <CloseButton className="absolute z-20 w-auto h-auto top-0 right-0 left-0 bottom-0 p-0 m-0" />
                {icon ?
                    <DialogPanelWithIcon
                        icon={icon}
                        iconClassName={iconClassName}
                        actions={actions}
                        className={cn(defaultClassName, className)}>
                        {children}
                    </DialogPanelWithIcon> :
                    <DialogPanelWithoutIcon
                        actions={actions}
                        className={cn(defaultClassName, className)}>
                        {children}
                    </DialogPanelWithoutIcon>
                }
            </div>
        </Dialog>
    )
}

export default BeDialog;