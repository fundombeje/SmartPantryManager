package com.example.smartpantrymanager.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/** A recipe: its name and preparation steps. Ingredients live in their own table. */
@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String steps;

    public Recipe() { }

    @Ignore
    public Recipe(String name, String steps) {
        this.name = name;
        this.steps = steps;
    }
}