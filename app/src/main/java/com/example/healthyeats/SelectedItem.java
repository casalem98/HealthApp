package com.example.healthyeats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/*
 * Allows the user to insert the specific selected item to their personalize list.
 */

public class SelectedItem extends AppCompatActivity {

    DatabaseHelper peopleDB;
    TextView foodName, calInfo;
    EditText numServing;
    Button addBtn;
    int serving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_item);

        //Bundle b = getIntent().getExtras();
        Food item = (Food) getIntent().getSerializableExtra("sampleObj");

        foodName = (TextView) findViewById(R.id.foodName);
        calInfo = (TextView) findViewById(R.id.calorieInfo);
        numServing = (EditText) findViewById(R.id.servingInput);
        addBtn = (Button) findViewById(R.id.btnAdd);
        serving = 0;

        peopleDB = new DatabaseHelper(this);

        foodName.setText(item.name);
        calInfo.setText("Calories per serving : " + item.calories);

        addData(item.ID, item.name, item.brand, item.code, item.calories);

    }

    public void addData(final String ID, final String name, final String brand, final String code, final int cal){
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = numServing.getText().toString();
                boolean inserted = false;
                if (num.equals("")) {
                    serving = 1;
                    inserted = peopleDB.insertFood(ID, name, brand, code, cal);
                }else{
                    try{
                        serving = Integer.parseInt(num.trim());
                        inserted = peopleDB.insertFood(ID, name, brand, code, cal);
                    }catch (NumberFormatException nfe){
                        Toast.makeText(SelectedItem.this, "Please enter a number", Toast.LENGTH_LONG).show();
                        inserted = false;
                    }
                }

                if(inserted == true){
                    Toast.makeText(SelectedItem.this, "Item added to database!", Toast.LENGTH_LONG).show();
                    insertToList(ID);
                }else{
                    Toast.makeText(SelectedItem.this, "Error in adding to database", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void insertToList(String itemID) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = preferences.getString("pref_userID", "n/a");
        String listType = preferences.getString("list_type", "n/a");
        String listServing = preferences.getString("list_servings", "n/a");

        boolean dataMade = peopleDB.checkDateData(userID, date);

        if (!dataMade) {
            boolean confirm = peopleDB.newDate(userID, date);
            if(confirm){
                // userID and date not found, makes a new table with data
                System.out.println("New table made for date " + date);
                HashMap<String, ArrayList<String>> map = new HashMap<>();
                map.put("breakfast", new ArrayList<String>());
                map.put("lunch", new ArrayList<String>());
                map.put("dinner", new ArrayList<String>());
                map.put("bSer", new ArrayList<String>());
                map.put("lSer", new ArrayList<String>());
                map.put("dSer", new ArrayList<String>());
                map.put(listType, new ArrayList<String>(Arrays.asList(itemID)));
                map.put(listServing, new ArrayList<String>(Arrays.asList(Integer.toString(serving))));
                /*
                Gson gson = new Gson();
                String json = gson.toJson(map);
                 */
                JSONObject json = new JSONObject(map);
                System.out.println(json.toString());
                peopleDB.updateList(userID, date, json.toString());
            }else{
                System.out.println("Error in making data for date " + date );
            }
        }else{
            /*
            TO DO: take existing list and add foodID onto it
            Already an existing list for date, take that list and add onto it
             */
            String dayList = peopleDB.getList(userID,date);
            ParsingHelper parser = new ParsingHelper();
            ArrayList<String> lBreakfast = parser.getArrayList(dayList,"breakfast");
            ArrayList<String> lLunch = parser.getArrayList(dayList,"lunch");
            ArrayList<String> lDinner = parser.getArrayList(dayList,"dinner");
            ArrayList<String> bServing  = parser.getArrayList(dayList,"bSer");
            ArrayList<String> lServing = parser.getArrayList(dayList,"lSer");
            ArrayList<String> dServing = parser.getArrayList(dayList,"dSer");

            HashMap<String, ArrayList<String>> map = new HashMap<>();

            if(listType.equals("breakfast")){
                lBreakfast.add(itemID);
                bServing.add(Integer.toString(serving));
            }else if(listType.equals("lunch")){
                lLunch.add(itemID);
                lServing.add(Integer.toString(serving));
            }else if(listType.equals("dinner")){
                lDinner.add(itemID);
                dServing.add(Integer.toString(serving));
            }

            map.put("breakfast", lBreakfast);
            map.put("lunch", lLunch);
            map.put("dinner", lDinner);
            map.put("bSer", bServing);
            map.put("lSer", lServing);
            map.put("dSer", dServing);

            JSONObject json = new JSONObject(map);
            System.out.println(json.toString());
            peopleDB.updateList(userID, date, json.toString());
        }
        Intent backHome = new Intent(SelectedItem.this, HomePage.class);
        startActivity(backHome);
    }

}