package com.example.ingredients.entity;


import com.example.ingredients.entity.Enum.MovementTypeEnum;
import com.example.ingredients.entity.Enum.Unit;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {
    private Integer id;
    private Integer ingredientId;
    private Double quantity;
    private MovementTypeEnum type;
    private Unit unit;
    private Instant creationDatetime;
}