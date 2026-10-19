package com.example.smartpantry.model;

/**
 * One required ingredient of a recipe (a row in "recipe_ingredients").
 * Kept separate from Ingredient because a recipe requirement has no id/expiry.
 */
public class RecipeIngredient {

    private final String name;
    private final double quantity;
    private final String unit;

    public RecipeIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }

    public String getQuantityText() {
        if (quantity == Math.rint(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    public String getDisplayLine() {
        return getQuantityText() + " " + unit + " " + name;
    }
}