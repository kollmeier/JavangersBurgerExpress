package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.DisplayCategoryOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DisplayCategoryInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DisplayCategoryOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import de.ckollmeier.burgerexpress.backend.repository.DisplayCategoryRepository;
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
class DisplayCategoryServiceTest {

    @Mock
    private DisplayCategoryRepository displayCategoryRepository;

    @Mock
    private ValidatedItemService<DisplayCategory> validatedDisplayCategoryService;

    @Mock
    private DisplayItemService displayItemService;

    @InjectMocks
    private DisplayCategoryService displayCategoryService;

    @Nested
    @DisplayName("getAllDisplayCategories()")
    class GetAllDisplayCategories {

        @Test
        @DisplayName("shouldReturnSortedDTOs_whenDisplayCategoriesExist")
        void getAllDisplayCategories_shouldReturnSortedDTOs_whenDisplayCategoriesExist() {
            // given
            DisplayCategory cat1 = mock(DisplayCategory.class);
            DisplayCategory cat2 = mock(DisplayCategory.class);
            List<DisplayCategory> categories = List.of(cat1, cat2);
            when(displayCategoryRepository.findAllByOrderByPositionAsc()).thenReturn(categories);

            // Create real DisplayCategoryOutputDTO instances instead of mocking them
            DisplayCategoryOutputDTO dto1 = new DisplayCategoryOutputDTO(
                "id1", "Category 1", "Description 1", 
                Collections.emptyList(), "image1.jpg", true);
            DisplayCategoryOutputDTO dto2 = new DisplayCategoryOutputDTO(
                "id2", "Category 2", "Description 2", 
                Collections.emptyList(), "image2.jpg", false);
            List<DisplayCategoryOutputDTO> dtos = List.of(dto1, dto2);
            try (MockedStatic<DisplayCategoryOutputDTOConverter> mock = mockStatic(DisplayCategoryOutputDTOConverter.class)) {
                mock.when(() -> DisplayCategoryOutputDTOConverter.convert(categories)).thenReturn(dtos);

                // when
                List<DisplayCategoryOutputDTO> result = displayCategoryService.getAllDisplayCategories();

                // then
                assertThat(result).containsExactlyElementsOf(dtos);
                verify(displayCategoryRepository).findAllByOrderByPositionAsc();
            }
        }

        @Test
        @DisplayName("shouldReturnEmptyList_whenNoDisplayCategoriesExist")
        void getAllDisplayCategories_shouldReturnEmptyList_whenNoDisplayCategoriesExist() {
            // given
            when(displayCategoryRepository.findAllByOrderByPositionAsc()).thenReturn(Collections.emptyList());
            try (MockedStatic<DisplayCategoryOutputDTOConverter> mock = mockStatic(DisplayCategoryOutputDTOConverter.class)) {
                    mock.when(() -> DisplayCategoryOutputDTOConverter.convert(Collections.emptyList()))
                        .thenReturn(Collections.emptyList());

                // when
                List<DisplayCategoryOutputDTO> result = displayCategoryService.getAllDisplayCategories();

                // then
                assertThat(result).isEmpty();
                verify(displayCategoryRepository).findAllByOrderByPositionAsc();
            }

        }
    }

    @Nested
    @DisplayName("addDisplayCategory(DisplayCategoryInputDTO)")
    class AddDisplayCategory {

