import {cn} from "@/util";
import {Link, NavLink} from "react-router-dom";

export type StepIndicatorProps<T extends React.ElementType = "div", S extends typeof Link | typeof NavLink = typeof Link> = {
    steps: React.ReactNode[],
    stepsLink?: string[],
    currentStep?: number,
    onStepClick?: (step: number) => void,
    as?: T,
    stepAs?: S,
    className?: string,
} & React.ComponentProps<T>;

type LinkWrapperProps<S extends typeof Link | typeof NavLink = typeof Link> = {
    as: S,
    isActive?: boolean,
    className: (props: {isActive: boolean}) => string,
} & Omit<React.ComponentProps<S>, "className">;

const LinkWrapper: React.FC<LinkWrapperProps> = ({
    as,
    isActive = false,
    className,
    ...props
}) => {
    if (as === Link) {
        return <Link className={className({isActive})} {...props} />
    }
    return <NavLink className={className} {...props} />
}

const StepIndicator: React.FC<StepIndicatorProps> = ({
    steps,
    currentStep,
    stepsLink = [],
    as = "div",
    stepAs = Link,
    onStepClick,
    className,
    ...props
}) => {
    const As = as;

    return <As className={cn("flex justify-start overflow-y-hidden overflow-x-visible", className)} {...props}>
        {steps.map((step, index) =>
            <LinkWrapper
                as={stepAs}
                to={stepsLink.length > index ? stepsLink[index] : "#"}
                key={"step-" + index}
                isActive={currentStep === index}
                className={({isActive}) => cn(
                    "flex w-fit items-center justify-center py-1 pl-10 pr-3 relative",
                    "first:pl-5",
                    "rounded-md text-nowrap",
                    "text-sm text-white",
                    "bg-green-200",
                    "after:content-['_'] after:block after:w-0 after:h-0",
                    "after:absolute after:top-2/4 after:left-full after:mt-[-50px] after:-ml-3 after:z-2",
                    "after:border-t-[50px] after:border-b-[50px] after:border-l-[30px]",
                    "after:border-t-transparent after:border-b-transparent after:border-l-green-200",
                    "before:content-['_'] before:block before:w-0 before:h-0",
                    "before:absolute before:top-2/4 before:left-full before:mt-[-50px] before:-ml-2 before:z-1",
                    "before:border-t-[50px] before:border-b-[50px] before:border-l-[30px]",
                    "before:border-t-transparent before:border-b-transparent before:border-l-white",
                    {"pointer-events-none": !isActive},
                    {"bg-green-500 after:border-l-green-500": isActive}
                )}
                onClick={() => onStepClick?.(index)}
            >
                {step}
            </LinkWrapper>)}
    </As>
}

export default StepIndicator;