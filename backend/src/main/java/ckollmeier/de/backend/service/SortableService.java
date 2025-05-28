package ckollmeier.de.backend.service;

import ckollmeier.de.backend.dto.SortedInputDTO;
import ckollmeier.de.backend.interfaces.Sortable;
import ckollmeier.de.backend.repository.SortableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList; // Importieren
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SortableService<T extends Sortable> {
    /**
     * The repository for storing and retrieving sorted items.
     */
    private final SortableRepository<T> sortableRepository;

    /**
     * Reorders a list of items based on the provided sorted input DTOs
     * and saves the updated order.
     *
     * @param theClass       The class of the items to reorder.
     * @param sortedInputDTOS The list of DTOs containing the new order of items.
     * @return sortedList The list of items with the updated order.
     */
    public List<T> reorder(final Class<T> theClass, final List<SortedInputDTO> sortedInputDTOS) {
        if (sortedInputDTOS.isEmpty()) {
            return List.of();
        }
        Map<String, Integer> positionsByItemId = new HashMap<>();
        sortedInputDTOS.forEach(input -> positionsByItemId.put(input.id(), input.index()));

        List<T> items = sortableRepository.findAll(theClass);

        List<T> sortedItems = new ArrayList<>(items.size()); // Initialkapazität setzen
        for (T item : items) {
            if (!positionsByItemId.containsKey(item.getId())) {
                sortedItems.add(item);
                continue;
            }
            T updatedItem = item.withPosition(positionsByItemId.get(item.getId()));
            sortedItems.add(updatedItem);
        }
        sortedItems.sort(T::compareWith);

        return sortableRepository.saveAll(sortedItems);
    }
}