package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.interfaces.AdditionalInformation;
import de.ckollmeier.burgerexpress.backend.types.AdditionalInformationType;

import java.io.Serializable;

public record PlainTextAdditionalInformation(
        String value
) implements AdditionalInformation<String>, Serializable {
    @Override
    public String displayString() {
        return value;
    }

    @Override
    public String shortDisplayString() {
        return value;
    }

    @Override
    public String type() {
        return AdditionalInformationType.PLAIN_TEXT.toString();
    }
}
