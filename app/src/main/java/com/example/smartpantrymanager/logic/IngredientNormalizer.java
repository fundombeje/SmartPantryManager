package com.example.smartpantrymanager.logic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Makes ingredient names and units comparable eg: singular/plural, kg vs g, l vs ml.
 */
public final class IngredientNormalizer {

    /**
     * Words that end in "s" but are not plurals.
     */
    private static final Set<String> NOT_PLURAL = new HashSet<>(Arrays.asList(
            "hummus", "couscous", "asparagus", "citrus", "molasses", "watercress"));

    private IngredientNormalizer() {
    }

    /**
     * A quantity converted to a base unit: "g", "ml", "pcs"
     */
    public static final class BaseQuantity {
        public final double amount;
        public final String unit;

        BaseQuantity(double amount, String unit) {
            this.amount = amount;
            this.unit = unit;
        }
    }

    /**
     * "  Fresh Tomatoes! " becomes "fresh tomato".
     */
    public static String normalizeName(String raw) {
        if (raw == null) {
            return "";
        }
        // Lowercase, and turn anything that is not a letter into a space
        String cleaned = raw.toLowerCase(Locale.ROOT).replaceAll("\\P{L}+", " ").trim();
        if (cleaned.isEmpty()) {
            return "";
        }
        // Only the last word is singularised: "olive oils" -> "olive oil"
        int lastSpace = cleaned.lastIndexOf(' ');
        String prefix = cleaned.substring(0, lastSpace + 1); // empty if one word
        String lastWord = cleaned.substring(lastSpace + 1);
        return prefix + singularize(lastWord);
    }

    /**
     * Simple rule-based singular form; deliberately not a full language model.
     */
    static String singularize(String word) {
        if (word.length() <= 3 || NOT_PLURAL.contains(word)) {
            return word;
        }
        if (word.equals("loaves")) {
            return "loaf";
        }
        if (word.endsWith("ss") || word.endsWith("us")) {
            return word;                                      // glass, citrus
        }
        if (word.endsWith("ies")) {
            return word.substring(0, word.length() - 3) + "y"; // berries -> berry
        }
        if (word.endsWith("oes") || word.endsWith("ches") || word.endsWith("shes")
                || word.endsWith("sses") || word.endsWith("xes")) {
            return word.substring(0, word.length() - 2);       // tomatoes -> tomato
        }
        if (word.endsWith("s")) {
            return word.substring(0, word.length() - 1);       // eggs -> egg
        }
        return word;
    }

    /**
     * Converts a quantity into its base unit so different units can be compared.
     */
    public static BaseQuantity toBase(double quantity, String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        switch (u) {
            case "g":
            case "gram":
            case "grams":
                return new BaseQuantity(quantity, "g");
            case "kg":
            case "kilogram":
            case "kilograms":
                return new BaseQuantity(quantity * 1000, "g");
            case "ml":
            case "millilitre":
            case "millilitres":
                return new BaseQuantity(quantity, "ml");
            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return new BaseQuantity(quantity * 1000, "ml");
            case "pcs":
            case "pc":
            case "piece":
            case "pieces":
                return new BaseQuantity(quantity, "pcs");
            default:
                return new BaseQuantity(quantity, u); // unknown unit: only matches itself
        }
    }
}