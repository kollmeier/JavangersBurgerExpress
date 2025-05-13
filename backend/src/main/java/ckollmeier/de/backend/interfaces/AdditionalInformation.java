package ckollmeier.de.backend.interfaces;

public interface AdditionalInformation<T> {
    T value();
    String type();
    String displayString();
    String shortDisplayString();
}
