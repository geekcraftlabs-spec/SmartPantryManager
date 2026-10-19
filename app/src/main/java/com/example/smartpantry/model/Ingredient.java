package com.example.smartpantry.model;

/**
 * A single row in the user's pantry (the "ingredients" table).
 */
public class Ingredient {

    private long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate; // ISO yyyy-MM-dd, or null if not set

    public Ingredient() { }

    public Ingredient(String name, double quantity, String unit, String expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public Ingredient(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    /** 3.0 displays as "3", 3.5 displays as "3.5". */
    public String getQuantityText() {
        if (!Double.isInfinite(quantity) && quantity == Math.rint(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    /** e.g. "3 count", "200 g" - used by the pantry list row. */
    public String getQuantityWithUnit() {
        return getQuantityText() + " " + unit;
    }
}