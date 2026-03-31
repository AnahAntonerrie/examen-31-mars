package com.example.ingredients.service;


import com.example.ingredients.entity.Ingredient;
import com.example.ingredients.entity.StockValue;
import com.example.ingredients.repository.IngredientRepository;
import com.example.ingredients.validator.IngredientValidator;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientValidator ingredientValidator;

    public IngredientService(IngredientRepository ingredientRepository, IngredientValidator ingredientValidator) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientValidator = ingredientValidator;
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Optional<Ingredient> getIngredientById(Integer id) {
        return ingredientRepository.findById(id);
    }

    public StockValue getStockAt(Integer ingredientId, Instant at, String unit) {
        if (ingredientRepository.findById(ingredientId).isEmpty()) {
            throw new IllegalArgumentException("Ingredient.id=" + ingredientId + " is not found");
        }
        ingredientValidator.validateStockParams(ingredientId, at, unit);
        return ingredientRepository.getStockAt(ingredientId, at, unit);
    }
}
