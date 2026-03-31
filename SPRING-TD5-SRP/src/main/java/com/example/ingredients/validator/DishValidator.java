package com.example.ingredients.validator;

import com.example.ingredients.entity.Ingredient;
import com.example.ingredients.repository.DishRepository;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DishValidator {

    private final DishRepository dishRepository;

    public DishValidator(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public void validateUpdateIngredients(Integer dishId, List<Ingredient> ingredients) {
        if (dishId == null) throw new IllegalArgumentException("Dish ID cannot be null");
        if (ingredients == null) throw new IllegalArgumentException("Ingredients list cannot be null");
        if (dishRepository.findById(dishId).isEmpty())
            throw new IllegalArgumentException("Dish with id " + dishId + " not found");
    }
}