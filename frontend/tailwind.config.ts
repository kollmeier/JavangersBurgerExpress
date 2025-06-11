import {TailwindConfig} from "tailwindcss/tailwind-config";

export default {
    content: [
        "./src/**/*.{html,js,ts,jsx,tsx,css}",
    ],
    theme: {
        extend: {
            gridTemplateColumns: {
                card: '[first] var(--spacing-circle-column-md) [middle] 1fr [side] var(--spacing-circle-lg) [last] var(--spacing-circle-column-lg)'
            },
            gridTemplateRows: {
                card: '[head] minmax(var(--spacing-circle-column-md), min-content) [content] minmax(calc(var(--spacing) * 8), min-content) [foot] auto [actions] min-content'
            },
            gridRowStart: {
                head: 'head',
                content: 'content',
                foot: 'foot',
                actions: 'actions',
            },
            gridRowEnd: {
                head: 'head',
                content: 'content',
                foot: 'foot',
                actions: 'actions',
            },
            gridRow: {
                head: 'head',
                head_foot: 'head / foot',
                content: 'content',
                content_foot: 'content / foot',
                content_actions: 'content / actions',
                foot: 'foot',
                foot_actions: 'foot / actions',
                foot_end: 'foot / -1',
                actions: 'actions',
            },
            gridColumnStart: {
                first: 'first',
                middle: 'middle',
                side: 'side',
                last: 'last',
            },
            gridColumnEnd: {
                first: 'first',
                middle: 'middle',
                side: 'side',
                last: 'last'
            },
            gridColumn: {
                first: 'first',
                first_middle: 'first / middle',
                first_side: 'first / side',
                first_last: 'first / last',
                first_end: 'first / -1',
                middle: 'middle',
                middle_side: 'middle / side',
                middle_last: 'middle / last',
                middle_end: 'middle / -1',
                side: 'side',
                side_last: 'side / last',
                side_end: 'side / -1',
                last: 'last'
            }
        }
    }
} satisfies TailwindConfig;
