package com.example.healthyeats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/*
 * Used to help parse through JSON files contain items of food.
 */
public class ParsingHelper {

    public ParsingHelper(){
    }

    public ArrayList<String> getArrayList(String list, String meal) {
        ArrayList<String> foodIds = new ArrayList<>();
        try {
            System.out.println("Already data");
            JSONObject json = new JSONObject(list);
            System.out.println(json.toString());
            String items = json.getString(meal);
            System.out.println(items);
            JSONArray arr = json.getJSONArray(meal);
            //ArrayList<String> foodIds = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++){
                System.out.println(arr.getString(i));
                foodIds.add(arr.getString(i));
            }
        }catch (JSONException e) {
            System.out.println("Error in parsing ");
        }
        return foodIds;
    }

    public double getActivityInput(double BMR, int tActivity) {
        if(tActivity == 0){
            return BMR * 1.2;
        }else if(tActivity == 1){
            return BMR * 1.375;
        }else if(tActivity == 2){
            return BMR * 1.55;
        }else if(tActivity == 3){
            return BMR * 1.6375;
        }else if(tActivity == 4){
            return BMR * 1.725;
        }else{
            return BMR * 1.9;
        }
    }

    public double getCal(double BMR, int tGoal){
        if(tGoal == 1){
            return BMR - 250;
        }else if(tGoal == 2){
            return BMR - 500;
        }else if(tGoal == 3){
            return BMR + 250;
        }else if(tGoal == 4){
            return BMR + 500;
        }else{
            return BMR;
        }
    }
}
