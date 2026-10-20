package com.example.smartpantry.matching;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Normalises ingredient names and units so that simple real-world variations
 * ("tomato" vs "tomatoes", "g" vs "grams", "kg" vs "g") compare correctly.
 *
 * Not a full NLP solution - a small alias map plus a singularisation heuristic
 * covers the cases the app actually needs.
 */
public final class IngredientNormalizer {

    private IngredientNormalizer() { }

    /** Dimensions used by the matcher. */
    public static final String DIMENSION_MASS = "mass";
    public static final String DIMENSION_VOLUME = "volume";
    public static final String DIMENSION_COUNT = "count";

    /**
     * Holds a quantity converted to a base unit within a dimension:
     *   mass   -> grams
     *   volume -> millilitres
     *   count  -> number of items
     */
    public static final class NormalizedQuantity {
        public final String dimension;
        public final double baseQuantity;

        public NormalizedQuantity(String dimension, double baseQuantity) {
            this.dimension = dimension;
            this.baseQuantity = baseQuantity;
        }

        @Override
        public String toString() {
            return baseQuantity + " " + dimension;
        }
    }

    /** Simple alias map - keys and values must both be in normalised form. */
    private static final Map<String, String> ALIASES = new HashMap<>();
    static {
        ALIASES.put("aubergine", "eggplant");
        ALIASES.put("courgette", "zucchini");
        ALIASES.put("coriander", "cilantro");
        ALIASES.put("prawn", "shrimp");
        ALIASES.put("rocket", "arugula");
        ALIASES.put("garbanzo", "chickpea");
        ALIASES.put("capsicum", "bell pepper");
        ALIASES.put("soya sauce", "soy sauce");
    }

    // ------------------------------------------------------------------
    // Name normalisation
    // ------------------------------------------------------------------

    /**
     * Lowercases, trims, strips a trailing s/es/ies, and resolves aliases.
     * "Tomatoes" -> "tomato", "aubergine" -> "eggplant".
     */
    public static String normalizeName(String raw) {
        if (raw == null) {
            return "";
        }
        String n = raw.toLowerCase(Locale.ROOT).trim();
        n = n.replaceAll("[,\\.\\s]+$", "").trim();
        if (n.isEmpty()) {
            return "";
        }

        String direct = ALIASES.get(n);
        if (direct != null) {
            return direct;
        }

        String singular = singularize(n);
        String viaAlias = ALIASES.get(singular);
        if (viaAlias != null) {
            return viaAlias;
        }
        return singular;
    }

    private static String singularize(String n) {
        if (n.length() <= 2) {
            return n;
        }
        if (n.endsWith("ies") && n.length() > 3) {
            return n.substring(0, n.length() - 3) + "y";
        }
        if (n.endsWith("oes") && n.length() > 3) {
            return n.substring(0, n.length() - 2);
        }
        if (n.endsWith("ches") || n.endsWith("shes")
                || n.endsWith("sses") || n.endsWith("xes")) {
            return n.substring(0, n.length() - 2);
        }
        if (n.endsWith("s") && !n.endsWith("ss")) {
            return n.substring(0, n.length() - 1);
        }
        return n;
    }

    // ------------------------------------------------------------------
    // Unit normalisation
    // ------------------------------------------------------------------

    /**
     * Converts (quantity, unit) into a NormalizedQuantity in base units.
     * Unrecognised units map to a "count" dimension so that strict matching
     * still works for items like "1 block cheese".
     */
    public static NormalizedQuantity normalizeQuantity(double quantity, String unit) {
        String u = unit == null ? "" : unit.toLowerCase(Locale.ROOT).trim();
        switch (u) {
            case "g":
            case "gram":
            case "grams":
                return new NormalizedQuantity(DIMENSION_MASS, quantity);

            case "kg":
            case "kilo":
            case "kilos":
            case "kilogram":
            case "kilograms":
                return new NormalizedQuantity(DIMENSION_MASS, quantity * 1000d);

            case "ml":
            case "millilitre":
            case "milliliter":
            case "millilitres":
            case "milliliters":
                return new NormalizedQuantity(DIMENSION_VOLUME, quantity);

            case "l":
            case "litre":
            case "liter":
            case "litres":
            case "liters":
                return new NormalizedQuantity(DIMENSION_VOLUME, quantity * 1000d);

            case "tsp":
            case "teaspoon":
            case "teaspoons":
                return new NormalizedQuantity(DIMENSION_VOLUME, quantity * 5d);

            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return new NormalizedQuantity(DIMENSION_VOLUME, quantity * 15d);

            case "cup":
            case "cups":
                return new NormalizedQuantity(DIMENSION_VOLUME, quantity * 240d);

            case "":
            case "count":
            case "counts":
            case "whole":
            case "item":
            case "items":
            case "piece":
            case "pieces":
            case "slice":
            case "slices":
            case "clove":
            case "cloves":
            case "pinch":
            case "pinches":
            case "can":
            case "cans":
            case "packet":
            case "packets":
                return new NormalizedQuantity(DIMENSION_COUNT, quantity);

            default:
                // Unknown unit: treat as countable so strict matching still works.
                return new NormalizedQuantity(DIMENSION_COUNT, quantity);
        }
    }
}