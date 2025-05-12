package ckollmeier.de.backend.converter;

import ckollmeier.de.backend.dto.AdditionalInformationDTO;
import ckollmeier.de.backend.interfaces.AdditionalInformation;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class provides utility methods for converting between
 * {@link AdditionalInformationDTO} and {@link AdditionalInformation} objects.
 */
public final class AdditionalInformationDTOConverter {
    private AdditionalInformationDTOConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts an instance of {@link AdditionalInformation} to an {@link AdditionalInformationDTO}.
     *
     * @param additionalInformation the instance of {@link AdditionalInformation} to be converted
     *                              containing type, value, display string, and short display string.
     * @return the converted {@link AdditionalInformationDTO} with the corresponding type, value,
     *         display string, and short display string extracted from the provided {@link AdditionalInformation}.
     */
    public static AdditionalInformationDTO convert(final AdditionalInformation<?> additionalInformation) {
        return new AdditionalInformationDTO(additionalInformation.type(),
                additionalInformation.value().toString(),
                additionalInformation.displayString(),
                additionalInformation.shortDisplayString());
    }

    /**
     * Converts a map of {@link AdditionalInformation} objects to a map of {@link AdditionalInformationDTO} objects.
     *
     * @param additionalInformation the map where the keys are {@link String} and the values are {@link AdditionalInformation}
     *                              instances to be converted.
     * @return a {@link Map} with the same keys as the input, but the values are converted to {@link AdditionalInformationDTO}.
     */
    public static Map<String, AdditionalInformationDTO> convert(final Map<String, ? extends AdditionalInformation<?>> additionalInformation) {
        return additionalInformation.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), convert(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
