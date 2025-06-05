package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.DisplayItemOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DisplayItemInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DisplayItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import de.ckollmeier.burgerexpress.backend.repository.DisplayItemRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisplayItemServiceTest {

    @Mock
    private DisplayItemRepository displayItemRepository;

    @Mock
    private ValidatedItemService<DisplayItem> validatedDisplayItemService;

    @Mock
    private ConverterService converterService;

    @InjectMocks
    private DisplayItemService displayItemService;

    @Nested
    @DisplayName("getAllDisplayItems()")
    class GetAllDisplayItems {

        @Test
        @DisplayName("shouldReturnSortedDTOs_whenDisplayItemsExist")
        void getAllDisplayItems_shouldReturnSortedDTOs_whenDisplayItemsExist() {
            DisplayItem item1 = mock(DisplayItem.class);
            DisplayItem item2 = mock(DisplayItem.class);
            List<DisplayItem> items = List.of(item1, item2);
            when(displayItemRepository.findAllByOrderByPositionAsc()).thenReturn(items);

            DisplayItemOutputDTO dto1 = mock(DisplayItemOutputDTO.class);
            DisplayItemOutputDTO dto2 = mock(DisplayItemOutputDTO.class);
            List<DisplayItemOutputDTO> dtos = List.of(dto1, dto2);
            try (MockedStatic<DisplayItemOutputDTOConverter> mockStatic = mockStatic(DisplayItemOutputDTOConverter.class)) {
                mockStatic.when(() -> DisplayItemOutputDTOConverter.convert(items)).thenReturn(dtos);

                List<DisplayItemOutputDTO> result = displayItemService.getAllDisplayItems();

                assertThat(result).containsExactlyElementsOf(dtos);
                verify(displayItemRepository).findAllByOrderByPositionAsc();
            }
        }

        @Test
        @DisplayName("shouldReturnEmptyList_whenNoDisplayItemsExist")
        void getAllDisplayItems_shouldReturnEmptyList_whenNoDisplayItemsExist() {
            when(displayItemRepository.findAllByOrderByPositionAsc()).thenReturn(Collections.emptyList());
            try (MockedStatic<DisplayItemOutputDTOConverter> mockStatic = mockStatic(DisplayItemOutputDTOConverter.class)) {
                mockStatic.when(() -> DisplayItemOutputDTOConverter.convert(Collections.emptyList())).thenReturn(Collections.emptyList());

                List<DisplayItemOutputDTO> result = displayItemService.getAllDisplayItems();

                assertThat(result).isEmpty();
                verify(displayItemRepository).findAllByOrderByPositionAsc();
            }
        }
    }

    @Nested
    @DisplayName("addDisplayItem(DisplayItemInputDTO)")
    class AddDisplayItem {

        @Test
        @DisplayName("shouldConvertAndSaveDisplayItem_whenValidInput")
        void addDisplayItem_shouldConvertAndSaveDisplayItem_whenValidInput() {
            DisplayItemInputDTO inputDTO = mock(DisplayItemInputDTO.class);
            when(inputDTO.orderableItemIds()).thenReturn(List.of("item1", "item2"));

            DisplayItem converted = mock(DisplayItem.class);
            when(converterService.convert(inputDTO)).thenReturn(converted);

            DisplayItem withId = mock(DisplayItem.class);
            when(converted.withId(anyString())).thenReturn(withId);

            DisplayItem saved = mock(DisplayItem.class);
            when(displayItemRepository.save(withId)).thenReturn(saved);

            DisplayItemOutputDTO outputDTO = mock(DisplayItemOutputDTO.class);
            try (MockedStatic<DisplayItemOutputDTOConverter> mockStatic = mockStatic(DisplayItemOutputDTOConverter.class)) {
                mockStatic.when(() -> DisplayItemOutputDTOConverter.convert(saved)).thenReturn(outputDTO);

                DisplayItemOutputDTO result = displayItemService.addDisplayItem(inputDTO);

                assertThat(result).isEqualTo(outputDTO);
                verify(converterService).convert(inputDTO);
                verify(displayItemRepository).save(withId);
            }
        }

        @Test
        @DisplayName("shouldThrowException_whenValidatedItemServiceThrows")
        void addDisplayItem_shouldThrowException_whenValidatedItemServiceThrows() {
            DisplayItemInputDTO inputDTO = mock(DisplayItemInputDTO.class);
            when(inputDTO.orderableItemIds()).thenReturn(List.of("item1"));

            when(converterService.convert(inputDTO)).thenThrow(new IllegalArgumentException("Ungültige Eingabe"));

            assertThatThrownBy(() -> displayItemService.addDisplayItem(inputDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ungültige Eingabe");
        }
    }

    @Nested
    @DisplayName("removeDisplayItem(String id)")
    class RemoveDisplayItem {

        @Test
        @DisplayName("shouldValidateAndDeleteDisplayItem_whenIdGiven")
        void removeDisplayItem_shouldValidateAndDeleteDisplayItem_whenIdGiven() {
            String id = "itemid-123";

            DisplayItem item = mock(DisplayItem.class);
            when(validatedDisplayItemService.validatedItemOrThrow(
                    eq(DisplayItem.class), eq("Anzeigeelement"), eq("displayItems"), isNull(), eq(id), eq("delete")))
                    .thenReturn(item);

            doNothing().when(displayItemRepository).deleteById(id);

            displayItemService.removeDisplayItem(id);

            verify(validatedDisplayItemService).validatedItemOrThrow(
                    eq(DisplayItem.class), eq("Anzeigeelement"), eq("displayItems"), isNull(), eq(id), eq("delete"));
            verify(displayItemRepository).deleteById(id);
        }
    }

    @Nested
    @DisplayName("updateDisplayItem(String id, DisplayItemInputDTO input)")
    class UpdateDisplayItem {

        @Test
        @DisplayName("shouldThrowException_whenValidatedItemServiceThrows")
        void updateDisplayItem_shouldThrowException_whenValidatedItemServiceThrows() {
            String id = "itemid-error";
            DisplayItemInputDTO inputDTO = mock(DisplayItemInputDTO.class);
            when(inputDTO.orderableItemIds()).thenReturn(List.of("item1"));

            DisplayItem existingItem = mock(DisplayItem.class);
            when(validatedDisplayItemService.validatedItemOrThrow(
                    eq(DisplayItem.class), eq("Anzeigeelement"), eq("displayItems"), isNull(), eq(id), eq("update")))
                    .thenReturn(existingItem);

            when(converterService.convert(inputDTO, existingItem)).thenThrow(new IllegalStateException("Fehler in Validation"));

            assertThatThrownBy(() -> displayItemService.updateDisplayItem(id, inputDTO))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Fehler in Validation");
        }

        @Test
        @DisplayName("shouldValidateAndUpdateDisplayItem_whenValidInput")
        void updateDisplayItem_shouldValidateAndUpdateDisplayItem_whenValidInput() {
            String id = "itemid-xyz";
            DisplayItemInputDTO inputDTO = mock(DisplayItemInputDTO.class);
            when(inputDTO.orderableItemIds()).thenReturn(List.of("item1", "item2"));

            DisplayItem existingItem = mock(DisplayItem.class);
            when(validatedDisplayItemService.validatedItemOrThrow(
                    eq(DisplayItem.class), eq("Anzeigeelement"), eq("displayItems"), isNull(), eq(id), eq("update")))
                    .thenReturn(existingItem);

            DisplayItem updated = mock(DisplayItem.class);
            when(converterService.convert(inputDTO, existingItem)).thenReturn(updated);

            DisplayItem saved = mock(DisplayItem.class);
            when(displayItemRepository.save(updated)).thenReturn(saved);

            DisplayItemOutputDTO outputDTO = mock(DisplayItemOutputDTO.class);
            try (MockedStatic<DisplayItemOutputDTOConverter> mockStatic = mockStatic(DisplayItemOutputDTOConverter.class)) {
                mockStatic.when(() -> DisplayItemOutputDTOConverter.convert(saved)).thenReturn(outputDTO);

                DisplayItemOutputDTO result = displayItemService.updateDisplayItem(id, inputDTO);

                assertThat(result).isEqualTo(outputDTO);
                verify(validatedDisplayItemService).validatedItemOrThrow(
                        eq(DisplayItem.class), eq("Anzeigeelement"), eq("displayItems"), isNull(), eq(id), eq("update"));
                verify(converterService).convert(inputDTO, existingItem);
                verify(displayItemRepository).save(updated);
            }
        }
    }
}
