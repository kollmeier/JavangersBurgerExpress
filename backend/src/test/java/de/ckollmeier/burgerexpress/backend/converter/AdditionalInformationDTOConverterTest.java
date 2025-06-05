package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.AdditionalInformationDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.AdditionalInformation;
import de.ckollmeier.burgerexpress.backend.interfaces.BaseAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.model.PlainTextAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.model.SizeInLiterAdditionalInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class AdditionalInformationDTOConverterTest {

    @Test
    @DisplayName("convert(PlainTextAdditionalInformation) gibt korrekten DTO zurück")
    void convert_plainText_gibtDTO() {
        var info = new PlainTextAdditionalInformation("Vegan");
        var dto = AdditionalInformationDTOConverter.convert(info);

        assertThat(dto.type()).isEqualTo("PLAIN_TEXT");
        assertThat(dto.value()).isEqualTo("Vegan");
        assertThat(dto.displayString()).isEqualTo(info.displayString());
        assertThat(dto.shortDisplayString()).isEqualTo(info.shortDisplayString());
    }

    @Test
    @DisplayName("convert(SizeInLiterAdditionalInformation) gibt korrekten DTO zurück")
    void convert_sizeInLiter_gibtDTO() {
        var info = new SizeInLiterAdditionalInformation(new BigDecimal("0.75"));
        var dto = AdditionalInformationDTOConverter.convert(info);

        assertThat(dto.type()).isEqualTo("SIZE_IN_LITER");
        assertThat(dto.value()).isEqualTo("0.75");
        assertThat(dto.displayString()).isEqualTo(info.displayString());
        assertThat(dto.shortDisplayString()).isEqualTo(info.shortDisplayString());
    }

    @Test
    @DisplayName("convert(Map<String, BaseAdditionalInformation>) gibt korrektes DTO-Map zurück")
    void convert_map_gibtKorrektesDTOMap() {
        Map<String, BaseAdditionalInformation> infos = new HashMap<>();
        infos.put("desc", new PlainTextAdditionalInformation("Bio"));
        infos.put("amount", new SizeInLiterAdditionalInformation(new BigDecimal("1.5")));

        Map<String, AdditionalInformationDTO> dtoMap = AdditionalInformationDTOConverter.convert(infos);

        assertThat(dtoMap)
                .hasSize(2)
                .containsKeys("desc", "amount");

        AdditionalInformationDTO plainDto = dtoMap.get("desc");
        assertThat(plainDto.type()).isEqualTo("PLAIN_TEXT");
        assertThat(plainDto.value()).isEqualTo("Bio");

        AdditionalInformationDTO literDto = dtoMap.get("amount");
        assertThat(literDto.type()).isEqualTo("SIZE_IN_LITER");
        assertThat(literDto.value()).isEqualTo("1.5");
    }

    @Test
    @DisplayName("Konstruktor der Utility-Klasse ist privat und wirft Exception")
    void constructor_is_private_and_throws() throws Exception {
        var ctor = AdditionalInformationDTOConverter.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertThatThrownBy(ctor::newInstance)
                .isInstanceOf(java.lang.reflect.InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("This is a utility class and cannot be instantiated");
    }
}