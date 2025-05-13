package ckollmeier.de.backend.repository;

import ckollmeier.de.backend.model.Dish;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends MongoRepository<Dish, String> {
    List<Dish> findAllByOrderByPositionAsc();
}
