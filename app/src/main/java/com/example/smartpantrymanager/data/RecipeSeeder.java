package com.example.smartpantrymanager.data;

import java.util.ArrayList;
import java.util.List;

/** Inserts the starter recipes into the database the first time the app runs. */
public class RecipeSeeder {

    /** Must be called on a background thread. */
    public static void seedIfEmpty(AppDatabase db) {
        RecipeDao dao = db.recipeDao();
        if (dao.getRecipeCount() > 0) {
            return; // already seeded
        }

        // One transaction: either all recipes are saved or none are.
        db.runInTransaction(() -> {
            add(dao, "Scrambled Eggs",
                    "1. Whisk the eggs with the salt.\n2. Melt the butter in a pan.\n3. Pour in the eggs and stir gently until just set.",
                    ing("egg", 3, "pcs"), ing("butter", 10, "g"), ing("salt", 1, "g"));

            add(dao, "Grilled Cheese Sandwich",
                    "1. Butter the outside of the bread.\n2. Place the cheese between the slices.\n3. Grill until golden on both sides.",
                    ing("bread", 2, "pcs"), ing("cheese", 50, "g"), ing("butter", 10, "g"));

            add(dao, "Tomato Pasta",
                    "1. Boil the pasta until tender.\n2. Fry the onion and garlic in the oil.\n3. Add chopped tomatoes and salt, simmer 10 minutes.\n4. Toss the pasta through the sauce.",
                    ing("pasta", 200, "g"), ing("tomato", 3, "pcs"), ing("onion", 1, "pcs"),
                    ing("garlic", 2, "pcs"), ing("cooking oil", 15, "ml"), ing("salt", 2, "g"));

            add(dao, "Egg Fried Rice",
                    "1. Cook the rice and let it cool.\n2. Fry the chopped onion in the oil.\n3. Push aside, scramble the eggs in the pan.\n4. Add the rice and salt, stir-fry for 5 minutes.",
                    ing("rice", 200, "g"), ing("egg", 2, "pcs"), ing("onion", 1, "pcs"),
                    ing("cooking oil", 15, "ml"), ing("salt", 1, "g"));

            add(dao, "Pancakes",
                    "1. Mix the flour and sugar.\n2. Whisk in the milk and egg to make a smooth batter.\n3. Fry spoonfuls in a lightly oiled pan until golden on both sides.",
                    ing("flour", 150, "g"), ing("milk", 250, "ml"), ing("egg", 1, "pcs"), ing("sugar", 20, "g"));

            add(dao, "Mashed Potatoes",
                    "1. Peel and chop the potatoes.\n2. Boil until soft, then drain.\n3. Mash with the butter, milk and salt.",
                    ing("potato", 500, "g"), ing("butter", 30, "g"), ing("milk", 50, "ml"), ing("salt", 2, "g"));

            add(dao, "Cheese Omelette",
                    "1. Beat the eggs with the salt.\n2. Melt the butter in a pan and pour in the eggs.\n3. Add the grated cheese, fold in half and serve.",
                    ing("egg", 3, "pcs"), ing("cheese", 30, "g"), ing("butter", 10, "g"), ing("salt", 1, "g"));

            add(dao, "Garlic Bread",
                    "1. Mash the crushed garlic into the softened butter.\n2. Spread on the bread.\n3. Bake at 200 C for 8-10 minutes until crisp.",
                    ing("bread", 4, "pcs"), ing("butter", 40, "g"), ing("garlic", 2, "pcs"));

            add(dao, "Banana Porridge",
                    "1. Heat the oats and milk in a pot, stirring.\n2. Cook for 5 minutes until thick.\n3. Top with sliced banana.",
                    ing("oats", 60, "g"), ing("milk", 250, "ml"), ing("banana", 1, "pcs"));

            add(dao, "Tuna Mayo Sandwich",
                    "1. Drain the tuna and mix with the mayonnaise.\n2. Spread on a slice of bread.\n3. Top with the second slice and cut in half.",
                    ing("bread", 2, "pcs"), ing("tuna", 80, "g"), ing("mayonnaise", 20, "g"));

            add(dao, "Cucumber Yoghurt Salad",
                    "1. Dice the cucumber.\n2. Stir into the yoghurt with the crushed garlic and salt.\n3. Chill for 15 minutes before serving.",
                    ing("cucumber", 1, "pcs"), ing("yoghurt", 150, "g"), ing("garlic", 1, "pcs"), ing("salt", 1, "g"));

            add(dao, "Chicken Rice Bowl",
                    "1. Cook the rice.\n2. Cut the chicken into pieces and fry with the onion in the oil.\n3. Season with salt.\n4. Serve over the rice.",
                    ing("chicken", 200, "g"), ing("rice", 150, "g"), ing("onion", 1, "pcs"),
                    ing("cooking oil", 15, "ml"), ing("salt", 2, "g"));

            add(dao, "Loaded Baked Potato",
                    "1. Prick the potatoes and bake at 200 C for about 1 hour.\n2. Cut open and add the butter and salt.\n3. Top with grated cheese and return to the oven for 5 minutes.",
                    ing("potato", 2, "pcs"), ing("cheese", 60, "g"), ing("butter", 15, "g"), ing("salt", 1, "g"));

            add(dao, "Tomato Soup",
                    "1. Fry the chopped onion and garlic in the butter.\n2. Add the chopped tomatoes and salt.\n3. Simmer for 20 minutes, then blend until smooth.",
                    ing("tomato", 6, "pcs"), ing("onion", 1, "pcs"), ing("garlic", 2, "pcs"),
                    ing("butter", 20, "g"), ing("salt", 2, "g"));

            add(dao, "French Toast",
                    "1. Whisk the egg, milk and sugar together.\n2. Dip each slice of bread in the mixture.\n3. Fry in the butter until golden on both sides.",
                    ing("bread", 4, "pcs"), ing("egg", 2, "pcs"), ing("milk", 100, "ml"),
                    ing("sugar", 10, "g"), ing("butter", 10, "g"));

            add(dao, "Creamy Chicken Pasta",
                    "1. Boil the pasta.\n2. Fry the chicken pieces and garlic until cooked.\n3. Add the milk, cheese and salt and simmer until creamy.\n4. Mix in the pasta.",
                    ing("pasta", 200, "g"), ing("chicken", 200, "g"), ing("milk", 150, "ml"),
                    ing("cheese", 40, "g"), ing("garlic", 2, "pcs"), ing("salt", 2, "g"));

            add(dao, "Vegetable Fried Rice",
                    "1. Cook the rice.\n2. Fry the diced carrots and onion in the oil until soft.\n3. Add the rice and salt, stir-fry for 5 minutes.",
                    ing("rice", 200, "g"), ing("carrot", 2, "pcs"), ing("onion", 1, "pcs"),
                    ing("cooking oil", 15, "ml"), ing("salt", 2, "g"));

            add(dao, "Beans on Toast",
                    "1. Toast and butter the bread.\n2. Heat the beans in a small pot.\n3. Pour over the toast and sprinkle with cheese.",
                    ing("bread", 2, "pcs"), ing("beans", 200, "g"), ing("butter", 10, "g"), ing("cheese", 30, "g"));

            add(dao, "Fried Egg Sandwich",
                    "1. Fry the eggs in the butter and season with salt.\n2. Place between the slices of bread.",
                    ing("bread", 2, "pcs"), ing("egg", 2, "pcs"), ing("butter", 10, "g"), ing("salt", 1, "g"));
        });
    }

    /** Saves one recipe, then saves its ingredients linked by the new recipe id. */
    private static void add(RecipeDao dao, String name, String steps, RecipeIngredient... items) {
        long recipeId = dao.insertRecipe(new Recipe(name, steps));
        List<RecipeIngredient> list = new ArrayList<>();
        for (RecipeIngredient item : items) {
            item.recipeId = (int) recipeId;
            list.add(item);
        }
        dao.insertIngredients(list);
    }

    /** Shorthand for creating an ingredient; recipeId is filled in by add(). */
    private static RecipeIngredient ing(String name, double quantity, String unit) {
        return new RecipeIngredient(0, name, quantity, unit);
    }
}