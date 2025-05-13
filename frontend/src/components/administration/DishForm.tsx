import React, {type ChangeEvent, useEffect, useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSave} from "@fortawesome/free-solid-svg-icons/faSave";
import {faClose} from "@fortawesome/free-solid-svg-icons/faClose";
import type {DishOutputDTO} from "../../types/DishOutputDTO.ts";
import type {DishInputDTO} from "../../types/DishInputDTO.ts";

import "./DishForm.scss";

type Props = {
    dish?: DishOutputDTO;
    dishType: 'main' | 'side' | 'beverage';
    onSubmit?:  (event: React.FormEvent<HTMLFormElement>, dish: DishInputDTO, dishId?: string) => void;
    onChange?:  (event: React.ChangeEvent<HTMLInputElement>, dish: DishInputDTO) => void;
    onCancel?:  React.UIEventHandler<HTMLButtonElement | HTMLAnchorElement>;
}

const DishForm = ({ dish, dishType, onSubmit, onChange, onCancel }: Props)=> {
    const [changedDish, setChangedDish] = useState<DishInputDTO>({
        name: '',
        price: '',
        type: dishType,
        additionalInformation: {}
    });

    useEffect(() => {
        if (dish === undefined) {
            return;
        }
        setChangedDish({
            name: dish.name,
            price: dish.price,
            type: dish.type,
            additionalInformation: dish.additionalInformation
        })
    }, [dish]);

    const handleChange = (event: ChangeEvent<HTMLInputElement>)=> {
        const { name, value } = event.target;
        if (name === 'description' || name === 'size') {
            setChangedDish((prevDish) => ({
                ...prevDish,
                additionalInformation: {
                    ...prevDish.additionalInformation,
                    [name]: {
                        type: name === 'size' ? 'SIZE_IN_LITER' : 'PLAIN_TEXT',
                        value: value
                    }
                }
            }));
        } else {
            setChangedDish((prevDish) => ({
                ...prevDish,
                [name]: value
            }));
        }
        if (onChange) {
            onChange(event, changedDish);
        }
    }

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (onSubmit) {
            onSubmit(event, changedDish)
        }
    }

    const handleCancel = (event: React.MouseEvent<HTMLButtonElement | HTMLAnchorElement>) => {
        event.preventDefault();
        if (onCancel) {
            onCancel(event);
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <div className="input-widget input-name">
                <label htmlFor="name">Name</label>
                <input
                    type="text"
                    name="name"
                    value={changedDish.name}
                    onChange={handleChange}
                />
            </div>
            <div className="input-widget input-price">
                <label htmlFor="price">Preis</label>
                <input
                    type="text"
                    name="price"
                    value={changedDish.price}
                    onChange={handleChange}
                />
            </div>
            {(dishType === 'beverage') ? (
                <div className="input-widget input-size">
                    <label htmlFor="size">Inhalt in Liter</label>
                    <input
                        type="text"
                        name="size"
                        value={changedDish.additionalInformation.size?.value ?? ''}
                        onChange={handleChange}
                    />
                </div>
            ) : ("")
            }
            <div className="input-widget input-description">
                <label htmlFor="description">Beschreibung</label>
                <input
                    type="textarea"
                    name="description"
                    value={changedDish.additionalInformation.description?.value ?? ''}
                    onChange={handleChange}
                />
            </div>
            <div className="dish-actions">
                <button type="submit"><FontAwesomeIcon icon={faSave}/> Speichern</button>
                <button type="button" onClick={handleCancel}><FontAwesomeIcon icon={faClose}/> Abbrechen</button>
            </div>
        </form>
    );
}

export default DishForm;