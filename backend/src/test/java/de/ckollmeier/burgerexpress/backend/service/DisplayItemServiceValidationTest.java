package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.DisplayItemOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DisplayItemInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DisplayItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.exceptions.NotEmptyException;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import de.ckollmeier.burgerexpress.backend.repository.DisplayItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DisplayItemService Validation Tests")
class DisplayItemServiceValidationTest {

    @Mock
    private DisplayItemRepository displayItemRepository;

    @Mock
    private ValidatedItemService<DisplayItem> validatedDisplayItemService;

    @Mock
    private ConverterService converterService;

    @InjectMocks
    private DisplayItemService displayItemService;

    @Nested
    @DisplayName("validatedDisplayItemOrThrow method")
    class ValidatedDisplayItemOrThrowTests {

        @Test
        @DisplayName("should throw NullPointerException when orderableItemIds is null for new item")
        void shouldThrowNullPointerException_whenOrderableItemIdsIsNull_forNewItem() {
            // Arrange
            DisplayItemInputDTO inputDTO = mock(DisplayItemInputDTO.class);
            when(inputDTO.orderableItemIds()).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> displayItemService.addDisplayItem(inputDTO))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Bestellbare Artikel dürfen nicht null sein!");
        }

        @Test
        @DisplayName("should throw NotEmptyException when orderableItemIds is empty")
        void shouldThrowNotEmptyException_whenOrderableItemIdsIsEmpty() {
            // Arrange
            DisplayItemInputDTO inputDTO = mock(DisplayItemInputDTO.class);
            when(inputDTO.orderableItemIds()).thenReturn(Collections.emptyList());

            // Act & Assert
            assertThatThrownBy(() -> displayItemService.addDisplayItem(inputDTO))
                    .isInstanceOf(NotEmptyException.class)
                    .hasMessage("Bestellbare Artikel dürfen nicht leer sein!");
        }

        @Test
        @DisplayName("should not throw exception when orderableItemIds is null for update")
        void shouldNotThrowException_whenOrderableItemIdsIsNull_forUpdate() {
            // Arrange
            String id = "item-id";

            DisplayItem existingItem = mock(DisplayItem.class);
            when(validatedDisplayItemService.validatedItemOrThrow(
                    eq(DisplayItem.class), eq("Anzeigeelement"), eq("displayItems"), isNull(), eq(id), eq("delete")))
                    .thenReturn(existingItem);

            // Act & Assert - should not throw exception
            displayItemService.removeDisplayItem(id);

            // Verify
            verify(validatedDisplayItemService).validatedItemOrThrow(
                    eq(DisplayItem.class), eq("Anzeigeelement"), eq("displayItems"), isNull(), eq(id), eq("delete"));
            verify(displayItemRepository).deleteById(id);
        }

        @Test
        @DisplayName("should not throw exception when orderableItemIds has valid items")
        void shouldNotThrowException_whenOrderableItemIdsHasValidItems() {
            // Arrange
            DisplayItemInputDTO inputDTO = mock(DisplayItemInputDTO.class);
            when(inputDTO.orderableItemIds()).thenReturn(List.of("item1", "item2"));

            DisplayItem convertedItem = mock(DisplayItem.class);
            when(converterService.convert(inputDTO)).thenReturn(convertedItem);

            DisplayItem withIdItem = mock(DisplayItem.class);
            when(convertedItem.withId(anyString())).thenReturn(withIdItem);

            DisplayItem savedItem = mock(DisplayItem.class);
            when(displayItemRepository.save(any(DisplayItem.class))).thenReturn(savedItem);

            DisplayItemOutputDTO outputDTO = mock(DisplayItemOutputDTO.class);

            try (MockedStatic<DisplayItemOutputDTOConverter> mockStatic = mockStatic(DisplayItemOutputDTOConverter.class)) {
                mockStatic.when(() -> DisplayItemOutputDTOConverter.convert(savedItem)).thenReturn(outputDTO);

                // Act
                DisplayItemOutputDTO result = displayItemService.addDisplayItem(inputDTO);

                // Assert
                assertThat(result).isEqualTo(outputDTO);
                verify(converterService).convert(inputDTO);
                verify(displayItemRepository).save(withIdItem);
            }
        }
    }
}
