package com.example.smartpantry.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartpantry.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Simple settings screen persisted to SharedPreferences.
 */
public class SettingsFragment extends Fragment {

    public static final String PREFS_NAME = "smart_pantry_prefs";
    public static final String PREF_EXPIRY_ALERTS = "expiry_alerts";
    public static final String PREF_DEFAULT_UNIT = "default_unit";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialSwitch switchExpiry = view.findViewById(R.id.switch_expiry);
        TextInputEditText inputDefaultUnit = view.findViewById(R.id.input_default_unit);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_settings);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        boolean expiry = prefs.getBoolean(PREF_EXPIRY_ALERTS, true);
        String unit = prefs.getString(PREF_DEFAULT_UNIT, "g");

        switchExpiry.setChecked(expiry);
        inputDefaultUnit.setText(unit);

        btnSave.setOnClickListener(v -> {
            String newUnit = inputDefaultUnit.getText() == null
                    ? "" : inputDefaultUnit.getText().toString().trim();
            if (TextUtils.isEmpty(newUnit)) {
                newUnit = "g";
            }
            prefs.edit()
                    .putBoolean(PREF_EXPIRY_ALERTS, switchExpiry.isChecked())
                    .putString(PREF_DEFAULT_UNIT, newUnit)
                    .apply();
            Toast.makeText(requireContext(), R.string.settings_saved, Toast.LENGTH_SHORT).show();
        });
    }
}