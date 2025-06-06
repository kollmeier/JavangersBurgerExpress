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

            // Create real DisplayItemOutputDTO instances instead of mocking them
            DisplayItemOutputDTO dto1 = new DisplayItemOutputDTO(
                "id1", "category1", "Item 1", "Description 1", 
                Collections.emptyList(), "10.00", null, true);
            DisplayItemOutputDTO dto2 = new DisplayItemOutputDTO(
                "id2", "category2", "Item 2", "Description 2", 
                Collections.emptyList(), "20.00", "25.00", false);
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
            // Create a real DisplayItemInputDTO instead of mocking it
            DisplayItemInputDTO inputDTO = new DisplayItemInputDTO(
                "Test Item", "Test Description", true, "10.00",
                List.of("item1", "item2"), true, "category1");

            DisplayItem converted = mock(DisplayItem.class);
            when(converterService.convert(inputDTO)).thenReturn(converted);

            DisplayItem withId = mock(DisplayItem.class);
            when(converted.withId(anyString())).thenReturn(withId);

            DisplayItem saved = mock(DisplayItem.class);
            when(displayItemRepository.save(withId)).thenReturn(saved);

            // Create a real DisplayItemOutputDTO instead of mocking it
            DisplayItemOutputDTO expectedDTO = new DisplayItemOutputDTO(
                "test-id", "test-category", "Test Item", "Test Description", 
                Collections.emptyList(), "10.00", null, true);

            try (MockedStatic<DisplayItemOutputDTOConverter> mockStatic = mockStatic(DisplayItemOutputDTOConverter.class)) {
                mockStatic.when(() -> DisplayItemOutputDTOConverter.convert(saved)).thenReturn(expectedDTO);

                DisplayItemOutputDTO result = displayItemService.addDisplayItem(inputDTO);

                assertThat(result).isEqualTo(expectedDTO);
                verify(converterService).convert(inputDTO);
                verify(displayItemRepository).save(withId);
            }
        }

        @Test
        @DisplayName("shouldThrowException_whenValidatedItemServiceThrows")
        void addDisplayItem_shouldThrowException_whenValidatedItemServiceThrows() {
            // Create a real DisplayItemInputDTO instead of mocking it
            DisplayItemInputDTO inputDTO = new DisplayItemInputDTO(
                "Invalid Item", "Invalid Description", true, "15.00",
                List.of("item1"), true, "category1");

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
            // Create a real DisplayItemInputDTO instead of mocking it
            DisplayItemInputDTO inputDTO = new DisplayItemInputDTO(
                "Error Item", "Error Description", false, "5.00",
                List.of("item1"), false, "category-error");

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
            // Create a real DisplayItemInputDTO instead of mocking it
            DisplayItemInputDTO inputDTO = new DisplayItemInputDTO(
                "Updated Item", "Updated Description", true, "12.50",
                List.of("item1", "item2"), true, "category-updated");

            DisplayItem existingItem = mock(DisplayItem.class);
            when(validatedDisplayItemService.validatedItemOrThrow(
                    eq(DisplayItem.class), eq("Anzeigeelement"), eq("displayItems"), isNull(), eq(id), eq("update")))
                    .thenReturn(existingItem);

            DisplayItem updated = mock(DisplayItem.class);
            when(converterService.convert(inputDTO, existingItem)).thenReturn(updated);

            DisplayItem saved = mock(DisplayItem.class);
            when(displayItemRepository.save(updated)).thenReturn(saved);

            // Create a real DisplayItemOutputDTO instead of mocking it
            DisplayItemOutputDTO expectedDTO = new DisplayItemOutputDTO(
                "test-id", "test-category", "Test Item", "Test Description", 
                Collections.emptyList(), "10.00", null, true);

            try (MockedStatic<DisplayItemOutputDTOConverter> mockStatic = mockStatic(DisplayItemOutputDTOConverter.class)) {
                mockStatic.when(() -> DisplayItemOutputDTOConverter.convert(saved)).thenReturn(expectedDTO);

                DisplayItemOutputDTO result = displayItemService.updateDisplayItem(id, inputDTO);

                assertThat(result).isEqualTo(expectedDTO);
                verify(validatedDisplayItemService).validatedItemOrThrow(
                        eq(DisplayItem.class), eq("Anzeigeelement"), eq("displayItems"), isNull(), eq(id), eq("update"));
                verify(converterService).convert(inputDTO, existingItem);
                verify(displayItemRepository).save(updated);
            }
        }
    }
}
