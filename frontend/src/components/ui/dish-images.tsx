import {cn} from "@/util";

export type DishImagesProps = {
    mainImages: string[]
    sideImages: string[]
    beverageImages: string[]
    className?: string;
}

const DishImages = ({mainImages, sideImages, beverageImages, className}: DishImagesProps) => {
    return (<div className={cn("dish-images", className)}>
        <div className="dish-images_main">
            {mainImages.map(url => <img key={url} src={url + "?size=180"} alt="Hauptgericht" />)}
            {mainImages.length === 0 && sideImages.map(url => <img key={url} src={url + "?size=180"} alt="Beilage" />)}
            {mainImages.length === 0 && beverageImages.map(url => <img key={url} src={url + "?size=180"} alt="Getränk" />)}
        </div>
        <div className="dish-images_side">
            {mainImages.length > 0 && sideImages.map(url => <img key={url} src={url + "?size=190"} alt="Beilage" />)}
        </div>
        <div className="dish-images_beverage">
            {mainImages.length > 0 && beverageImages.map(url => <img key={url} src={url + "?size=160"} alt="Getränk" />)}
        </div>
    </div>)
}

export default DishImages;