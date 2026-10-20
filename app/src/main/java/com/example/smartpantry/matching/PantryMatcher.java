package com.example.smartpantry.matching;

import com.example.smartpantry.model.Ingredient;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The core business logic of the app.
 *
 * A recipe qualifies as a "strict match" (shown under "You can cook these now")
 * only if the pantry contains AT LEAST the required quantity of EVERY ingredient
 * in the recipe - name matched after normalisation, quantity compared in base
 * units within the same dimension.
 *
 * A recipe with exactly one missing (or insufficient) ingredient lands in the
 * "Almost There" list. Two or more missing ingredients and the recipe is not
 * shown at all.
 */
public final class PantryMatcher {

    private PantryMatcher() { }

    public static MatchResult match(List<Recipe> recipes, List<Ingredient> pantry) {
        // Group the pantry by normalised ingredient name so multiple entries for
        // the same ingredient (e.g. "200 g flour" and "1 kg flour") sum correctly.
        Map<String, List<IngredientNormalizer.NormalizedQuantity>> pantryByName =
                new HashMap<>();
        if (pantry != null) {
            for (Ingredient ing : pantry) {
                String key = IngredientNormalizer.normalizeName(ing.getName());
                IngredientNormalizer.NormalizedQuantity nq =
                        IngredientNormalizer.normalizeQuantity(
                                ing.getQuantity(), ing.getUnit());
                pantryByName.computeIfAbsent(key, k -> new ArrayList<>()).add(nq);
            }
        }

        List<Recipe> strict = new ArrayList<>();
        List<Recipe> almost = new ArrayList<>();

        if (recipes == null) {
            return new MatchResult(strict, almost);
        }

        for (Recipe recipe : recipes) {
            int missing = 0;
            for (RecipeIngredient need : recipe.getIngredients()) {
                String key = IngredientNormalizer.normalizeName(need.getName());
                IngredientNormalizer.NormalizedQuantity required =
                        IngredientNormalizer.normalizeQuantity(
                                need.getQuantity(), need.getUnit());

                if (!hasEnough(pantryByName.get(key), required)) {
                    missing++;
                    if (missing > 1) {
                        break;
                    }
                }
            }

            if (missing == 0) {
                strict.add(recipe);
            } else if (missing == 1) {
                recipe.setMissingCount(1);
                almost.add(recipe);
            }
        }

        return new MatchResult(strict, almost);
    }

    /** True if the pantry has at least the required quantity in the same dimension. */
    private static boolean hasEnough(
            List<IngredientNormalizer.NormalizedQuantity> availableForName,
            IngredientNormalizer.NormalizedQuantity required) {

        if (availableForName == null || availableForName.isEmpty()) {
            return false;
        }

        double total = 0d;
        boolean sameDimension = false;
        for (IngredientNormalizer.NormalizedQuantity nq : availableForName) {
            if (nq.dimension.equals(required.dimension)) {
                total += nq.baseQuantity;
                sameDimension = true;
            }
        }
        return sameDimension && total >= required.baseQuantity;
    }
}