package ckollmeier.de.backend.model;

import ckollmeier.de.backend.interfaces.AdditionalInformation;
import ckollmeier.de.backend.types.AdditionalInformationType;

public record PlainTextAdditionalInformation(
        String value
) implements AdditionalInformation<String> {
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
