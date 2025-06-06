package de.ckollmeier.burgerexpress.backend.interfaces;

public interface AdditionalInformation<T> extends BaseAdditionalInformation {
    @Override
    T value();
    @Override
    String type();
    @Override
    String displayString();
    @Override
    String shortDisplayString();
}
