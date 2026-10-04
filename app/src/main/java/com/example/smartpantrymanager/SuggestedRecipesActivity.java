package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.Recipe;
import com.example.smartpantrymanager.data.RecipeIngredient;
import com.example.smartpantrymanager.logic.RecipeMatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lists only the recipes the user can cook right now, using the strict-matching rule.
 */
public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecipeAdapter adapter;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle(R.string.suggested_recipes);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back arrow
        }

        textEmpty = findViewById(R.id.textEmpty);

        RecyclerView recyclerRecipes = findViewById(R.id.recyclerRecipes);
        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter();
        recyclerRecipes.setAdapter(adapter);
        // Tapping a recipe opens its detail screen, passing the recipe id in the Intent
        adapter.setOnRecipeClickListener(recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.id);
            startActivity(intent);
        });
    }

    /**
     * Recalculate every time the screen is shown, so it always reflects the current pantry.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    /**
     * Makes the toolbar back arrow close this screen.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Reads the database and runs the strict matcher on a background thread.
     */
    private void loadSuggestions() {
        AppDatabase.databaseExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());

            List<Recipe> recipes = db.recipeDao().getAllRecipes();
            List<RecipeIngredient> ingredients = db.recipeDao().getAllIngredients();
            List<PantryItem> pantry = db.pantryItemDao().getAll();

            // The core rule: only recipes whose every ingredient is in the pantry
            List<Recipe> matches = RecipeMatcher.findStrictMatches(recipes, ingredients, pantry);

            // Ingredient counts, shown on each recipe card
            Map<Integer, Integer> counts = new HashMap<>();
            for (RecipeIngredient ingredient : ingredients) {
                counts.put(ingredient.recipeId, counts.getOrDefault(ingredient.recipeId, 0) + 1);
            }

            runOnUiThread(() -> {
                adapter.setRecipes(matches, counts);
                textEmpty.setVisibility(matches.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}