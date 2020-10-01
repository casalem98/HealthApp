package com.example.healthyeats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/*
 * Allows the user to view their current calorie goals for the day.
 * Allows the user to see their current list of food throughout the current day,
 */

public class HomePage extends AppCompatActivity {

    DatabaseHelper peopleDB;
    Button btnAddBreakfast, btnAddLunch, btnAddDinner, btnEditBreakfast, btnEditLunch, btnEditDinner;
    TextView mCalories, mCaloriesLeft, mBreakfast, mLunch, mDinner;
    String dayList;
    int totalCalories, leftCalories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        btnAddBreakfast = (Button) findViewById(R.id.breakfastBtn);
        btnAddLunch = (Button) findViewById(R.id.lunchAddBtn);
        btnAddDinner = (Button) findViewById(R.id.dinnerAddBtn);

        btnEditBreakfast = (Button) findViewById(R.id.breakfastEditBtn);
        btnEditLunch = (Button) findViewById(R.id.lunchEditBtn);
        btnEditDinner = (Button) findViewById(R.id.dinnerEditBtn);

        mCalories = (TextView) findViewById(R.id.calInfoText);
        mCaloriesLeft = (TextView) findViewById(R.id.calLeftNum);
        mBreakfast = (TextView) findViewById(R.id.breakfastOut);
        mLunch = (TextView) findViewById(R.id.lunchOut);
        mDinner = (TextView) findViewById(R.id.dinnerOut);

        peopleDB = new DatabaseHelper(this);

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
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(),About.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = preferences.getString("pref_userID", "n/a");
        int calGoal = preferences.getInt("pref_calGoal", 0);

        boolean dataMade = peopleDB.checkDateData(userID, date);
        if(dataMade){
            dayList = peopleDB.getList(userID,date);
            ParsingHelper helper = new ParsingHelper();

            showList(helper.getArrayList(dayList, "breakfast"), helper.getArrayList(dayList, "bSer"), mBreakfast);
            showList(helper.getArrayList(dayList, "lunch"), helper.getArrayList(dayList, "lSer"), mLunch);
            showList(helper.getArrayList(dayList, "dinner"), helper.getArrayList(dayList, "dSer"), mDinner);
        }

        btnPress(btnAddBreakfast, "breakfast", "bSer", true);
        btnPress(btnAddLunch, "lunch", "lSer", true);
        btnPress(btnAddDinner, "dinner", "dSer", true);
        btnPress(btnEditBreakfast,"breakfast", "bSer", false);
        btnPress(btnEditLunch,"lunch", "lSer", false);
        btnPress(btnEditDinner,"dinner", "dSer", false);
        mCalories.append(totalCalories + "");
        leftCalories = calGoal - totalCalories;
        mCaloriesLeft.append(leftCalories + "");

        peopleDB.addSuccess(userID, date, calSuccess(totalCalories, calGoal));
        
    }

    public void btnPress(Button mBtn, final String list_type, final String serving_type, final boolean add){
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomePage.this);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("list_type", list_type);
                edit.putString("list_servings", serving_type);
                edit.commit();
                if (add){
                    Intent addIntent = new Intent(HomePage.this, AddFoodItems.class);
                    startActivity(addIntent);
                }else{
                    edit.putString("day_list", dayList);
                    edit.commit();
                    Intent editIntent = new Intent(HomePage.this, EditFoodItems.class);
                    startActivity(editIntent);
                }
            }
        });
    }


    public void showList(ArrayList<String> mealList, ArrayList<String> servingList, TextView mText) {
        mText.setText("");
        for(int i = 0; i < mealList.size(); i++) {
            String code = mealList.get(i);
            int serving = Integer.parseInt(servingList.get(i));
            Cursor item = peopleDB.findItem(code);

            if (item.getCount() == 0) {
                Cursor mItem = peopleDB.findmItem(code);
                if(mItem.getCount() != 0){
                    System.out.println("Found Manual Input");
                    mItem.moveToFirst();
                    mText.append(mItem.getString(0) + " | " + mItem.getString(1) + " | " + mItem.getString(2) + " Calories per serving");
                    mText.append("\n");
                    int calories = Integer.parseInt(mItem.getString(2)) * serving;
                    mText.append(serving + " Servings taken, " + calories + " Total");
                    mText.append("\n");
                    totalCalories += calories;
                }else{
                    System.out.println("Error No Data Found.");
                }
            } else {
                item.moveToFirst();
                mText.append(item.getString(0) + " | " + item.getString(1) + " | " + item.getString(2) + " Calories per serving");
                mText.append("\n");
                int calories = Integer.parseInt(item.getString(2)) * serving;
                mText.append(serving + " Servings taken, " + calories + " Total");
                mText.append("\n");
                totalCalories += calories;
            }

        }

        if(mealList.size() != 0){
            String currentValue = mText.getText().toString();
            String newValue = currentValue.substring(0, currentValue.lastIndexOf("\n"));
            mText.setText(newValue);
        }

    }

    public int calSuccess(int totalCal, int goalCal){
        double upperBound = (goalCal + (goalCal * 0.15) + goalCal);
        double lowerBound = goalCal - (goalCal * 0.15);

        double upperBound2 = (goalCal + (goalCal * 0.25) + goalCal);
        double lowerBound2 = goalCal - (goalCal * 0.25);

        if((totalCal - goalCal) == 0){
            return 0;
        }else if(upperBound >= totalCal && totalCal >= lowerBound){
            return 1;
        }else if(upperBound2 >= totalCal && totalCal >= lowerBound2){
            return 2;
        }else{
            return 3;
        }

    }


}