package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.Recipe;
import com.example.smartpantrymanager.data.RecipeDao;
import com.example.smartpantrymanager.data.RecipeIngredient;

import java.util.List;

/**
 * Shows one recipe: its name, the full ingredient list, and the preparation steps.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    /**
     * Key for the Intent extra that carries the id of the recipe to show.
     */
    public static final String EXTRA_RECIPE_ID = "recipe_id";
    private static final int NO_ID = -1;

    private TextView textName;
    private TextView textIngredients;
    private TextView textSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle(R.string.recipe_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back arrow
        }

        textName = findViewById(R.id.textRecipeName);
        textIngredients = findViewById(R.id.textIngredients);
        textSteps = findViewById(R.id.textSteps);

        // Read the recipe id that the previous screen sent in the Intent
        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, NO_ID);
        if (recipeId == NO_ID) {
            finish(); // nothing to show without an id
            return;
        }
        loadRecipe(recipeId);
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
     * Loads the recipe and its ingredients on a background thread, then fills in the screen.
     */
    private void loadRecipe(int recipeId) {
        AppDatabase.databaseExecutor.execute(() -> {
            RecipeDao dao = AppDatabase.getInstance(getApplicationContext()).recipeDao();
            Recipe recipe = dao.getRecipeById(recipeId);
            List<RecipeIngredient> ingredients = dao.getIngredientsForRecipe(recipeId);

            runOnUiThread(() -> {
                if (recipe == null) {
                    finish(); // recipe no longer exists
                    return;
                }
                textName.setText(recipe.name);
                textIngredients.setText(buildIngredientList(ingredients));
                textSteps.setText(recipe.steps);
            });
        });
    }

    /**
     * Turns the ingredient rows into lines like "• 200 g pasta".
     */
    private String buildIngredientList(List<RecipeIngredient> ingredients) {
        StringBuilder builder = new StringBuilder();
        for (RecipeIngredient ingredient : ingredients) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append("• ")
                    .append(PantryAdapter.formatQuantity(ingredient.quantity))
                    .append(" ")
                    .append(ingredient.unit)
                    .append(" ")
                    .append(ingredient.name);
        }
        return builder.toString();
    }
}