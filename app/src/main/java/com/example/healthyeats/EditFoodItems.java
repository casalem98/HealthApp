package com.example.healthyeats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/*
 *   Page used to list the items from a specific meal list.
 *   Allows the user to change the serving of and item or delete an item from their list.
 */
public class EditFoodItems extends AppCompatActivity {

    ListView mealItems;
    DatabaseHelper peopleDB;
    int serving, pos;
    ArrayList<String> foodName, brand, ID, calories, ser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_items);

        mealItems = (ListView) findViewById(R.id.listMeal);
        peopleDB = new DatabaseHelper(this);
        getList();

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
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomePage.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(),About.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }

    public void getList(){
        // Get's the list of items from meal type and preforms specific operations on it
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EditFoodItems.this);
        final String dayList = preferences.getString("day_list", "n/a");
        String lType = preferences.getString("list_type", "n/a");
        String lServing = preferences.getString("list_servings", "n/a");

        ParsingHelper helper = new ParsingHelper();

        final ArrayList<String> items = helper.getArrayList(dayList, lType);
        final ArrayList<String> ser = helper.getArrayList(dayList, lServing);

        System.out.println("Attempting to edit!");
        System.out.println(items.toString());
        System.out.println(ser.toString());

        List<HashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item, new String[]{"Name", "Brand", "Calories"},
                new int[]{R.id.name, R.id.brand, R.id.calories});

        for(int i = 0; i < items.size(); i++){
            HashMap<String, String> resultsMap = new HashMap<>();
            String id = items.get(i);
            int serving = Integer.parseInt(ser.get(i));
            Cursor item = peopleDB.findItem(id);

            if (item.getCount() == 0) {
                Cursor mItem = peopleDB.findmItem(id);
                if(mItem.getCount() != 0){
                    System.out.println("Found Manual Input");
                    mItem.moveToFirst();
                    resultsMap.put("Name", mItem.getString(0));
                    resultsMap.put("Brand", mItem.getString(1));
                    resultsMap.put("Calories", mItem.getString(2));
                    listItems.add(resultsMap);
                }else{
                    System.out.println("Error No Data Found.");
                }
            } else {
                item.moveToFirst();
                resultsMap.put("Name", item.getString(0));
                resultsMap.put("Brand", item.getString(1));
                resultsMap.put("Calories", item.getString(2));
                listItems.add(resultsMap);
            }
        }

        mealItems.setAdapter(adapter);

        mealItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;

                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


                Object clickedObject = parent.getItemAtPosition(position);
                // System.out.println(clickedObject);
                // System.out.println(position);
                String item = items.get(position);
                System.out.println(item);
                serving = Integer.parseInt(ser.get(position));
                pos = position;

                TextView product = (TextView) popupView.findViewById(R.id.productInfo);
                final EditText changeServing = (EditText) popupView.findViewById(R.id.changeSer);
                changeServing.setHint(serving + " servings");

                //View linearLayout = inflater.inflate(R.layout.)
                Button bEdit = (Button) popupView.findViewById(R.id.btnEdit);
                Button bDelete = (Button) popupView.findViewById(R.id.btnDelete);
                Button bBack = (Button) popupView.findViewById(R.id.backBtn);

                Cursor it = peopleDB.findItem(item);

                if (it.getCount() == 0) {
                    Cursor mItem = peopleDB.findmItem(item);
                    if(mItem.getCount() != 0){
                        System.out.println("Found Manual Input");
                        mItem.moveToFirst();
                        product.setText(mItem.getString(0));
                        product.append(" from " +mItem.getString(1) + "\n");
                        product.append("Calories per serving" + mItem.getString(2));
                    }else{
                        System.out.println("Error No Data Found.");
                    }
                } else {
                    it.moveToFirst();
                    product.setText(it.getString(0));
                    product.append("\n" +it.getString(1));
                    product.append("\nCalories per serving" + it.getString(2));
                }

                bEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Edits item's serving quantity
                        String result = changeServing.getText().toString();
                        if(!result.equals("")){
                            try{
                                int new_serving = Integer.parseInt(result);
                                editList(pos, dayList, new_serving);
                                Intent homePage = new Intent(EditFoodItems.this, HomePage.class);
                                startActivity(homePage);

                            }catch(NumberFormatException nfe){
                                Toast.makeText(EditFoodItems.this, "Please enter a number.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

                bDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    // Delete object from list
                    deleteList(pos, dayList);
                    Intent homePage = new Intent(EditFoodItems.this, HomePage.class);
                    startActivity(homePage);
                    }
                });

                bBack.setOnClickListener(new View.OnClickListener() {
                    // closes popup window
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });

            }
        });
    }

    public void editList(int position, String list, int serving){
        // Only can edit the current day's list
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = preferences.getString("pref_userID", "n/a");
        String listType = preferences.getString("list_type", "n/a");
        String listServing = preferences.getString("list_servings", "n/a");

        ParsingHelper parser = new ParsingHelper();
        ArrayList<String> lBreakfast = parser.getArrayList(list,"breakfast");
        ArrayList<String> lLunch = parser.getArrayList(list,"lunch");
        ArrayList<String> lDinner = parser.getArrayList(list,"dinner");
        ArrayList<String> bServing  = parser.getArrayList(list,"bSer");
        ArrayList<String> lServing = parser.getArrayList(list,"lSer");
        ArrayList<String> dServing = parser.getArrayList(list,"dSer");

        HashMap<String, ArrayList<String>> map = new HashMap<>();

        if(listType.equals("breakfast")){
            bServing.set(position, serving + "");
        }else if(listType.equals("lunch")){
            lServing.set(position, serving + "");
        }else if(listType.equals("dinner")){
            dServing.set(position, serving + "");
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

    public void deleteList(int position, String list){

        // Only can edit the current day's list
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = preferences.getString("pref_userID", "n/a");
        String listType = preferences.getString("list_type", "n/a");
        String listServing = preferences.getString("list_servings", "n/a");

        ParsingHelper parser = new ParsingHelper();
        ArrayList<String> lBreakfast = parser.getArrayList(list,"breakfast");
        ArrayList<String> lLunch = parser.getArrayList(list,"lunch");
        ArrayList<String> lDinner = parser.getArrayList(list,"dinner");
        ArrayList<String> bServing  = parser.getArrayList(list,"bSer");
        ArrayList<String> lServing = parser.getArrayList(list,"lSer");
        ArrayList<String> dServing = parser.getArrayList(list,"dSer");

        HashMap<String, ArrayList<String>> map = new HashMap<>();

        if(listType.equals("breakfast")){
            lBreakfast.remove(position);
            bServing.remove(position);
        }else if(listType.equals("lunch")){
            lLunch.remove(position);
            lServing.remove(position);
        }else if(listType.equals("dinner")){
            lDinner.remove(position);
            dServing.remove(position);
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

}