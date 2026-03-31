package com.example.ingredients.controller;

import com.example.ingredients.entity.Dish;
import com.example.ingredients.entity.Ingredient;
import com.example.ingredients.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }
    @GetMapping
    public List<Dish> getAllDishes() {
        return dishService.getAllDishes();
    }
    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable Integer id,
            @RequestBody List<Ingredient> ingredients) {
        try {
            dishService.updateDishIngredients(id, ingredients);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{id}/ingredients")
    public ResponseEntity<?> getDishIngredients(
            @PathVariable Integer id,
            @RequestParam(required = false) String ingredientName,
            @RequestParam(required = false) Double ingredientPriceAround) {

        Optional<Dish> dishOpt = dishService.getDishById(id);
        if (dishOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Dish.id=" + id + " is not found");
        }
        List<Ingredient> ingredients = dishService.getIngredientsByFilters(id, ingredientName, ingredientPriceAround);
        return ResponseEntity.ok(ingredients);
    }
}
