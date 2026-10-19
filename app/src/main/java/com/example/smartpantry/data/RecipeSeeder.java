package com.example.smartpantry.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Inserts the starter recipe collection the first time the database is created.
 * Each seed ingredient is stored as "name|quantity|unit" and split on insert.
 */
public final class RecipeSeeder {

    private RecipeSeeder() { }

    private static final class SeedRecipe {
        final String name;
        final String instructions;
        final String[] ingredients;

        SeedRecipe(String name, String instructions, String... ingredients) {
            this.name = name;
            this.instructions = instructions;
            this.ingredients = ingredients;
        }
    }

    private static final SeedRecipe[] RECIPES = new SeedRecipe[]{

        new SeedRecipe("Scrambled Eggs",
            "1. Whisk the eggs with the milk and a pinch of salt.\n" +
            "2. Melt the butter in a non-stick pan over medium heat.\n" +
            "3. Pour in the egg mixture and stir gently until just set.\n" +
            "4. Serve immediately.",
            "eggs|3|count", "milk|30|ml", "butter|10|g"),

        new SeedRecipe("Cheese Toastie",
            "1. Butter the outside of both slices of bread.\n" +
            "2. Put the cheese between the slices, buttered sides facing out.\n" +
            "3. Fry in a dry pan for 2-3 minutes per side until golden.\n" +
            "4. Cut in half and serve hot.",
            "bread|2|slice", "cheese|40|g", "butter|10|g"),

        new SeedRecipe("Tomato Omelette",
            "1. Dice the tomato and onion finely.\n" +
            "2. Beat the eggs with salt.\n" +
            "3. Heat the oil and soften the onion for 2 minutes.\n" +
            "4. Add the tomato, then the eggs, and cook until set.\n" +
            "5. Fold and serve.",
            "eggs|3|count", "tomato|1|count", "onion|1|count", "oil|15|ml", "salt|1|tsp"),

        new SeedRecipe("Garlic Pasta",
            "1. Boil the pasta in salted water until al dente.\n" +
            "2. Gently warm the sliced garlic in the olive oil - do not brown it.\n" +
            "3. Drain the pasta and toss it through the garlic oil.\n" +
            "4. Season with salt and serve.",
            "pasta|200|g", "garlic|3|clove", "olive oil|30|ml", "salt|1|tsp"),

        new SeedRecipe("Tomato Pasta",
            "1. Boil the pasta until al dente.\n" +
            "2. Soften the onion and garlic in the olive oil.\n" +
            "3. Add the chopped tomatoes and simmer for 15 minutes.\n" +
            "4. Toss the sauce through the drained pasta and serve.",
            "pasta|200|g", "tomato|3|count", "onion|1|count", "garlic|2|clove", "olive oil|20|ml"),

        new SeedRecipe("Tuna Mayo Sandwich",
            "1. Drain the tuna and mix it with the mayonnaise.\n" +
            "2. Season lightly.\n" +
            "3. Spread the mixture between the two slices of bread.\n" +
            "4. Cut and serve.",
            "bread|2|slice", "tuna|100|g", "mayonnaise|30|ml"),

        new SeedRecipe("Vegetable Stir-Fry",
            "1. Slice the carrot and cut the broccoli into small florets.\n" +
            "2. Heat the oil in a wok over high heat.\n" +
            "3. Stir-fry the garlic for 30 seconds, then add the vegetables.\n" +
            "4. Add the soy sauce and cook for 4-5 minutes until tender-crisp.",
            "carrot|1|count", "broccoli|150|g", "soy sauce|30|ml", "oil|15|ml", "garlic|2|clove"),

        new SeedRecipe("Egg Fried Rice",
            "1. Cook the rice and let it cool.\n" +
            "2. Heat the oil and scramble the eggs.\n" +
            "3. Add the diced carrot and onion and stir-fry for 3 minutes.\n" +
            "4. Add the rice and soy sauce and fry for another 3 minutes.",
            "rice|200|g", "egg|2|count", "soy sauce|20|ml", "carrot|1|count", "onion|1|count"),

        new SeedRecipe("Banana Smoothie",
            "1. Peel the bananas and break them into chunks.\n" +
            "2. Blend with the milk and honey until smooth.\n" +
            "3. Pour into a glass and serve cold.",
            "banana|2|count", "milk|250|ml", "honey|15|ml"),

        new SeedRecipe("Greek Salad",
            "1. Chop the cucumber and tomatoes into chunks.\n" +
            "2. Crumble the feta over the vegetables.\n" +
            "3. Add the olives and drizzle with olive oil.\n" +
            "4. Toss gently and serve.",
            "cucumber|1|count", "tomato|2|count", "feta|100|g", "olive oil|20|ml", "olives|50|g"),

        new SeedRecipe("Chicken and Rice",
            "1. Dice the chicken and season it.\n" +
            "2. Brown the chicken in the oil, then set aside.\n" +
            "3. Soften the onion and garlic, add the rice and 400 ml water.\n" +
            "4. Return the chicken, cover and simmer for 20 minutes.",
            "chicken breast|300|g", "rice|200|g", "onion|1|count", "garlic|2|clove", "oil|15|ml"),

        new SeedRecipe("Pancakes",
            "1. Whisk the flour, milk, eggs and sugar into a smooth batter.\n" +
            "2. Melt a little butter in a pan.\n" +
            "3. Pour in a ladle of batter and cook for 2 minutes per side.\n" +
            "4. Repeat with the remaining batter and serve.",
            "flour|200|g", "milk|250|ml", "egg|2|count", "sugar|30|g", "butter|20|g"),

        new SeedRecipe("Grilled Cheese and Tomato",
            "1. Slice the tomato and pat it dry.\n" +
            "2. Butter the bread and layer cheese and tomato inside.\n" +
            "3. Grill for 3-4 minutes per side until golden.\n" +
            "4. Serve immediately.",
            "bread|2|slice", "cheese|50|g", "tomato|1|count", "butter|10|g"),

        new SeedRecipe("Lentil Soup",
            "1. Dice the carrot and onion.\n" +
            "2. Soften them with the garlic in a pot.\n" +
            "3. Add the lentils and stock and bring to a boil.\n" +
            "4. Simmer for 25 minutes until the lentils are soft.",
            "lentils|200|g", "carrot|1|count", "onion|1|count", "garlic|2|clove", "stock|500|ml"),

        new SeedRecipe("Bean Burrito",
            "1. Warm the tortillas in a dry pan.\n" +
            "2. Mash the beans with a little water and heat through.\n" +
            "3. Spoon beans, diced tomato and cheese onto each tortilla.\n" +
            "4. Roll up tightly and serve.",
            "tortilla|2|count", "beans|200|g", "cheese|50|g", "tomato|1|count"),

        new SeedRecipe("Mushroom Omelette",
            "1. Slice the mushrooms and fry them in the butter until golden.\n" +
            "2. Beat the eggs and pour them over the mushrooms.\n" +
            "3. Sprinkle with cheese and cook until set.\n" +
            "4. Fold and serve.",
            "eggs|3|count", "mushroom|100|g", "butter|10|g", "cheese|30|g"),

        new SeedRecipe("Peanut Butter Toast",
            "1. Toast the bread until golden.\n" +
            "2. Spread the peanut butter evenly.\n" +
            "3. Slice the banana on top and serve.",
            "bread|2|slice", "peanut butter|30|g", "banana|1|count"),

        new SeedRecipe("Yogurt Berry Bowl",
            "1. Spoon the yogurt into a bowl.\n" +
            "2. Top with the berries.\n" +
            "3. Drizzle with honey and serve.",
            "yogurt|200|g", "berries|100|g", "honey|15|ml"),

        new SeedRecipe("Egg Fried Noodles",
            "1. Cook the noodles according to the packet and drain.\n" +
            "2. Heat a little oil and scramble the eggs.\n" +
            "3. Add the grated carrot and noodles.\n" +
            "4. Stir in the soy sauce and toss for 2 minutes.",
            "noodles|200|g", "egg|2|count", "soy sauce|20|ml", "carrot|1|count"),

        new SeedRecipe("Potato Hash",
            "1. Dice the potatoes and boil them for 5 minutes.\n" +
            "2. Fry the onion in the oil until soft.\n" +
            "3. Add the potatoes and fry until crisp and golden.\n" +
            "4. Make two wells, crack in the eggs and cook until set.",
            "potato|3|count", "onion|1|count", "oil|20|ml", "egg|2|count")
    };

    /** Called from PantryDbHelper.onCreate(). Must run inside the same transaction. */
    public static void seed(SQLiteDatabase db) {
        for (SeedRecipe sr : RECIPES) {
            ContentValues recipeValues = new ContentValues();
            recipeValues.put(PantryDbHelper.COL_NAME, sr.name);
            recipeValues.put(PantryDbHelper.COL_INSTRUCTIONS, sr.instructions);
            long recipeId = db.insert(PantryDbHelper.TABLE_RECIPES, null, recipeValues);

            for (String line : sr.ingredients) {
                String[] parts = line.split("\\|");
                ContentValues ingValues = new ContentValues();
                ingValues.put(PantryDbHelper.COL_RECIPE_ID, recipeId);
                ingValues.put(PantryDbHelper.COL_ING_NAME, parts[0]);
                ingValues.put(PantryDbHelper.COL_QTY, Double.parseDouble(parts[1]));
                ingValues.put(PantryDbHelper.COL_UNIT, parts[2]);
                db.insert(PantryDbHelper.TABLE_RECIPE_INGREDIENTS, null, ingValues);
            }
        }
    }
}