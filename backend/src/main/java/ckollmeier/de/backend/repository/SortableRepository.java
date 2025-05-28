package ckollmeier.de.backend.repository;


import ckollmeier.de.backend.interfaces.Sortable;

import java.util.List;

public interface SortableRepository<T extends Sortable> {
    List<T> findAllByIdIn(Class<T> theClass, List<String> ids);
    List<T> saveAll(List<T> entity);
}
