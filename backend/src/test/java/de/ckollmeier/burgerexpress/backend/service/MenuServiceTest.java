package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.MenuConverter;
import de.ckollmeier.burgerexpress.backend.converter.MenuOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.MenuInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.repository.MenuRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("Returns sorted list of MenuOutputDTOs when menus exist")
    void getAllMenus_returnsSortedDTOs() {
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
                        List.of(),
                        List.of(),
                        Collections.emptyMap()),
                new MenuOutputDTO("2",
                        "Fries",
                        "3,50",
                        List.of(),
                        List.of(),
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
    @DisplayName("Returns empty list if no menus exist in the repository")
    void getAllMenus_returnsEmptyList() {
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

    @Test
    @DisplayName("Saves a valid menu and returns the MenuOutputDTO")
    void addMenu_savesMenuAndReturnsDTO() {
        // Given
        Menu inputMenu = Menu.builder()
                .name("Lemonade")
                .price(new BigDecimal("2.50"))
                .additionalInformation(Collections.emptyMap())
                .build();
        Menu savedMenu = inputMenu.withId("unique-id-111");
        when(menuRepository.save(any(Menu.class))).thenReturn(savedMenu);

        MenuOutputDTO expectedDTO = new MenuOutputDTO("unique-id-111",
                "Lemonade",
                "2,50",
                List.of(),
                List.of(),
                List.of(),
                Collections.emptyMap());

        try (MockedStatic<MenuOutputDTOConverter> converterMock = mockStatic(MenuOutputDTOConverter.class)) {
            converterMock.when(() -> MenuOutputDTOConverter.convert(savedMenu)).thenReturn(expectedDTO);

            // When
            MenuOutputDTO result = menuService.addMenu(inputMenu);

            // Then
            verify(menuRepository).save(any(Menu.class));
            assertThat(result).isEqualTo(expectedDTO);
            converterMock.verify(() -> MenuOutputDTOConverter.convert(savedMenu));
        }
    }

    @Test
    @DisplayName("Throws exception when trying to save a menu with empty name")
    void addMenu_emptyName_throwsException() {
        // Given
        Menu inputMenu = Menu.builder()
                .name("")
                .price(new BigDecimal("5.00"))
                .additionalInformation(Collections.emptyMap())
                .build();

        // When / Then
        assertThatThrownBy(() -> menuService.addMenu(inputMenu))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Menu name cannot be empty");
        verifyNoInteractions(menuRepository);
    }

    @Test
    @DisplayName("Converts MenuInputDTO and stores menu with correct type, returns DTO")
    void addMenuByDTOAndType_returnsDTO() {
        // Given
        MenuInputDTO inputDTO = new MenuInputDTO("Cola",
                "2.20",
                List.of(),
                List.of(),
                List.of(),
                Collections.emptyMap());

        Menu convertedMenu = Menu.builder()
                .name("Cola")
                .price(new BigDecimal("2.20"))
                .additionalInformation(Collections.emptyMap())
                .build();

        Menu savedMenu = convertedMenu.withId("drink-id-212");

        MenuOutputDTO expectedDTO = new MenuOutputDTO("drink-id-212",
                "Cola",
                "2.20",
                List.of(),
                List.of(),
                List.of(),
                Collections.emptyMap());

        try (
            MockedStatic<MenuConverter> menuConverterMock = mockStatic(MenuConverter.class);
            MockedStatic<MenuOutputDTOConverter> menuOutputConverterMock = mockStatic(MenuOutputDTOConverter.class)
        ) {
            menuConverterMock.when(() -> MenuConverter.convert(any(), any())).thenReturn(convertedMenu);
            when(menuRepository.save(any(Menu.class))).thenReturn(savedMenu);
            menuOutputConverterMock.when(() -> MenuOutputDTOConverter.convert(savedMenu)).thenReturn(expectedDTO);

            // When
            MenuOutputDTO result = menuService.addMenu(inputDTO);

            // Then
            verify(menuRepository).save(argThat(d -> d.getName().equals("Cola")));
            assertThat(result).isEqualTo(expectedDTO);
            menuConverterMock.verify(() -> MenuConverter.convert(any(), any()));
            menuOutputConverterMock.verify(() -> MenuOutputDTOConverter.convert(savedMenu));
        }
    }

    @Test
    @DisplayName("Throws exception if name from MenuInputDTO is empty")
    void addMenuByDTOAndType_emptyName_throwsException() {
        // Given
        MenuInputDTO inputDTO = new MenuInputDTO("",
                "8.40",
                List.of(),
                List.of(),
                List.of(),
                Collections.emptyMap());

        Menu convertedMenu = Menu.builder()
                .name("")
                .price(new BigDecimal("8.40"))
                .additionalInformation(Collections.emptyMap())
                .build();

        try (MockedStatic<MenuConverter> menuConverterMock = mockStatic(MenuConverter.class)) {
            menuConverterMock.when(() -> MenuConverter.convert(any(), any())).thenReturn(convertedMenu);

            // When / Then
            assertThatThrownBy(() -> menuService.addMenu(inputDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Menu name cannot be empty");
            verifyNoInteractions(menuRepository);
            menuConverterMock.verify(() -> MenuConverter.convert(any(), any()));
        }
    }

    @Test
    @DisplayName("Aktualisiert existierendes Menu und gibt aktualisiertes MenuOutputDTO zurück")
    void updateMenu_updatesExistingMenuAndReturnsDTO() {
        // Given
        Menu existingMenu = Menu.builder()
                .id("menu-123")
                .name("Burger")
                .price(new BigDecimal("6.00"))
                .additionalInformation(Collections.emptyMap())
                .build();
        Menu updatedMenu = existingMenu.withName("Vegan Burger").withPrice(new BigDecimal("7.00"));

        when(menuRepository.existsById("menu-123")).thenReturn(true);
        when(menuRepository.save(updatedMenu)).thenReturn(updatedMenu);

        MenuOutputDTO expectedDTO = new MenuOutputDTO("menu-123",
                "Vegan Burger",
                "7,00",
                List.of(),
                List.of(),
                List.of(),
                Collections.emptyMap());

        try (MockedStatic<MenuOutputDTOConverter> converterMock = mockStatic(MenuOutputDTOConverter.class)) {
            converterMock.when(() -> MenuOutputDTOConverter.convert(updatedMenu)).thenReturn(expectedDTO);

            // When
            MenuOutputDTO result = menuService.updateMenu(updatedMenu);

            // Then
            verify(menuRepository).existsById("menu-123");
            verify(menuRepository).save(updatedMenu);
            assertThat(result).isEqualTo(expectedDTO);
            converterMock.verify(() -> MenuOutputDTOConverter.convert(updatedMenu));
        }
    }

    @Test
    @DisplayName("Wirft Exception, wenn Menu zu aktualisieren nicht existiert")
    void updateMenu_nonexistentMenu_throwsException() {
        // Given
        Menu nonExistingMenu = Menu.builder()
                .id("notfound-001")
                .name("Rice")
                .price(new BigDecimal("2.00"))
                .additionalInformation(Collections.emptyMap())
                .build();

        when(menuRepository.existsById("notfound-001")).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> menuService.updateMenu(nonExistingMenu))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Menu not found");
        verify(menuRepository).existsById("notfound-001");
        verify(menuRepository, never()).save(any());
    }

    @Test
    @DisplayName("Aktualisiert Menu anhand ID und MenuInputDTO und gibt MenuOutputDTO zurück")
    void updateMenuByIdAndInputDto_updatesAndReturnsDTO() {
        // Given
        String id = "test-4711";
        MenuInputDTO inputDTO = new MenuInputDTO("Wrap",
                "9.95",
                List.of(),
                List.of(),
                List.of(),
                Collections.emptyMap());
        Menu convertedMenu = Menu.builder()
                .id(id)
                .name("Wrap")
                .price(new BigDecimal("9.95"))
                .additionalInformation(Collections.emptyMap())
                .build();

        when(menuRepository.findById(id)).thenReturn(Optional.of(convertedMenu));
        when(menuRepository.existsById(id)).thenReturn(true);
        when(menuRepository.save(convertedMenu)).thenReturn(convertedMenu);

        MenuOutputDTO expectedDTO = new MenuOutputDTO(id,
                "Wrap",
                "9,95",
                List.of(),
                List.of(),
                List.of(),
                Collections.emptyMap());

        try (
                MockedStatic<MenuOutputDTOConverter> converterMock = mockStatic(MenuOutputDTOConverter.class)
        ) {
            converterMock.when(() -> MenuOutputDTOConverter.convert(convertedMenu)).thenReturn(expectedDTO);

            // When
            MenuOutputDTO result = menuService.updateMenu(id, inputDTO);

            // Then
            verify(menuRepository).findById(id);
            verify(menuRepository).save(convertedMenu);
            assertThat(result).isEqualTo(expectedDTO);
            converterMock.verify(() -> MenuOutputDTOConverter.convert(convertedMenu));
        }
    }

    @Test
    @DisplayName("Wirft Exception, wenn zu aktualisierendes Menu anhand ID und DTO nicht existiert")
    void updateMenuByIdAndInputDto_nonexistent_throwsException() {
        // Given
        String id = "notexists-789";
        MenuInputDTO inputDTO = new MenuInputDTO("Pommes",
                "3.90",
                List.of(),
                List.of(),
                List.of(),
                Collections.emptyMap());

        when(menuRepository.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> menuService.updateMenu(id, inputDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Menu not found");
        verify(menuRepository).findById(id);
        verify(menuRepository, never()).save(any());
    }


    @Test
    @DisplayName("Entfernt ein existierendes Menu erfolgreich")
    void removeMenu_shouldRemoveMenu_whenMenuExists() {
        // Given
        String id = "existing-menu-id";
        when(menuRepository.existsById(id)).thenReturn(true);

        // When
        menuService.removeMenu(id);

        // Then
        verify(menuRepository).existsById(id);
        verify(menuRepository).deleteById(id);
    }

    @Test
    @DisplayName("Wirft Exception, wenn zu löschendes Menu nicht existiert")
    void removeMenu_shouldThrowException_whenMenuDoesNotExist() {
        // Given
        String id = "nonexistent-menu-id";
        when(menuRepository.existsById(id)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> menuService.removeMenu(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Menu not found");
        verify(menuRepository).existsById(id);
        verify(menuRepository, never()).deleteById(any());
    }
}