        @Test
        @DisplayName("shouldConvertAndSaveDisplayCategory_whenValidInput")
        void addDisplayCategory_shouldConvertAndSaveDisplayCategory_whenValidInput() {
            // given
            DisplayCategoryInputDTO inputDTO = mock(DisplayCategoryInputDTO.class);
            DisplayCategory validated = mock(DisplayCategory.class);
            when(validatedDisplayCategoryService.validatedItemOrThrow(
                    eq(DisplayCategory.class), anyString(), anyString(), eq(inputDTO), isNull(), anyString()
            )).thenReturn(validated);
            when(validated.withId(anyString())).thenReturn(validated);

            DisplayCategory saved = mock(DisplayCategory.class);
            when(displayCategoryRepository.save(validated)).thenReturn(saved);

            // Create a real DisplayCategoryOutputDTO instead of mocking it
            DisplayCategoryOutputDTO outputDTO = new DisplayCategoryOutputDTO(
                "test-id", "Test Category", "Test Description", 
                Collections.emptyList(), "test-image.jpg", true);
            try (MockedStatic<DisplayCategoryOutputDTOConverter> mock = mockStatic(DisplayCategoryOutputDTOConverter.class)) {
                mock.when(() -> DisplayCategoryOutputDTOConverter.convert(saved)).thenReturn(outputDTO);

                // when
                DisplayCategoryOutputDTO result = displayCategoryService.addDisplayCategory(inputDTO);

                // then
                assertThat(result).isEqualTo(outputDTO);
                verify(validatedDisplayCategoryService).validatedItemOrThrow(
                        eq(DisplayCategory.class), anyString(), anyString(), eq(inputDTO), isNull(), anyString());
                verify(displayCategoryRepository).save(validated);
            }
        }

