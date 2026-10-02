package com.example.smartpantrymanager.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** One ingredient line of a recipe. Deleting a recipe deletes its ingredients. */
@Entity(
        tableName = "recipe_ingredients",
        foreignKeys = @ForeignKey(
                entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("recipeId")}
)
public class RecipeIngredient {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int recipeId;      // links to Recipe.id
    public String name;
    public double quantity;
    public String unit;

    public RecipeIngredient() { }

    @Ignore
    public RecipeIngredient(int recipeId, String name, double quantity, String unit) {
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }
}