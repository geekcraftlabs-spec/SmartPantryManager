package com.example.smartpantry.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for the pantry list. Exposes edit and delete callbacks
 * so the fragment owning the list can decide how to handle them.
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.VH> {

    public interface Listener {
        void onEdit(Ingredient ingredient);
        void onDelete(Ingredient ingredient);
    }

    private final List<Ingredient> items = new ArrayList<>();
    private final Listener listener;

    public IngredientAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<Ingredient> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Ingredient ing = items.get(position);
        h.textName.setText(ing.getName());
        h.textQuantity.setText(ing.getQuantityWithUnit());
        h.btnEdit.setOnClickListener(v -> listener.onEdit(ing));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(ing));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textQuantity;
        final ImageButton btnEdit;
        final ImageButton btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_name);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}