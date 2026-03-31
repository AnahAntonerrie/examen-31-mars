package com.example.ingredients.repository;

import com.example.ingredients.Datasource.Datasource;
import com.example.ingredients.entity.Enum.CategoryEnum;
import com.example.ingredients.entity.Ingredient;
import com.example.ingredients.entity.StockValue;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class IngredientRepository {

    private final Datasource datasource;

    public IngredientRepository(Datasource datasource) {
        this.datasource = datasource;
    }

    public List<Ingredient> findAll() {
        String sql = "SELECT id, name, category, price FROM ingredient";
        try (Connection conn = datasource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Ingredient> ingredients = new ArrayList<>();
            while (rs.next()) {
                ingredients.add(mapIngredient(rs));
            }
            return ingredients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Ingredient> findById(Integer id) {
        String sql = "SELECT id, name, category, price FROM ingredient WHERE id = ?";
        try (Connection conn = datasource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapIngredient(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public StockValue getStockAt(Integer ingredientId, Instant at, String unit) {
        String sql = """
            SELECT COALESCE(SUM(
                CASE WHEN type::text = 'IN' THEN quantity ELSE -quantity END
            ), 0) AS total
            FROM stock_movement
            WHERE id_ingredient = ? AND creation_datetime <= ? AND unit = ?::unit
        """;
        try (Connection conn = datasource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ingredientId);
            stmt.setTimestamp(2, Timestamp.from(at));
            stmt.setString(3, unit);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble("total");
                    return new StockValue(unit, total);
                }
                return new StockValue(unit, 0.0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Ingredient mapIngredient(ResultSet rs) throws SQLException {
        Ingredient i = new Ingredient();
        i.setId(rs.getInt("id"));
        i.setName(rs.getString("name"));
        i.setCategory(CategoryEnum.valueOf(rs.getString("category")));
        i.setPrice(rs.getDouble("price"));
        return i;
    }
}