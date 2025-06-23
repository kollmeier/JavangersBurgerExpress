import React, {PropsWithChildren, useEffect, useRef, useState} from "react";
import {debounce} from "lodash";
import {cn} from "@/util";
import {CircleChevronDown, CircleChevronUp} from "lucide-react";

type ScrollableWithIndicatorsProps<T extends React.HTMLElementType = "div"> = PropsWithChildren<{
    as?: T;
    pre?: React.ReactElement;
    post?: React.ReactElement;
}> & React.ComponentPropsWithRef<T>

const ScrollableWithIndicators: React.FC<ScrollableWithIndicatorsProps> = ({as, pre, post, children, className, ...props}) => {
    const Element = as ?? "div";

    const [canScrollUp, setCanScrollUp] = useState(false);
    const [canScrollDown, setCanScrollDown] = useState(false);
    const containerRef = useRef<HTMLDivElement | null>(null);

    const checkForScrollPosition = () => {
        if (!containerRef.current) {
            return;
        }
        const { scrollTop, scrollHeight, clientHeight } = containerRef.current;
        const pos = Math.ceil(scrollTop);

        setCanScrollUp(pos > 0);
        setCanScrollDown(pos !== scrollHeight - clientHeight);
    };

    const debounceCheckForScrollPosition = debounce(checkForScrollPosition, 50);

    const scrollContainerBy = (distance: number) => {
        containerRef.current?.scrollBy({ top: distance, behavior: "smooth" });
    };

    const controls = (
        <div className="item-controls">


        </div>
    );

    useEffect(() => {
        checkForScrollPosition();

        const ref = containerRef.current;

        ref?.addEventListener(
            "scroll",
            debounceCheckForScrollPosition,
        );

        return () => {
            ref?.removeEventListener(
                "scroll",
                debounceCheckForScrollPosition,
            );
        };
    }, [debounceCheckForScrollPosition]);

    return <>
        <button
            type="button"
            className={cn(
                "top-0 relative z-1 w-full h-6 text-center transform-[opacity,height] duration-300",
                {"before:pointer-events-none before:bg-linear-to-t before:from-transparent before:to-white before:content-['_'] before:absolute before:top-9 before:left-0 before:w-full before:h-20": !pre && canScrollUp},
                canScrollUp ? "opacity-100" : "opacity-0 h-0")}
            disabled={!canScrollUp}
            onClick={() => {
                scrollContainerBy(-200);
            }}
        >
            <CircleChevronUp className="!h-full text-gray-500" />
        </button>
        {pre && <div className={cn(
            "relative",
            {"before:pointer-events-none before:bg-linear-to-b before:from-transparent before:to-white before:content-['_'] before:absolute before:top-13 before:left-0 before:w-full before:h-20": canScrollUp},
        )}>
            {pre}
        </div>}
        <Element ref={containerRef} className={cn(
            "overflow-y-scroll overscroll-contain",
            className)} {...props}>
            {children}
            {controls}
        </Element>
        {post && <div className={cn(
            "relative",
            {"before:pointer-events-none before:bg-linear-to-b before:from-transparent before:to-white before:content-['_'] before:absolute before:-top-23 before:left-0 before:w-full before:h-20": canScrollDown},
        )}>
            {post}
        </div>}
        <button
            type="button"
            className={cn(
                "bottom-0 relative z-1 w-full h-6 text-center transform-[opacity,height] duration-300",
                {"before:pointer-events-none before:bg-linear-to-b before:from-transparent before:to-white before:content-['_'] before:absolute before:bottom-9 before:left-0 before:w-full before:h-20": !post && canScrollDown},
                canScrollDown ? "opacity-100" : "opacity-0 h-0")}
            disabled={!canScrollDown}
            onClick={() => {
                scrollContainerBy(200);
            }}
        >
            <CircleChevronDown className="!h-6 text-gray-500" />
        </button>
    </>
}

export default ScrollableWithIndicators;