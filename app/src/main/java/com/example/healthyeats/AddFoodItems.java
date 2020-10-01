package com.example.healthyeats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Allows the user to search for a specific item through the use of utritionix API
 * with either a key word or UPC.
 * Also allows the user to manually search for an item.
 */

public class AddFoodItems extends AppCompatActivity {
    private RequestQueue queue;
    Button btnSearch, btnAddManually;
    EditText txtFood, txtUPC;
    TextView mfoodResult;
    //ListView itemsLists;
    //ArrayList<String> printOuts;
    ArrayList<Food> foodList;
    RelativeLayout mainLayout;
    boolean foundItems;
    int page;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_items);

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // set home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        // perform item selected
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.calendar:
                        startActivity(new Intent(getApplicationContext(),CalendarPage.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(),About.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomePage.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
        mainLayout = (RelativeLayout) findViewById(R.id.addLayout);
        queue = Volley.newRequestQueue(this);
        btnSearch = (Button) findViewById(R.id.searchBtn);
        btnAddManually = (Button) findViewById(R.id.manualBtn);
        txtFood = (EditText) findViewById(R.id.foodNameInput);
        txtUPC = (EditText) findViewById(R.id.inputUPC);
        mfoodResult = (TextView) findViewById(R.id.searchedTxt);
        foodList = new ArrayList<Food>();
        //printOuts = new ArrayList<String>();

        //itemsLists.setVisibility(View.GONE);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodName = txtFood.getText().toString();
                String UPC = txtUPC.getText().toString();

                if(foodName.matches("")){
                    if(UPC.matches("")){
                        Toast.makeText(AddFoodItems.this, "No food entered", Toast.LENGTH_SHORT).show();
                    }else{
                        StringRequest stringRequest = searchJson(UPC, 1);
                        queue.add(stringRequest);
                    }
                }else{
                    StringRequest stringRequest = searchJson(foodName, 0);
                    queue.add(stringRequest);
                }
                /*

                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                 */
            }
        });

        btnAddManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manuallyAdd = new Intent(AddFoodItems.this, ManuallyAdd.class);
                startActivity(manuallyAdd);
            }
        });

        /*
        System.out.println("Item!");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,printOuts);
        //itemsLists.setVisibility(View.VISIBLE);
        itemsLists.setAdapter(arrayAdapter);
        */


    }


    private StringRequest searchJson(final String userInput, final int codeNumber) {
        final String API_KEY = "&appKey=bcffddd3047d37e0b438774f68ccd6b3";
        final String APP_ID = "&appId=92d457a6";
        String PREF = "";
        String URL_PREFIX = "";
        String SEARCH_INPUT = "";
        if (codeNumber == 0) {
            SEARCH_INPUT = userInput.replaceAll(" ", "%20");
            URL_PREFIX = "https://api.nutritionix.com/v1_1/search/";
            PREF = "?results=0:20&fields=item_name,brand_name,item_id,nf_calories,nf_total_fat";
        } else {
            SEARCH_INPUT = userInput;
            URL_PREFIX = "https://api.nutritionix.com/v1_1/item?upc=";
        }
        final String url = URL_PREFIX + SEARCH_INPUT + PREF + APP_ID + API_KEY;
        return new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // try/catch block for returned JSON data
                // see API's documentation for returned format
                try {

                    JSONObject obj = new JSONObject(response);
                    System.out.println(obj.toString());

                    if(codeNumber == 1){
                        String foodName = obj.getString("item_name");
                        int calories = obj.getInt("nf_calories");
                        String id = obj.getString("item_id");
                        String brandName = obj.getString("brand_name");
                        Food item = new Food(foodName, id, brandName, calories);
                        item.hasUPC(userInput);

                        Intent intentUPC = new Intent(AddFoodItems.this, SelectedItem.class);
                        intentUPC.putExtra("sampleObj", item);
                        startActivity(intentUPC);
                    }else{
                        JSONArray items = obj.getJSONArray("hits");
                        //System.out.println("ARRAY MADE");
                        ArrayList<Food> searchResults = new ArrayList<Food>();
                        for (int i = 0; i < items.length(); i++) {
                            //System.out.println("GOING THROUGH OBJS");
                            JSONObject it = items.getJSONObject(i).getJSONObject("fields");
                            //System.out.println(items.getJSONObject(i).toString());
                            String foodName = it.getString("item_name");
                            //System.out.println(foodName);
                            String brandName = it.getString("brand_name");
                            int calories = it.getInt("nf_calories");
                            //System.out.println(calories);
                            String id = it.getString("item_id");
                            //System.out.println(id);
                            Food holder = new Food(foodName, id, brandName, calories);
                            System.out.println(holder.toString());
                            searchResults.add(holder);
                        }

                        Intent intentResults = new Intent(AddFoodItems.this, SearchedList.class);
                        intentResults.putExtra("searched_foods", (Serializable) searchResults);
                        startActivity(intentResults);
                    }

                    Toast.makeText(AddFoodItems.this, "Searched! " + userInput, Toast.LENGTH_LONG).show();
                    /*
                    JSONArray arr = obj.getJSONArray("hits");

                    JSONObject food = arr.getJSONObject(0);
                    System.out.println(food.toString());

                    JSONObject foodInfo = food.getJSONObject("food");
                    String foodID = foodInfo.getString("foodId");
                    String name = foodInfo.getString("label");

                    JSONObject nutrients = foodInfo.getJSONObject("nutrients");
                    int calories = nutrients.getInt("ENERC_KCAL");
                    foodList.add(new Food(name, foodID, calories));
                    String input = name + " Calories: " + calories;

                    */


                    // catch for the JSON parsing error
                } catch (JSONException e) {
                    Toast.makeText(AddFoodItems.this, "Error in Parsing", Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // display a simple message on the screen
                        Toast.makeText(AddFoodItems.this, "Food source is not responding (USDA API)", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
