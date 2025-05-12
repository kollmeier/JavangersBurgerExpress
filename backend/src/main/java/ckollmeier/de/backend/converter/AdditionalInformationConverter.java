package ckollmeier.de.backend.converter;

import ckollmeier.de.backend.dto.AdditionalInformationDTO;
import ckollmeier.de.backend.interfaces.AdditionalInformation;
import ckollmeier.de.backend.model.PlainTextAdditionalInformation;
import ckollmeier.de.backend.model.SizeInLiterAdditionalInformation;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class provides utility methods for converting between
 * {@link AdditionalInformationDTO} and {@link AdditionalInformation} objects.
 */
public final class AdditionalInformationConverter {
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, because this is a utility class.
     */
    private AdditionalInformationConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts an AdditionalInformationDTO to an appropriate AdditionalInformation object.
     *
     * @param additionalInformation The DTO to convert.
     * @return An instance of AdditionalInformation.
     * @throws IllegalArgumentException If the type in the DTO is unknown.
     */
    public static AdditionalInformation<?> convert(final AdditionalInformationDTO additionalInformation) {
        switch (additionalInformation.type()) {
            case "SIZE_IN_LITER" -> {
                return new SizeInLiterAdditionalInformation(new BigDecimal(additionalInformation.value()));
            }
            case "PLAIN_TEXT" -> {
                return new PlainTextAdditionalInformation(additionalInformation.value());
            }
            default ->
                    throw new IllegalArgumentException("Unknown additional information type: " + additionalInformation.type());
        }
    }

    public static Map<String, AdditionalInformation<?>> convert(final Map<String, AdditionalInformationDTO> additionalInformation) {
        return additionalInformation.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), convert(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
