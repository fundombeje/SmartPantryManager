package com.example.smartpantrymanager.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * One ingredient the user currently has at home.
 */
@Entity(tableName = "pantry_items")
public class PantryItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;        // e.g. "tomatoes"
    public double quantity;    // e.g. 500
    public String unit;        // e.g. "g", "ml", "pcs"
    public String expiryDate;  // optional, "yyyy-MM-dd", may be null

    public PantryItem() {
    }    // required by Room

    @Ignore
    public PantryItem(String name, double quantity, String unit, String expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }
}