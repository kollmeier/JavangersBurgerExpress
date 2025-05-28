package ckollmeier.de.backend.service;

import ckollmeier.de.backend.dto.SortedInputDTO;
import ckollmeier.de.backend.interfaces.Sortable;
import ckollmeier.de.backend.repository.SortableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SortableServiceTest {

    @Mock
    private SortableRepository<SortableMock> sortableRepository;

    private SortableService<SortableMock> sortableService;

    @BeforeEach
    void setUp() {
        this.sortableService = new SortableService<>(sortableRepository);
    }

    @Test
    @DisplayName("reorder gibt leere Liste zurück, wenn Input leer ist")
    void reorder_leer() {
        List<SortedInputDTO> inputs = List.of();
        List<SortableMock> result = sortableService.reorder(SortableMock.class, inputs);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(sortableRepository);
    }

    @Test
    @DisplayName("reorder ordnet und speichert korrekt")
    void reorder_erfolgreich() {
        List<SortedInputDTO> inputs = List.of(
                new SortedInputDTO(2, "a"),
                new SortedInputDTO(0, "b"),
                new SortedInputDTO(1, "c")
        );
        List<String> expectedIds = inputs.stream().map(SortedInputDTO::id).toList();

        // Vorbereitung: Items aus Repository zurückliefern
        List<SortableMock> repoItems = List.of(
                new SortableMock("a", 0),
                new SortableMock("b", 1),
                new SortableMock("c", 2)
        );
        when(sortableRepository.findAllByIdIn(SortableMock.class, expectedIds)).thenReturn(repoItems);

        // Vorbereitung: Save gibt Liste direkt zurück
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<SortableMock>> captor = ArgumentCaptor.forClass(List.class);
        when(sortableRepository.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);

        List<SortableMock> result = sortableService.reorder(SortableMock.class, inputs);

        // Prüfe, ob alle IDs übernommen wurden und Positionen entsprechend zugewiesen wurden
        assertEquals(3, result.size());
        for (SortableMock mock : result) {
            int expectedPosition = inputs.stream()
                    .filter(in -> in.id().equals(mock.getId()))
                    .findFirst().orElseThrow().index();
            assertEquals(expectedPosition, mock.getPosition());
        }

        // Repository-Methoden geprüft:
        verify(sortableRepository).findAllByIdIn(SortableMock.class, expectedIds);
        verify(sortableRepository).saveAll(captor.capture());
        List<SortableMock> savedMocks = captor.getValue();
        assertEquals(3, savedMocks.size());
    }

    // Hilfsklasse zur Simulation des Sortable-Objekts
    static class SortableMock implements Sortable {
        private final String id;
        private final int position;

        SortableMock(String id, int position) {
            this.id = id;
            this.position = position;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public int getPosition() {
            return position;
        }

        @Override
        public <T extends Sortable> T withPosition(Integer position) {
            // Rückgabe von neuer Instanz mit aktualisierter Position
            //noinspection unchecked
            return (T) new SortableMock(this.id, position);
        }

        @Override
        public int compareWith(Sortable other) {
            return Integer.compare(this.position, other.getPosition());
        }
    }
}