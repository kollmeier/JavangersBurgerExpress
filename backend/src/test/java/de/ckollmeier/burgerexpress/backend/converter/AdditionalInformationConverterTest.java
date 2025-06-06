package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.AdditionalInformationDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.BaseAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.model.PlainTextAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.model.SizeInLiterAdditionalInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class AdditionalInformationConverterTest {

    @Test
    @DisplayName("convert SIZE_IN_LITER DTO to SizeInLiterAdditionalInformation")
    void convert_sizeInLiterDTO_returnsSizeInLiterAdditionalInformation() {
        AdditionalInformationDTO dto = new AdditionalInformationDTO(
                "SIZE_IN_LITER",
                "1.5",
                null, // irrelevant for Conversion
                null // irrelevant for Conversion
        );

        BaseAdditionalInformation result = AdditionalInformationConverter.convert(dto);

        assertThat(result).isInstanceOf(SizeInLiterAdditionalInformation.class);
        assertThat(((SizeInLiterAdditionalInformation) result).value()).isEqualByComparingTo(new BigDecimal("1.5"));
    }

    @Test
    @DisplayName("convert PLAIN_TEXT DTO to PlainTextAdditionalInformation")
    void convert_plainTextDTO_returnsPlainTextAdditionalInformation() {
        AdditionalInformationDTO dto = new AdditionalInformationDTO(
                "PLAIN_TEXT",
                "Vegetarisch",
                null, // irrelevant for Conversion
                null // irrelevant for Conversion
        );

        BaseAdditionalInformation result = AdditionalInformationConverter.convert(dto);

        assertThat(result).isInstanceOf(PlainTextAdditionalInformation.class);
        assertThat(((PlainTextAdditionalInformation) result).value()).isEqualTo("Vegetarisch");
    }

    @Test
    @DisplayName("convert unknown type throws exception")
    void convert_unknownType_throws() {
        AdditionalInformationDTO dto = new AdditionalInformationDTO(
                "UNKNOWN_TYPE",
                "foo",
                null,  // irrelevant for Conversion
                null  // irrelevant for Conversion
        );

        assertThatThrownBy(() -> AdditionalInformationConverter.convert(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown additional information type: UNKNOWN_TYPE");
    }

    @Test
    @DisplayName("convert Map of DTOs to Map of AdditionalInformation")
    void convert_map_of_dtos() {
        Map<String, AdditionalInformationDTO> dtoMap = new HashMap<>();
        dtoMap.put("info1", new AdditionalInformationDTO("PLAIN_TEXT", "Glutenfrei", null, null));
        dtoMap.put("vol", new AdditionalInformationDTO("SIZE_IN_LITER", "0.33", null, null));

        Map<String, BaseAdditionalInformation> result = AdditionalInformationConverter.convert(dtoMap);

        assertThat(result)
                .containsOnlyKeys("info1", "vol")
                .extractingByKey("info1").isInstanceOf(PlainTextAdditionalInformation.class);
        assertThat(result.get("vol")).isInstanceOf(SizeInLiterAdditionalInformation.class);
        assertThat(((SizeInLiterAdditionalInformation) result.get("vol")).value())
                .isEqualByComparingTo(new BigDecimal("0.33"));
    }

    @Test
    @DisplayName("utility class constructor is private and throws")
    void utilityClass_constructor_throwsException() {
        assertThatThrownBy(() -> {
            // via reflection, da Konstruktor privat ist
            var ctor = AdditionalInformationConverter.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            ctor.newInstance();
        })
        .isInstanceOf(java.lang.reflect.InvocationTargetException.class)
        .hasCauseInstanceOf(UnsupportedOperationException.class)
        .hasRootCauseMessage("This is a utility class and cannot be instantiated");
    }

    @Test
    @DisplayName("convert(Map<DTO>, Map<Existing>): Neue Keys werden übernommen und existierende beibehalten")
    void convert_merge_maps_existing_data_preserved_new_added() {
        Map<String, AdditionalInformationDTO> dtoMap = new HashMap<>();
        dtoMap.put("info1", new AdditionalInformationDTO("PLAIN_TEXT", "Laktosefrei", null, null));
        dtoMap.put("info2", null); // Sollte Wert aus existingMap übernehmen

        Map<String, BaseAdditionalInformation> existingMap = new HashMap<>();
        existingMap.put("info2", new PlainTextAdditionalInformation("Vorhanden"));
        existingMap.put("infoOld", new SizeInLiterAdditionalInformation(new BigDecimal("2.0")));

        Map<String, BaseAdditionalInformation> result =
                AdditionalInformationConverter.convert(dtoMap, existingMap);

        assertThat(result).hasSize(3);
        assertThat(result.get("info1")).isInstanceOf(PlainTextAdditionalInformation.class);
        assertThat(((PlainTextAdditionalInformation) result.get("info1")).value()).isEqualTo("Laktosefrei");
        assertThat(result.get("info2")).isInstanceOf(PlainTextAdditionalInformation.class);
        assertThat(((PlainTextAdditionalInformation) result.get("info2")).value()).isEqualTo("Vorhanden");
        assertThat(result.get("infoOld")).isInstanceOf(SizeInLiterAdditionalInformation.class);
        assertThat(((SizeInLiterAdditionalInformation) result.get("infoOld")).value()).isEqualByComparingTo("2.0");
    }

    @Test
    @DisplayName("convert(Map<DTO>, Map<Existing>): Beide Maps leer liefert leere Ergebnismap")
    void convert_merge_maps_both_empty() {
        Map<String, AdditionalInformationDTO> dtoMap = new HashMap<>();
        Map<String, BaseAdditionalInformation> existingMap = new HashMap<>();

        Map<String, BaseAdditionalInformation> result =
                AdditionalInformationConverter.convert(dtoMap, existingMap);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("convert(Map<DTO>, Map<Existing>): Neue Keys überschreiben alte")
    void convert_merge_maps_overrides_existing() {
        Map<String, AdditionalInformationDTO> dtoMap = new HashMap<>();
        dtoMap.put("shared", new AdditionalInformationDTO("SIZE_IN_LITER", "3.33", null, null));

        Map<String, BaseAdditionalInformation> existingMap = new HashMap<>();
        existingMap.put("shared", new PlainTextAdditionalInformation("Alt"));
        existingMap.put("remain", new PlainTextAdditionalInformation("Bleibt"));

        Map<String, BaseAdditionalInformation> result =
                AdditionalInformationConverter.convert(dtoMap, existingMap);

        assertThat(result.get("shared")).isInstanceOf(SizeInLiterAdditionalInformation.class);
        assertThat(((SizeInLiterAdditionalInformation) result.get("shared")).value()).isEqualByComparingTo("3.33");
        assertThat(result.get("remain")).isInstanceOf(PlainTextAdditionalInformation.class);
        assertThat(((PlainTextAdditionalInformation) result.get("remain")).value()).isEqualTo("Bleibt");
    }

    @Test
    @DisplayName("convert SIZE_IN_LITER DTO mit deutschem Komma gibt BigDecimal mit Punkt zurück")
    void convert_sizeInLiterDTO_deutschesKomma() {
        AdditionalInformationDTO dto = new AdditionalInformationDTO(
                "SIZE_IN_LITER",
                "1,75", // deutsches Komma statt Punkt
                null,
                null
        );

        BaseAdditionalInformation result = AdditionalInformationConverter.convert(dto);

        assertThat(result).isInstanceOf(SizeInLiterAdditionalInformation.class);
        assertThat(((SizeInLiterAdditionalInformation) result).value()).isEqualByComparingTo(new BigDecimal("1.75"));
    }
}