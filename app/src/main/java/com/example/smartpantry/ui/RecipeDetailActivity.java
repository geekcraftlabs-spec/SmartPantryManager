package com.example.smartpantry.ui;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.data.PantryDbHelper;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

/**
 * Shows a single recipe's full ingredient list and method.
 * Reads the recipe id from the Intent and loads it from SQLite.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        if (recipeId <= 0) {
            finish();
            return;
        }

        PantryDbHelper db = PantryDbHelper.getInstance(this);
        Recipe recipe = db.getRecipe(recipeId);
        if (recipe == null) {
            finish();
            return;
        }

        setTitle(recipe.getName());

        TextView textName = findViewById(R.id.text_recipe_name);
        LinearLayout container = findViewById(R.id.container_ingredients);
        TextView textMethod = findViewById(R.id.text_method);

        textName.setText(recipe.getName());
        container.removeAllViews();

        for (RecipeIngredient ing : recipe.getIngredients()) {
            TextView tv = new TextView(this);
            tv.setText("• " + ing.getDisplayLine());
            tv.setTextSize(15);
            container.addView(tv);
        }

        textMethod.setText(recipe.getInstructions());
    }
}