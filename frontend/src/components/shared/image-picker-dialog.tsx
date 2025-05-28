import React, {useState, useRef, useEffect} from 'react';
import BeDialog from "@/components/shared/be-dialog.tsx";
import BeButton from "@/components/ui/be-button.tsx";
import {
    Field, Input, Label,
    Listbox,
    ListboxOption,
    ListboxOptions,
} from "@headlessui/react";
import {cn, useImages} from "@/util";

interface ImagePickerDialogProps {
    open: boolean;
    values?: string[];
    onClose: () => void;
    onImagesSelected: (files: File[], existingImageUrls: string[]) => void;
    multiple?: boolean;
}

const ImagePickerDialog: React.FC<ImagePickerDialogProps> = ({
    open,
    onClose,
    onImagesSelected,
    values,
    multiple = true,
}) => {
    const [previews, setPreviews] = useState<string[]>([]);
    const [files, setFiles] = useState<File[]>([]);
    const [selectedFiles, setSelectedFiles] = useState<string[]>([]);
    const inputRef = useRef<HTMLInputElement>(null);
    const uploadedImages = useImages();

    useEffect(() => {
        setSelectedFiles(values ?? []);
    }, [values])

    function withoutExisting(existing: string[], incoming: string[]) {
        return incoming.filter(url => !existing.includes(url));
    }

    const handleFiles = (incoming: FileList) => {
        if (incoming.length === 0) return;
        if (!multiple) {
            setSelectedFiles([]);
        }
        const files = Array.from(incoming).filter(file => !previews.includes(URL.createObjectURL(file)));
        const urls = files.map(file => URL.createObjectURL(file));
        setFiles(files);
        setPreviews(prev => [...prev, ...withoutExisting(prev, urls)]);
        setSelectedFiles(prev => [...prev, ...withoutExisting(prev, urls)]);
    };

    const handleSelectSingle = (url: string) => {
        console.log(url);
        setSelectedFiles([url]);
    }

    const handleSelectMultiple = (urls: string[]) => {
        setSelectedFiles(urls);
    }

    const handleConfirm = () => {
        onImagesSelected(
            files.filter(file => !selectedFiles.includes(URL.createObjectURL(file))),
            selectedFiles.filter(url => !files.map(file => URL.createObjectURL(file)).includes(url))
        );
        onClose();
        setPreviews([]);
        setFiles([]);
        setSelectedFiles([]);
    };

    const handleClose = () => {
        onClose();
        setPreviews([]);
        setFiles([]);
        setSelectedFiles([]);
    }

    return (
        <BeDialog
            open={open}
            onClose={handleClose}
            className="w-xl"
            actions={<>
                <BeButton variant="neutral" onClick={onClose}>Abbrechen</BeButton>
                <BeButton
                    variant="primary"
                    onClick={handleConfirm}
                    disabled={selectedFiles.length === 0}
                >
                    Übernehmen
                </BeButton>
            </>}
        >
            <div className="flex items-center justify w-full">
                <Field className="w-full">
                    <Label
                        onClick={() => inputRef.current?.click()}
                        className="flex flex-col items-center justify-center w-full h-64 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer bg-gray-50 dark:hover:bg-gray-800 dark:bg-gray-700 hover:bg-gray-100 dark:border-gray-600 dark:hover:border-gray-500">
                        <div className="flex flex-col items-center justify-center pt-5 pb-6">
                            <svg className="w-8 h-8 mb-4 text-gray-500 dark:text-gray-400" aria-hidden="true"
                                 xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 16">
                                <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                                      d="M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2"/>
                            </svg>
                            <p className="mb-2 text-sm text-gray-500 dark:text-gray-400"><span className="font-semibold">Klicken zum Hochladen</span> oder
                                hierhin ziehen</p>
                            <p className="text-xs text-gray-500 dark:text-gray-400">SVG, PNG, JPG oder GIF</p>
                        </div>
                    </Label>
                    <Input ref={inputRef} id="dropzone-file" type="file" className="hidden"
                           multiple={multiple} accept="image/png, image/jpeg, image/gif, image/svg+xml"
                           onChange={(e) => e.target.files && handleFiles(e.target.files)}/>
                </Field>
            </div>

            <Listbox value={(multiple ? selectedFiles : selectedFiles[0]) ?? ''} horizontal
                     onChange={multiple ? handleSelectMultiple : handleSelectSingle}
                     multiple={multiple}>
                <ListboxOptions autoFocus static className="mt-20 overflow-x-scroll overflow-y-hidden border-2 border-gray-400 bg-gray-50 rounded-lg shadow-lg">
                    <div className="flex flex-row gap-2 py-2 px-4 h-fit w-fit">
                        {previews.map((url) => (
                            <ListboxOption key={"preview-" + url}
                                           value={url}
                                           className={() => cn(
                                               "flex place-items-center relative w-20 h-20 data-focus:bg-gray-200 hover:bg-gray-100 ring-2 ring-transparent",
                                               selectedFiles.find(f => f === url) && "ring-primary"
                                           )}>
                                <img src={url} alt="Vorschau" className="hover:scale-110 transition-transform hover:z-10"/>
                            </ListboxOption>
                        ))}
                        {uploadedImages?.map((image) => (
                            <ListboxOption key={"uploaded-" + image.id}
                                           value={image.uri}
                                           className={() => cn(
                                               "flex place-items-center relative w-20 h-20 data-focus:bg-gray-200 hover:bg-gray-100 ring-2 ring-transparent",
                                               selectedFiles.find(f => f === image.uri) && "ring-primary"
                                           )}>
                                <img src={image.uri} alt="Vorschau" className="hover:scale-110 transition-transform hover:z-10"/>
                            </ListboxOption>
                        ))}
                    </div>
                </ListboxOptions>
            </Listbox>
        </BeDialog>
    );
};

export default ImagePickerDialog;