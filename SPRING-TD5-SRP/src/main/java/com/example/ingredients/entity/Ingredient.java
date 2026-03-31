package com.example.ingredients.entity;

import com.example.ingredients.entity.Enum.CategoryEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;
}