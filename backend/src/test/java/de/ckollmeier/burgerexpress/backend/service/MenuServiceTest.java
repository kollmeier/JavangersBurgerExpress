package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.DishConverter;
import de.ckollmeier.burgerexpress.backend.converter.MenuOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DishOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuOutputDTO;
import de.ckollmeier.burgerexpress.backend.exceptions.NotBlankException;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.repository.MenuRepository;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Nested;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @SuppressWarnings("unused")
    @Mock
    private DishRepository dishRepository;

    @Mock
    private ValidatedItemService<Menu> validatedMenuService;

    @InjectMocks
    private MenuService menuService;

    @Nested
    @DisplayName("getAllMenus()")
    class GetAllMenus {

        @Test
        @DisplayName("Gibt sortierte Liste von MenuOutputDTOs zurück, wenn Menüs existieren")
        void returnsSortedDTOs() {
            // Given
            Menu menu1 = Menu.builder()
                    .id("1")
                    .name("Pizza")
                    .price(new BigDecimal("10.99"))
                    .additionalInformation(Collections.emptyMap())
                    .build();
            Menu menu2 = Menu.builder()
                    .id("2")
                    .name("Fries")
                    .price(new BigDecimal("3.50"))
                    .additionalInformation(Collections.emptyMap())
                    .build();
            List<Menu> menus = List.of(menu1, menu2);

            when(menuRepository.findAllByOrderByPositionAsc()).thenReturn(menus);

            List<MenuOutputDTO> expectedDTOs = List.of(
                    new MenuOutputDTO("1",
                            "Pizza",
                            "10,99",
                            List.of(),
                            Collections.emptyMap()),
                    new MenuOutputDTO("2",
                            "Fries",
                            "3,50",
                            List.of(),
                            Collections.emptyMap())
            );

            try (MockedStatic<MenuOutputDTOConverter> converterMock = mockStatic(MenuOutputDTOConverter.class)) {
                converterMock.when(() -> MenuOutputDTOConverter.convert(menus)).thenReturn(expectedDTOs);

                // When
                List<MenuOutputDTO> result = menuService.getAllMenus();

                // Then
                assertThat(result).isEqualTo(expectedDTOs);
                verify(menuRepository).findAllByOrderByPositionAsc();
                converterMock.verify(() -> MenuOutputDTOConverter.convert(menus));
            }
        }

        @Test
        @DisplayName("Gibt leere Liste zurück, wenn keine Menüs existieren")
        void returnsEmptyList() {
            // Given
            when(menuRepository.findAllByOrderByPositionAsc()).thenReturn(Collections.emptyList());

            try (MockedStatic<MenuOutputDTOConverter> converterMock = mockStatic(MenuOutputDTOConverter.class)) {
                converterMock.when(() -> MenuOutputDTOConverter.convert(Collections.emptyList())).thenReturn(Collections.emptyList());

                // When
                List<MenuOutputDTO> result = menuService.getAllMenus();

                // Then
                assertThat(result).isEmpty();
                verify(menuRepository).findAllByOrderByPositionAsc();
                converterMock.verify(() -> MenuOutputDTOConverter.convert(Collections.emptyList()));
            }
        }
    }

    @Nested
    @DisplayName("addMenu(final MenuInputDTO)")
    class AddMenu {

        @Test
        @DisplayName("Konvertiert MenuInputDTO und speichert Menü mit korrektem Typ, gibt DTO zurück")
        void addMenuByDTO_returnsDTO() {
            // Given
            MenuInputDTO inputDTO = new MenuInputDTO("Cola",
                    "2.20",
                    List.of("dish-id"),
                    Collections.emptyMap());

            Menu convertedMenu = Menu.builder()
                    .name("Cola")
                    .price(new BigDecimal("2.20"))
                    .additionalInformation(Collections.emptyMap())
                    .dishes(List.of(mock(Dish.class)))
                    .build();

            Menu savedMenu = convertedMenu.withId("drink-id-212");

            MenuOutputDTO expectedDTO = new MenuOutputDTO("drink-id-212",
                    "Cola",
                    "2.20",
                    List.of(),
                    Collections.emptyMap());

            try (
                MockedStatic<MenuOutputDTOConverter> menuOutputConverterMock = mockStatic(MenuOutputDTOConverter.class)
            ) {
                when(validatedMenuService.validatedItemOrThrow(
                        any(),
                        anyString(),
                        anyString(),
                        any(MenuInputDTO.class),
                        nullable(String.class),
                        anyString())).thenReturn(convertedMenu);

                when(menuRepository.save(any(Menu.class))).thenReturn(savedMenu);
                menuOutputConverterMock.when(() -> MenuOutputDTOConverter.convert(savedMenu)).thenReturn(expectedDTO);

                // When
                MenuOutputDTO result = menuService.addMenu(inputDTO);

                // Then
                verify(menuRepository).save(argThat(d -> d.getName().equals("Cola")));
                assertThat(result).isEqualTo(expectedDTO);
                menuOutputConverterMock.verify(() -> MenuOutputDTOConverter.convert(savedMenu));
            }
        }

        @Test
        @DisplayName("Wirft Exception, wenn validatedMenuService wirft")
        void addMenuByDTOAndType_emptyName_throwsException() {
            // Given
            MenuInputDTO inputDTO = new MenuInputDTO("",
                    "8.40",
                    List.of(),
                    Collections.emptyMap());

            when(validatedMenuService.validatedItemOrThrow(
                    any(),
                    anyString(),
                    anyString(),
                    any(MenuInputDTO.class),
                    nullable(String.class),
                    anyString()))
                    .thenThrow(new NotBlankException("Menu name cannot be empty", "menus/add/name"));

            // When / Then
            assertThatThrownBy(() -> menuService.addMenu(inputDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Menu name cannot be empty");
            verifyNoInteractions(menuRepository);
        }
    }

    @Nested
    @DisplayName("updateMenu(final String, final MenuInputDTO)")
    class UpdateMenu {

        @Test
        @DisplayName("Aktualisiert Menu anhand ID und MenuInputDTO und gibt MenuOutputDTO zurück")
        void updatesAndReturnsDTO() {
            // Given
            Dish dish1 = Dish.builder()
                    .id("dish-1")
                    .name("Pizza")
                    .type(DishType.MAIN)
                    .imageUrl("pizza.png")
                    .price(new BigDecimal("10.99"))
                    .build();

            DishOutputDTO expectedDishDTO = new DishOutputDTO("dish-1",
                    "Pizza",
                    "10,99",
                    "main",
                    Collections.emptyMap(),
                    "pizza.png");

            String id = "test-4711";
            MenuInputDTO inputDTO = new MenuInputDTO("Wrap",
                    "9.95",
                    List.of("dish-1"),
                    Collections.emptyMap());
            Menu convertedMenu = Menu.builder()
                    .id(id)
                    .name("Wrap")
                    .price(new BigDecimal("9.95"))
                    .dishes(List.of(dish1))
                    .additionalInformation(Collections.emptyMap())
                    .build();


            when(validatedMenuService.validatedItemOrThrow(
                    any(),
                    anyString(),
                    anyString(),
                    any(MenuInputDTO.class),
                    nullable(String.class),
                    anyString(),
                    anyBoolean()))
                    .thenReturn(convertedMenu);
            when(menuRepository.save(convertedMenu)).thenReturn(convertedMenu);

            MenuOutputDTO expectedDTO = new MenuOutputDTO(id,
                    "Wrap",
                    "9,95",
                    List.of(expectedDishDTO),
                    Collections.emptyMap());

            try (
                    MockedStatic<MenuOutputDTOConverter> converterMock = mockStatic(MenuOutputDTOConverter.class)
            ) {
                converterMock.when(() -> MenuOutputDTOConverter.convert(convertedMenu)).thenReturn(expectedDTO);
                try (
                        MockedStatic<DishConverter> dishConverterMock = mockStatic(DishConverter.class)
                ) {
                    dishConverterMock.when(() -> DishConverter.convert(argThat((List<String> l) -> l.contains("dish-1")), any())).thenReturn(List.of(dish1));

                    // When
                    MenuOutputDTO result = menuService.updateMenu(id, inputDTO);

                    // Then
                    verify(menuRepository).save(convertedMenu);
                    assertThat(result).isEqualTo(expectedDTO);
                    converterMock.verify(() -> MenuOutputDTOConverter.convert(convertedMenu));
                }
            }
        }

        @Test
        @DisplayName("Wirft Exception, wenn validatedMenuService throws")
        void updateMenuByIdAndInputDto_nonexistent_throwsException() {
            // Given
            String id = "notexists-789";
            MenuInputDTO inputDTO = new MenuInputDTO("Pommes",
                    "3.90",
                    List.of(),
                    Collections.emptyMap());

            when(validatedMenuService.validatedItemOrThrow(
                    any(),
                    anyString(),
                    anyString(),
                    any(MenuInputDTO.class),
                    nullable(String.class),
                    anyString(),
                    anyBoolean()))
                    .thenThrow(new NotFoundException("Menu not found", "menus/notexists-789/"));

            // When / Then
            assertThatThrownBy(() -> menuService.updateMenu(id, inputDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Menu not found");
            verify(menuRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("removeMenu(final String)")
    class RemoveMenu {

        @Test
        @DisplayName("Entfernt ein existierendes Menu erfolgreich")
        void shouldRemoveMenu_whenMenuExists() {
            // Given
            String id = "existing-menu-id";
            when(validatedMenuService.validatedItemOrThrow(
                    any(),
                    anyString(),
                    anyString(),
                    nullable(MenuInputDTO.class),
                    argThat(s -> s.equals("existing-menu-id")),
                    anyString())
            ).thenReturn(mock(Menu.class));
            // When
            menuService.removeMenu(id);

            // Then
            verify(menuRepository).deleteById(id);
        }

        @Test
        @DisplayName("Wirft Exception, wenn validatedMenuService wirft")
        void shouldThrowException_whenMenuDoesNotExist() {
            // Given
            String id = "nonexistent-menu-id";
            when(validatedMenuService.validatedItemOrThrow(
                    any(),
                    anyString(),
                    anyString(),
                    nullable(MenuInputDTO.class),
                    nullable(String.class),
                    anyString())
            ).thenThrow(new NotFoundException("Menu not found", "menus/nonexistent-menu-id/"));

            // When / Then
            assertThatThrownBy(() -> menuService.removeMenu(id))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Menu not found");
            verify(menuRepository, never()).deleteById(any());
        }
    }
}