package com.example.smartpantry.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.adapters.IngredientAdapter;
import com.example.smartpantry.data.PantryDbHelper;
import com.example.smartpantry.model.Ingredient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Pantry list fragment: loads ingredients from SQLite, binds them to a
 * RecyclerView, and wires the FAB to the Add/Edit activity.
 */
public class PantryFragment extends Fragment implements IngredientAdapter.Listener {

    private PantryDbHelper db;
    private IngredientAdapter adapter;
    private RecyclerView recycler;
    private TextView textEmpty;

    private final ActivityResultLauncher<Intent> addEditLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            reload();
                        }
                    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = PantryDbHelper.getInstance(requireContext());

        recycler = view.findViewById(R.id.recycler_pantry);
        textEmpty = view.findViewById(R.id.text_empty);
        FloatingActionButton fab = view.findViewById(R.id.fab_add);

        adapter = new IngredientAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        fab.setOnClickListener(v -> openAddEdit(0));

        reload();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (db != null) reload();
    }

    private void reload() {
        if (db == null || adapter == null) return;
        List<Ingredient> items = db.getAllIngredients();
        adapter.setItems(items);
        boolean empty = items.isEmpty();
        textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void openAddEdit(long ingredientId) {
        Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
        if (ingredientId > 0) {
            intent.putExtra(AddEditIngredientActivity.EXTRA_INGREDIENT_ID, ingredientId);
        }
        addEditLauncher.launch(intent);
    }

    @Override
    public void onEdit(Ingredient ingredient) {
        openAddEdit(ingredient.getId());
    }

    @Override
    public void onDelete(Ingredient ingredient) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_message)
                .setPositiveButton(R.string.delete_yes, (d, w) -> {
                    db.deleteIngredient(ingredient.getId());
                    reload();
                })
                .setNegativeButton(R.string.delete_no, null)
                .show();
    }
}