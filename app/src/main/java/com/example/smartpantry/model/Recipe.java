package com.example.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe and everything it needs. missingCount is filled in by PantryMatcher
 * and is only used for the "Almost There" list.
 */
public class Recipe {

    private long id;
    private String name;
    private String instructions;
    private List<RecipeIngredient> ingredients = new ArrayList<>();
    private int missingCount = 0;

    public Recipe() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = (ingredients == null) ? new ArrayList<>() : ingredients;
    }

    public int getMissingCount() { return missingCount; }
    public void setMissingCount(int missingCount) { this.missingCount = missingCount; }

    /** "5 ingredients" / "1 ingredient" - used by the recipe list row. */
    public String getIngredientCountText() {
        int n = ingredients.size();
        return n + (n == 1 ? " ingredient" : " ingredients");
    }
}