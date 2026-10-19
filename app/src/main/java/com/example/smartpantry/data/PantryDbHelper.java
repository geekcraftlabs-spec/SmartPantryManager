package com.example.smartpantry.data;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.Ingredient;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite persistence for the Smart Pantry Manager.
 *
 * Three tables:
 *   ingredients          - the user's pantry (full CRUD in commit #7)
 *   recipes              - the seeded recipe collection (read only)
 *   recipe_ingredients   - the required ingredients of each recipe (read only)
 *
 * A recipe can be matched against the pantry by joining recipe_ingredients
 * to ingredients on normalised ingredient name (see PantryMatcher).
 */
public class PantryDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_QTY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";
    public static final String COL_INSTRUCTIONS = "instructions";
    public static final String COL_RECIPE_ID = "recipe_id";
    public static final String COL_ING_NAME = "ingredient_name";

    private static PantryDbHelper instance;

    /** One shared instance per process; always uses the application context. */
    public static synchronized PantryDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PantryDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private PantryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QTY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, " +
                COL_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_INSTRUCTIONS + " TEXT NOT NULL)");

                db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_ING_NAME + " TEXT NOT NULL, " +
                COL_QTY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_RECIPE_ID + ") REFERENCES " +
                TABLE_RECIPES + "(" + COL_ID + ") ON DELETE CASCADE)");

        // Seed the starter recipe collection on first run.
        RecipeSeeder.seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        onCreate(db);
    }

    // ------------------------------------------------------------------
    // Recipes (read only)
    // ------------------------------------------------------------------

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null,
                null, null, COL_NAME + " COLLATE NOCASE ASC");
        try {
            while (cursor.moveToNext()) {
                Recipe recipe = readRecipe(cursor);
                recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));
                recipes.add(recipe);
            }
        } finally {
            cursor.close();
        }
        return recipes;
    }

    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null,
                COL_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
        try {
            if (cursor.moveToFirst()) {
                Recipe recipe = readRecipe(cursor);
                recipe.setIngredients(getIngredientsForRecipe(recipe.getId()));
                return recipe;
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    private Recipe readRecipe(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        recipe.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        recipe.setInstructions(cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)));
        return recipe;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null,
                COL_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeId)},
                null, null, COL_ID + " ASC");
        try {
            while (cursor.moveToNext()) {
                list.add(new RecipeIngredient(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ING_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT))));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

        // ------------------------------------------------------------------
    // Ingredient CRUD
    // ------------------------------------------------------------------

    public long insertIngredient(Ingredient ingredient) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, ingredient.getName());
        values.put(COL_QTY, ingredient.getQuantity());
        values.put(COL_UNIT, ingredient.getUnit());
        values.put(COL_EXPIRY, ingredient.getExpiryDate());
        return db.insert(TABLE_INGREDIENTS, null, values);
    }

    public int updateIngredient(Ingredient ingredient) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, ingredient.getName());
        values.put(COL_QTY, ingredient.getQuantity());
        values.put(COL_UNIT, ingredient.getUnit());
        values.put(COL_EXPIRY, ingredient.getExpiryDate());
        return db.update(TABLE_INGREDIENTS, values,
                COL_ID + " = ?", new String[]{String.valueOf(ingredient.getId())});
    }

    public int deleteIngredient(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_INGREDIENTS,
                COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Ingredient getIngredient(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_INGREDIENTS, null,
                COL_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return readIngredient(cursor);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public List<Ingredient> getAllIngredients() {
        List<Ingredient> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_INGREDIENTS, null, null, null,
                null, null, COL_NAME + " COLLATE NOCASE ASC");
        try {
            while (cursor.moveToNext()) {
                list.add(readIngredient(cursor));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    private Ingredient readIngredient(Cursor cursor) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        ingredient.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        ingredient.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QTY)));
        ingredient.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)));
        int expiryIndex = cursor.getColumnIndexOrThrow(COL_EXPIRY);
        if (!cursor.isNull(expiryIndex)) {
            ingredient.setExpiryDate(cursor.getString(expiryIndex));
        }
        return ingredient;
    }
}