        @Test
        @DisplayName("shouldThrowException_whenValidatedItemServiceThrows")
        void addDisplayCategory_shouldThrowException_whenValidatedItemServiceThrows() {
            // given
            DisplayCategoryInputDTO inputDTO = mock(DisplayCategoryInputDTO.class);
            when(validatedDisplayCategoryService.validatedItemOrThrow(any(), any(), any(), any(), any(), any()))
                    .thenThrow(new IllegalArgumentException("Invalid"));

            // when / then
            assertThatThrownBy(() -> displayCategoryService.addDisplayCategory(inputDTO)).isInstanceOf(IllegalArgumentException.class);
            verify(validatedDisplayCategoryService).validatedItemOrThrow(any(), any(), any(), any(), any(), any());
            verify(displayCategoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateDisplayCategory(String, DisplayCategoryInputDTO)")
    class UpdateDisplayCategory {

        @Test
        @DisplayName("shouldUpdateAndReturnDTO_whenValidInput")
        void updateDisplayCategory_shouldUpdateAndReturnDTO_whenValidInput() {
            // given
            String id = "dc-1";
            DisplayCategoryInputDTO inputDTO = mock(DisplayCategoryInputDTO.class);
            DisplayCategory validated = mock(DisplayCategory.class);

            when(validatedDisplayCategoryService.validatedItemOrThrow(
                    eq(DisplayCategory.class), anyString(), anyString(), eq(inputDTO), eq(id), anyString(), eq(true)
            )).thenReturn(validated);

            DisplayCategory saved = mock(DisplayCategory.class);
            when(displayCategoryRepository.save(validated)).thenReturn(saved);

            // Create a real DisplayCategoryOutputDTO instead of mocking it
            DisplayCategoryOutputDTO outputDTO = new DisplayCategoryOutputDTO(
                "updated-id", "Updated Category", "Updated Description", 
                Collections.emptyList(), "updated-image.jpg", true);
            try (MockedStatic<DisplayCategoryOutputDTOConverter> mockStatic = mockStatic(DisplayCategoryOutputDTOConverter.class)) {
                mockStatic.when(() -> DisplayCategoryOutputDTOConverter.convert(saved)).thenReturn(outputDTO);

                // when
                DisplayCategoryOutputDTO result = displayCategoryService.updateDisplayCategory(id, inputDTO);

                // then
                assertThat(result).isEqualTo(outputDTO);
                verify(validatedDisplayCategoryService).validatedItemOrThrow(
                        eq(DisplayCategory.class), anyString(), anyString(), eq(inputDTO), eq(id), anyString(), eq(true));
                verify(displayCategoryRepository).save(validated);

            }
        }

        @Test
        @DisplayName("shouldThrowException_whenValidatedItemServiceThrows")
        void updateDisplayCategory_shouldThrowException_whenValidatedItemServiceThrows() {
            // given
            String id = "not-existing";
            DisplayCategoryInputDTO inputDTO = mock(DisplayCategoryInputDTO.class);
            when(validatedDisplayCategoryService.validatedItemOrThrow(
                    any(), any(), any(), any(), any(), any(), anyBoolean()))
                    .thenThrow(new IllegalArgumentException("Not found"));

            // when / then
            assertThatThrownBy(() -> displayCategoryService.updateDisplayCategory(id, inputDTO)).isInstanceOf(IllegalArgumentException.class);
            verify(validatedDisplayCategoryService).validatedItemOrThrow(
                    any(), any(), any(), any(), any(), any(), anyBoolean());
            verify(displayCategoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("removeDisplayCategory(String)")
    class RemoveDisplayCategory {

        @Test
        @DisplayName("shouldRemoveDisplayCategory_whenExists")
        void removeDisplayCategory_shouldRemoveDisplayCategory_whenExists() {
            // given
            String id = "dc-2";
            when(validatedDisplayCategoryService.validatedItemOrThrow(
                    eq(DisplayCategory.class), anyString(), anyString(), isNull(), eq(id), anyString()))
            .thenReturn(mock(DisplayCategory.class));
            doNothing().when(displayCategoryRepository).deleteById(id);

            // when
            displayCategoryService.removeDisplayCategory(id);

            // then
            verify(validatedDisplayCategoryService).validatedItemOrThrow(
                    eq(DisplayCategory.class), anyString(), anyString(), isNull(), eq(id), anyString());
            verify(displayCategoryRepository).deleteById(id);
            verify(displayItemService, never()).removeDisplayItem(anyString());
        }

        @Test
        @DisplayName("shouldRemoveDisplayCategory_whenExists")
        void removeDisplayCategory_shouldRemoveDisplayItems_whenDisplayItemsWithCategoryExist() {
            // given
            String id = "dc-2";
            DisplayCategory cat = mock(DisplayCategory.class);
            DisplayItem item1 = mock(DisplayItem.class);
            when(item1.getId()).thenReturn("item1-id");
            DisplayItem item2 = mock(DisplayItem.class);
            when(item2.getId()).thenReturn("item2-id");

            when(cat.getDisplayItems()).thenReturn(List.of(item1, item2));

            when(validatedDisplayCategoryService.validatedItemOrThrow(
                    eq(DisplayCategory.class), anyString(), anyString(), isNull(), eq(id), anyString()))
            .thenReturn(cat);
            doNothing().when(displayCategoryRepository).deleteById(id);
            doNothing().when(displayItemService).removeDisplayItem(anyString());

            // when
            displayCategoryService.removeDisplayCategory(id);

            // then
            verify(validatedDisplayCategoryService).validatedItemOrThrow(
                    eq(DisplayCategory.class), anyString(), anyString(), isNull(), eq(id), anyString());
            verify(displayCategoryRepository).deleteById(id);
            verify(displayItemService).removeDisplayItem("item1-id");
            verify(displayItemService).removeDisplayItem("item2-id");
        }

        @Test
        @DisplayName("shouldThrowException_whenValidatedItemServiceThrows")
        void removeDisplayCategory_shouldThrowException_whenValidatedItemServiceThrows() {
            // given
            String id = "dc-invalid";
            doThrow(new IllegalArgumentException("Not found")).when(validatedDisplayCategoryService)
                    .validatedItemOrThrow(any(), any(), any(), isNull(), eq(id), any());

            // when/then
            assertThatThrownBy(() -> displayCategoryService.removeDisplayCategory(id)).isInstanceOf(IllegalArgumentException.class);
            verify(displayCategoryRepository, never()).deleteById(any());
            verify(displayItemService, never()).removeDisplayItem(anyString());
        }
    }
}
