package com.example.ingredients.repository;

import com.example.ingredients.Datasource.Datasource;
import com.example.ingredients.entity.*;
import com.example.ingredients.entity.Enum.CategoryEnum;
import com.example.ingredients.entity.Enum.DishTypeEnum;
import com.example.ingredients.entity.Enum.Unit;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DishRepository {

    private final Datasource datasource;
    private final IngredientRepository ingredientRepository; // pour vérifier l'existence des ingrédients

    public DishRepository(Datasource datasource, IngredientRepository ingredientRepository) {
        this.datasource = datasource;
        this.ingredientRepository = ingredientRepository;
    }

    public List<Dish> findAll() {
        String sql = "SELECT id, name, dish_type, selling_price FROM dish";
        try (Connection conn = datasource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Dish> dishes = new ArrayList<>();
            while (rs.next()) {
                Dish dish = mapDish(rs);
                dish.setDishIngredients(findIngredientsForDish(dish.getId()));
                dishes.add(dish);
            }
            return dishes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Dish> findById(Integer id) {
        String sql = "SELECT id, name, dish_type, selling_price FROM dish WHERE id = ?";
        try (Connection conn = datasource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Dish dish = mapDish(rs);
                    dish.setDishIngredients(findIngredientsForDish(dish.getId()));
                    return Optional.of(dish);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<DishIngredient> findIngredientsForDish(Integer dishId) {
        String sql = """
            SELECT i.id, i.name, i.category, i.price, di.required_quantity, di.unit
            FROM ingredient i
            JOIN dish_ingredient di ON i.id = di.id_ingredient
            WHERE di.id_dish = ?
        """;
        try (Connection conn = datasource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dishId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<DishIngredient> list = new ArrayList<>();
                while (rs.next()) {
                    Ingredient ing = new Ingredient();
                    ing.setId(rs.getInt("id"));
                    ing.setName(rs.getString("name"));
                    ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    ing.setPrice(rs.getDouble("price"));
                    DishIngredient di = new DishIngredient();
                    di.setIngredient(ing);
                    di.setRequiredQuantity(rs.getDouble("required_quantity"));
                    di.setUnit(Unit.valueOf(rs.getString("unit")));
                    list.add(di);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void updateIngredients(Integer dishId, List<Ingredient> ingredients) {
        try (Connection conn = datasource.getConnection()) {
            conn.setAutoCommit(false);
            // Supprimer les anciennes associations
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM dish_ingredient WHERE id_dish = ?")) {
                stmt.setInt(1, dishId);
                stmt.executeUpdate();
            }
            String insertSql = "INSERT INTO dish_ingredient (id_dish, id_ingredient, required_quantity, unit) VALUES (?, ?, ?, ?::unit)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                for (Ingredient ing : ingredients) {
                    stmt.setInt(1, dishId);
                    stmt.setInt(2, ing.getId());
                    stmt.setDouble(3, 1.0); // quantité par défaut
                    stmt.setString(4, "PCS");
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ingredient> findIngredientsByFilters(Integer dishId, String ingredientName, Double ingredientPriceAround) {
        StringBuilder sql = new StringBuilder("""
            SELECT i.id, i.name, i.category, i.price
            FROM ingredient i
            JOIN dish_ingredient di ON i.id = di.id_ingredient
            WHERE di.id_dish = ?
        """);
        List<Object> params = new ArrayList<>();
        params.add(dishId);

        if (ingredientName != null && !ingredientName.trim().isEmpty()) {
            sql.append(" AND i.name ILIKE ?");
            params.add("%" + ingredientName + "%");
        }
        if (ingredientPriceAround != null) {
            sql.append(" AND i.price BETWEEN ? AND ?");
            params.add(ingredientPriceAround - 50);
            params.add(ingredientPriceAround + 50);
        }

        try (Connection conn = datasource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) stmt.setInt(i + 1, (Integer) p);
                else if (p instanceof String) stmt.setString(i + 1, (String) p);
                else if (p instanceof Double) stmt.setDouble(i + 1, (Double) p);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                List<Ingredient> ingredients = new ArrayList<>();
                while (rs.next()) {
                    Ingredient ing = new Ingredient();
                    ing.setId(rs.getInt("id"));
                    ing.setName(rs.getString("name"));
                    ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    ing.setPrice(rs.getDouble("price"));
                    ingredients.add(ing);
                }
                return ingredients;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Dish mapDish(ResultSet rs) throws SQLException {
        Dish d = new Dish();
        d.setId(rs.getInt("id"));
        d.setName(rs.getString("name"));
        d.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
        double price = rs.getDouble("selling_price");
        d.setSellingPrice(rs.wasNull() ? null : price);
        return d;
    }
}