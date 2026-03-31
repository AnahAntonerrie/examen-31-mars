package com.example.ingredients.validator;

import com.example.ingredients.entity.Enum.Unit;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Arrays;

@Component
public class IngredientValidator {

    public void validateStockParams(Integer ingredientId, Instant at, String unit) {
        if (ingredientId == null) throw new IllegalArgumentException("Ingredient ID cannot be null");
        if (at == null) throw new IllegalArgumentException("Parameter 'at' is required");
        if (unit == null || unit.trim().isEmpty()) throw new IllegalArgumentException("Parameter 'unit' is required");
        boolean unitSupported = Arrays.stream(Unit.values())
                .anyMatch(u -> u.name().equalsIgnoreCase(unit));
        if (!unitSupported) throw new IllegalArgumentException("Unit '" + unit + "' is not supported. Supported units: " + Arrays.toString(Unit.values()));
    }
}