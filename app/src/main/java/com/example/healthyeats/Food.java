package com.example.healthyeats;

import androidx.annotation.NonNull;

import java.io.Serializable;

/*
 Food object used to store food when searching items.
 */

public class Food implements Serializable {
    public String name;
    public String ID;
    public String brand;
    public int calories;
    public String code;
    //public static String fat;

    public Food(String foodName, String foodID, String brandName, int cal){
        name = foodName;
        ID = foodID;
        brand = brandName;
        calories = cal;
        code = "0";

    }

    public void hasUPC(String UPC){
        code = UPC;
    }

    @NonNull
    @Override
    public String toString() {
        return name + "\nCalories per serving: " + calories;
    }

    public String getName() {
        return name;
    }

    public String getBrand() { return brand; }

    public String getCal() {
        return "Calories per serving : " + calories;
    }


}
