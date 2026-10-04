package com.example.smartpantrymanager.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {PantryItem.class, Recipe.class, RecipeIngredient.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PantryItemDao pantryItemDao();

    public abstract RecipeDao recipeDao();

    private static volatile AppDatabase instance;

    public static final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    /**
     * Singleton: one database object shared by the whole app.
     */
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "smart_pantry.db").build();
                }
            }
        }
        return instance;
    }
}