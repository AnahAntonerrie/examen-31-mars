package com.example.ingredients.service;


import com.example.ingredients.entity.Dish;
import com.example.ingredients.entity.Ingredient;
import com.example.ingredients.repository.DishRepository;
import com.example.ingredients.repository.IngredientRepository;
import com.example.ingredients.validator.DishValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;
    private final DishValidator dishValidator;

    public DishService(DishRepository dishRepository, IngredientRepository ingredientRepository, DishValidator dishValidator) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
        this.dishValidator = dishValidator;
    }

    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    public Optional<Dish> getDishById(Integer id) {
        return dishRepository.findById(id);
    }

    @Transactional
    public void updateDishIngredients(Integer dishId, List<Ingredient> ingredients) {
        dishValidator.validateUpdateIngredients(dishId, ingredients);
        List<Ingredient> existingIngredients = ingredients.stream()
                .filter(ing -> ingredientRepository.findById(ing.getId()).isPresent())
                .collect(Collectors.toList());
        dishRepository.updateIngredients(dishId, existingIngredients);
    }
    public List<Ingredient> getIngredientsByFilters(Integer dishId, String ingredientName, Double ingredientPriceAround) {
        return dishRepository.findIngredientsByFilters(dishId, ingredientName, ingredientPriceAround);
    }
}