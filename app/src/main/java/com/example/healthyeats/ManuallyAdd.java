package com.example.healthyeats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/*
 * Allows user to manually input their own food item.
 * Make a copy of their item into the database, then adds the item onto their
 * meal's list.
 */

public class ManuallyAdd extends AppCompatActivity {
    Button addBtn;
    EditText etName, etCalorie, etServings, etBrand;
    DatabaseHelper peopleDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_add);

        addBtn = (Button) findViewById(R.id.addManualItem);
        etName = (EditText) findViewById(R.id.inputName);
        etBrand = (EditText) findViewById(R.id.inputBrand);
        etCalorie = (EditText) findViewById(R.id.inputCal);
        etServings = (EditText) findViewById(R.id.inputServings);

        peopleDB = new DatabaseHelper(this);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputName = etName.getText().toString();
                String inputBrand = etBrand.getText().toString();
                String calories = etCalorie.getText().toString();
                String servings = etServings.getText().toString();

                if(inputName.equals("") || calories.equals("") || inputBrand.equals("")) {
                    Toast.makeText(ManuallyAdd.this, "Please enter fields above.", Toast.LENGTH_LONG).show();
                }else{
                    try{
                        int cal = Integer.parseInt(calories);
                        int ser = 0;
                        if(servings.equals("")){
                            ser = 1;
                        }else{
                            ser = Integer.parseInt(servings);
                        }
                        Cursor data = peopleDB.insertmFood(inputName, inputBrand, cal);
                        String id = "";
                        if(data.getCount() == 0){
                            System.out.println("Not inputed");
                        }else{
                            data.moveToFirst();
                            id = data.getString(0);
                            insertToList(id, ser);
                        }
                    }catch(NumberFormatException nfe){
                        Toast.makeText(ManuallyAdd.this, "Error enter a number please.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void insertToList(String id, int serving){
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
                map.put(listType, new ArrayList<String>(Arrays.asList(id)));
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
                lBreakfast.add(id);
                bServing.add(Integer.toString(serving));
            }else if(listType.equals("lunch")){
                lLunch.add(id);
                lServing.add(Integer.toString(serving));
            }else if(listType.equals("dinner")){
                lDinner.add(id);
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
        Intent backHome = new Intent(ManuallyAdd.this, HomePage.class);
        startActivity(backHome);
    }
}