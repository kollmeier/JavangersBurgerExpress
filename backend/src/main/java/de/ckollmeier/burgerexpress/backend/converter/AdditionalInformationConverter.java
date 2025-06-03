package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.AdditionalInformationDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.AdditionalInformation;
import de.ckollmeier.burgerexpress.backend.model.PlainTextAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.model.SizeInLiterAdditionalInformation;

import java.math.BigDecimal;
import java.util.HashMap;
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
                return new SizeInLiterAdditionalInformation(new BigDecimal(additionalInformation.value().replace(",", ".")));
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

    /**
     * Konvertiert eine Map von AdditionalInformationDTO in eine Map von AdditionalInformation,
     * unter Berücksichtigung bereits existierender AdditionalInformation.
     *
     * @param additionalInformation Die Map mit AdditionalInformationDTO-Objekten, die konvertiert werden sollen.
     * @param existingAdditionalInformation Die bereits existierenden AdditionalInformation, die beibehalten werden sollen.
     * @return Eine neue Map, die die konvertierten AdditionalInformation enthält, kombiniert mit den bestehenden.
     */
    public static Map<String, AdditionalInformation<?>> convert(final Map<String, AdditionalInformationDTO> additionalInformation, final Map<String, AdditionalInformation<?>> existingAdditionalInformation) {
        Map<String, AdditionalInformation<?>> newAdditionalInformation = new HashMap<>(existingAdditionalInformation);
        newAdditionalInformation.putAll(additionalInformation.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue() != null ? convert(entry.getValue()) : existingAdditionalInformation.get(entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        return newAdditionalInformation;
    }
}
