package com.example.ingredients.entity;


import com.example.ingredients.entity.Enum.Unit;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishIngredient {
    private Dish dish;
    private Ingredient ingredient;
    private Double requiredQuantity;
    private Unit unit;
}
