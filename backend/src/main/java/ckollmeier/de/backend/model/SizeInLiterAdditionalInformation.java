package ckollmeier.de.backend.model;

import ckollmeier.de.backend.interfaces.AdditionalInformation;
import ckollmeier.de.backend.types.AdditionalInformationType;

import java.math.BigDecimal;

public record SizeInLiterAdditionalInformation(
        BigDecimal value
) implements AdditionalInformation<BigDecimal> {
    @Override
    public String displayString() {
        return String.format("Inhalt: %.1f Liter", value);
    }

    @Override
    public String shortDisplayString() {
        return String.format("%.1fl", value);
    }

    @Override
    public String type() {
        return AdditionalInformationType.SIZE_IN_LITER.toString();
    }
}
