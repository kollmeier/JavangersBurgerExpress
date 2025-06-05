package de.ckollmeier.burgerexpress.backend.interfaces;

/**
 * This interface represents a base for additional information without generic type parameters.
 * It extends BaseAdditionalInformation to provide a non-generic interface for use in return types,
 * which complies with SonarQube rule java:S1452 (Generic wildcard types should not be used in return parameters).
 */
public interface BaseAdditionalInformation {
    /**
     * Returns the value of this additional information.
     *
     * @return the value of this additional information
     */
    Object value();

    /**
     * Returns the type of this additional information.
     *
     * @return the type of this additional information
     */
    String type();

    /**
     * Returns a display string for this additional information.
     *
     * @return a display string for this additional information
     */
    String displayString();

    /**
     * Returns a short display string for this additional information.
     *
     * @return a short display string for this additional information
     */
    String shortDisplayString();
}