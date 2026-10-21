package com.example.smartpantry.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.data.PantryDbHelper;
import com.example.smartpantry.model.Ingredient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Add or edit a pantry ingredient. Pass EXTRA_INGREDIENT_ID to edit;
 * omit it (or pass 0) to add. All fields validated before writing to the DB.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_INGREDIENT_ID = "ingredient_id";

    private PantryDbHelper db;
    private long editingId = 0;
    private TextInputLayout layoutName, layoutQuantity, layoutUnit, layoutExpiry;
    private TextInputEditText inputName, inputQuantity, inputUnit, inputExpiry;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        db = PantryDbHelper.getInstance(this);

        layoutName = findViewById(R.id.layout_name);
        layoutQuantity = findViewById(R.id.layout_quantity);
        layoutUnit = findViewById(R.id.layout_unit);
        layoutExpiry = findViewById(R.id.layout_expiry);
        inputName = findViewById(R.id.input_name);
        inputQuantity = findViewById(R.id.input_quantity);
        inputUnit = findViewById(R.id.input_unit);
        inputExpiry = findViewById(R.id.input_expiry);
        MaterialButton btnSave = findViewById(R.id.btn_save);
        MaterialButton btnCancel = findViewById(R.id.btn_cancel);

        editingId = getIntent().getLongExtra(EXTRA_INGREDIENT_ID, 0);
        if (editingId > 0) {
            setTitle(R.string.title_edit_ingredient);
            Ingredient existing = db.getIngredient(editingId);
            if (existing != null) {
                inputName.setText(existing.getName());
                inputQuantity.setText(existing.getQuantityText());
                inputUnit.setText(existing.getUnit());
                if (existing.getExpiryDate() != null) {
                    inputExpiry.setText(existing.getExpiryDate());
                }
            }
        } else {
            setTitle(R.string.title_add_ingredient);
        }

        btnSave.setOnClickListener(v -> attemptSave());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void attemptSave() {
        layoutName.setError(null);
        layoutQuantity.setError(null);
        layoutUnit.setError(null);
        layoutExpiry.setError(null);

        String name = text(inputName);
        String quantityStr = text(inputQuantity);
        String unit = text(inputUnit);
        String expiry = text(inputExpiry);

        boolean ok = true;

        if (TextUtils.isEmpty(name)) {
            layoutName.setError(getString(R.string.error_name));
            ok = false;
        }

        double quantity = 0;
        if (TextUtils.isEmpty(quantityStr)) {
            layoutQuantity.setError(getString(R.string.error_quantity));
            ok = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityStr);
                if (quantity <= 0) {
                    layoutQuantity.setError(getString(R.string.error_quantity));
                    ok = false;
                }
            } catch (NumberFormatException e) {
                layoutQuantity.setError(getString(R.string.error_quantity));
                ok = false;
            }
        }

        if (TextUtils.isEmpty(unit)) {
            layoutUnit.setError(getString(R.string.error_unit));
            ok = false;
        }

        if (!TextUtils.isEmpty(expiry)) {
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(expiry);
            } catch (ParseException e) {
                layoutExpiry.setError(getString(R.string.error_expiry));
                ok = false;
            }
        }

        if (!ok) return;

        Ingredient ingredient = new Ingredient(
                name, quantity, unit, TextUtils.isEmpty(expiry) ? null : expiry);

        if (editingId > 0) {
            ingredient.setId(editingId);
            db.updateIngredient(ingredient);
        } else {
            db.insertIngredient(ingredient);
        }

        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }

    private String text(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}