package com.example.smartpantrymanager.logic;

import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.Recipe;
import com.example.smartpantrymanager.data.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** a recipe is suggested ONLY if every ingredient it needs is in the pantry, */
public final class RecipeMatcher {

    /** Tolerance so decimal rounding never rejects an exact match. */
    private static final double EPSILON = 1e-9;

    private RecipeMatcher() { }

    /** Returns only the recipes the user can cook right now with what is in the pantry. */
    public static List<Recipe> findStrictMatches(List<Recipe> recipes,
                                                 List<RecipeIngredient> allIngredients,
                                                 List<PantryItem> pantry) {
        Map<String, Double> pantryTotals = buildPantryTotals(pantry);
        Map<Integer, List<RecipeIngredient>> ingredientsByRecipe = groupByRecipe(allIngredients);

        List<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : recipes) {
            List<RecipeIngredient> needed = ingredientsByRecipe.get(recipe.id);

            // A recipe with no ingredients must never count as "matched"
            if (needed == null || needed.isEmpty()) {
                continue;
            }
            if (countMissing(needed, pantryTotals) == 0) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    /** How many of the recipe's ingredients the pantry cannot fully cover (0 = can cook it). */
    public static int countMissing(List<RecipeIngredient> needed, Map<String, Double> pantryTotals) {
        // Add up the recipe's needs first, in case it lists the same ingredient twice
        Map<String, Double> required = new HashMap<>();
        for (RecipeIngredient ingredient : needed) {
            addToTotals(required, ingredient.name, ingredient.quantity, ingredient.unit);
        }

        int missing = 0;
        for (Map.Entry<String, Double> entry : required.entrySet()) {
            double have = pantryTotals.getOrDefault(entry.getKey(), 0.0);
            if (have + EPSILON < entry.getValue()) {
                missing++;
            }
        }
        return missing;
    }

    /** Total quantity per ingredient, keyed like "tomato|pcs" or "rice|g". */
    public static Map<String, Double> buildPantryTotals(List<PantryItem> pantry) {
        Map<String, Double> totals = new HashMap<>();
        for (PantryItem item : pantry) {
            addToTotals(totals, item.name, item.quantity, item.unit);
        }
        return totals;
    }

    /** Normalises name and unit, then adds the quantity to the running total for that key. */
    private static void addToTotals(Map<String, Double> totals, String name,
                                    double quantity, String unit) {
        String normalizedName = IngredientNormalizer.normalizeName(name);
        if (normalizedName.isEmpty()) {
            return;
        }
        IngredientNormalizer.BaseQuantity base = IngredientNormalizer.toBase(quantity, unit);
        String key = normalizedName + "|" + base.unit;
        totals.put(key, totals.getOrDefault(key, 0.0) + base.amount);
    }

    private static Map<Integer, List<RecipeIngredient>> groupByRecipe(List<RecipeIngredient> all) {
        Map<Integer, List<RecipeIngredient>> grouped = new HashMap<>();
        for (RecipeIngredient ingredient : all) {
            grouped.computeIfAbsent(ingredient.recipeId, id -> new ArrayList<>()).add(ingredient);
        }
        return grouped;
    }
}