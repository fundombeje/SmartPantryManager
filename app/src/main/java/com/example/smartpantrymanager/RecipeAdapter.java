package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shows the suggested recipes in a RecyclerView: one card per recipe.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    /**
     * Lets the Activity react when a recipe is tapped (opens the detail screen).
     */
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private List<Recipe> recipes = new ArrayList<>();
    private Map<Integer, Integer> ingredientCounts = new HashMap<>();
    private OnRecipeClickListener listener;

    public void setOnRecipeClickListener(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    /**
     * Replaces the displayed recipes; counts maps recipe id to its number of ingredients.
     */
    public void setRecipes(List<Recipe> newRecipes, Map<Integer, Integer> counts) {
        this.recipes = new ArrayList<>(newRecipes);
        this.ingredientCounts = counts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.textName.setText(recipe.name);

        int count = ingredientCounts.getOrDefault(recipe.id, 0);
        holder.textCount.setText(count == 1 ? "1 ingredient" : count + " ingredients");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /**
     * Holds the views of one recipe row.
     */
    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textCount;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textRecipeName);
            textCount = itemView.findViewById(R.id.textIngredientCount);
        }
    }
}