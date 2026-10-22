package com.example.smartpantry.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for recipe lists. Tapping a card fires a click callback
 * so the fragment can open RecipeDetailActivity.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VH> {

    public interface Listener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> items = new ArrayList<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<Recipe> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Recipe recipe = items.get(position);
        h.textName.setText(recipe.getName());
        h.textCount.setText(recipe.getIngredientCountText());
        h.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textCount;

        VH(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_recipe_name);
            textCount = itemView.findViewById(R.id.text_recipe_count);
        }
    }
}