import DisplayCategoryForm, {
    DisplayCategoryFormProps
} from "@/features/manager/display-categories/components/display-category-form.tsx";

export type DisplayCategoryAddProps = DisplayCategoryFormProps;

const DisplayCategoryAdd = (props: DisplayCategoryAddProps) => {
    return (
        <DisplayCategoryForm {...props} />
    );
}

export default DisplayCategoryAdd;