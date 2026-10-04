package com.example.smartpantrymanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.Recipe;
import com.example.smartpantrymanager.data.RecipeIngredient;
import com.example.smartpantrymanager.logic.RecipeMatcher;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeMatcherTest {

    /** Runs the matcher for one test recipe (id 1) and returns the suggested recipes. */
    private static List<Recipe> suggest(List<RecipeIngredient> needed, List<PantryItem> pantry) {
        Recipe recipe = new Recipe("Test recipe", "steps");
        recipe.id = 1;
        return RecipeMatcher.findStrictMatches(Arrays.asList(recipe), needed, pantry);
    }

    private static RecipeIngredient need(String name, double quantity, String unit) {
        return new RecipeIngredient(1, name, quantity, unit);
    }

    private static PantryItem have(String name, double quantity, String unit) {
        return new PantryItem(name, quantity, unit, null);
    }

    @Test
    public void allIngredientsPresent_isSuggested() {
        List<RecipeIngredient> needed = Arrays.asList(
                need("bread", 2, "pcs"), need("cheese", 50, "g"), need("butter", 10, "g"));
        List<PantryItem> pantry = Arrays.asList(
                have("bread", 4, "pcs"), have("cheese", 200, "g"), have("butter", 250, "g"));

        assertEquals(1, suggest(needed, pantry).size());
    }

    @Test
    public void oneIngredientMissing_isNotSuggested() {
        // The recipe needs 5 ingredients and the pantry has only 4 of them
        List<RecipeIngredient> needed = Arrays.asList(
                need("tomato", 3, "pcs"), need("onion", 1, "pcs"), need("garlic", 2, "pcs"),
                need("cooking oil", 15, "ml"), need("salt", 2, "g"));
        List<PantryItem> pantry = Arrays.asList(
                have("tomato", 5, "pcs"), have("onion", 2, "pcs"), have("garlic", 4, "pcs"),
                have("cooking oil", 750, "ml"));

        assertTrue(suggest(needed, pantry).isEmpty());
    }

    @Test
    public void quantityTooLow_isNotSuggested() {
        List<RecipeIngredient> needed = Arrays.asList(need("rice", 200, "g"));
        List<PantryItem> pantry = Arrays.asList(have("rice", 100, "g"));

        assertTrue(suggest(needed, pantry).isEmpty());
    }

    @Test
    public void pluralAndSingularNames_match() {
        List<RecipeIngredient> needed = Arrays.asList(need("tomato", 3, "pcs"));
        List<PantryItem> pantry = Arrays.asList(have("Tomatoes", 3, "pcs"));

        assertEquals(1, suggest(needed, pantry).size());
    }

    @Test
    public void differentUnits_kilogramsSatisfyGrams() {
        List<RecipeIngredient> needed = Arrays.asList(need("rice", 200, "g"));
        List<PantryItem> pantry = Arrays.asList(have("rice", 1, "kg"));

        assertEquals(1, suggest(needed, pantry).size());
    }

    @Test
    public void duplicatePantryRows_areAddedTogether() {
        List<RecipeIngredient> needed = Arrays.asList(need("egg", 3, "pcs"));
        List<PantryItem> pantry = Arrays.asList(have("egg", 2, "pcs"), have("eggs", 1, "pcs"));

        assertEquals(1, suggest(needed, pantry).size());
    }

    @Test
    public void incompatibleUnits_doNotMatch() {
        // 2 pieces of rice cannot be compared with 200 grams of rice
        List<RecipeIngredient> needed = Arrays.asList(need("rice", 200, "g"));
        List<PantryItem> pantry = Arrays.asList(have("rice", 2, "pcs"));

        assertTrue(suggest(needed, pantry).isEmpty());
    }

    @Test
    public void recipeWithNoIngredients_isNotSuggested() {
        List<PantryItem> pantry = Arrays.asList(have("egg", 6, "pcs"));

        assertTrue(suggest(new ArrayList<RecipeIngredient>(), pantry).isEmpty());
    }
}