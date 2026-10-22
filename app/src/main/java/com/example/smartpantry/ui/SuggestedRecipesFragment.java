package com.example.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.adapters.RecipeAdapter;
import com.example.smartpantry.data.PantryDbHelper;
import com.example.smartpantry.matching.MatchResult;
import com.example.smartpantry.matching.PantryMatcher;
import com.example.smartpantry.model.Ingredient;
import com.example.smartpantry.model.Recipe;

import java.util.List;

/**
 * Runs the strict matcher against the current pantry and displays two clearly
 * separated lists: "You can cook these now" (strict) and "Almost there"
 * (missing exactly one ingredient). Refreshes on resume so adding/removing an
 * ingredient in the Pantry tab is reflected immediately.
 */
public class SuggestedRecipesFragment extends Fragment implements RecipeAdapter.Listener {

    private PantryDbHelper db;
    private RecipeAdapter strictAdapter;
    private RecipeAdapter almostAdapter;
    private RecyclerView recyclerStrict, recyclerAlmost;
    private TextView headerStrict, headerAlmost, textEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggested, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = PantryDbHelper.getInstance(requireContext());

        recyclerStrict = view.findViewById(R.id.recycler_strict);
        recyclerAlmost = view.findViewById(R.id.recycler_almost);
        headerStrict = view.findViewById(R.id.header_strict);
        headerAlmost = view.findViewById(R.id.header_almost);
        textEmpty = view.findViewById(R.id.text_empty);

        strictAdapter = new RecipeAdapter(this);
        almostAdapter = new RecipeAdapter(this);
        recyclerStrict.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerAlmost.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerStrict.setAdapter(strictAdapter);
        recyclerAlmost.setAdapter(almostAdapter);

        reload();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (db != null) reload();
    }

    private void reload() {
        if (db == null) return;

        List<Recipe> allRecipes = db.getAllRecipes();
        List<Ingredient> pantry = db.getAllIngredients();
        MatchResult result = PantryMatcher.match(allRecipes, pantry);

        boolean strictEmpty = result.getStrictMatches().isEmpty();
        boolean almostEmpty = result.getAlmostMatches().isEmpty();

        strictAdapter.setItems(result.getStrictMatches());
        almostAdapter.setItems(result.getAlmostMatches());

        headerStrict.setVisibility(strictEmpty ? View.GONE : View.VISIBLE);
        recyclerStrict.setVisibility(strictEmpty ? View.GONE : View.VISIBLE);
        headerAlmost.setVisibility(almostEmpty ? View.GONE : View.VISIBLE);
        recyclerAlmost.setVisibility(almostEmpty ? View.GONE : View.VISIBLE);

        textEmpty.setVisibility((strictEmpty && almostEmpty) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}