package com.example.healthyeats;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/*
 * Array adapter for food items for user's search.
 */

public class AdapterFood extends ArrayAdapter<Food> {
    private Activity activity;
    private ArrayList<Food> lFood;
    private static LayoutInflater inflater = null;

    public AdapterFood(Activity activity, int textViewResourceId, ArrayList<Food> foodList) {
        super(activity, textViewResourceId, foodList);
        try{
            this.activity = activity;
            this.lFood = foodList;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {

        }
    }

    public int getCount() {
        return lFood.size();
    }

}