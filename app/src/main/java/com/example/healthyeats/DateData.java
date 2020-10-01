package com.example.healthyeats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
/*
 * Allows the user to view a list from a specific selected date.
 */

public class DateData extends AppCompatActivity {

    DatabaseHelper peopleDB;
    TextView mDate, mCal, mBreakfast, mLunch, mDinner, titleB, titleL, titleD;
    Button btnB, btnL, btnD;
    ImageView imSleep, imSuccess;
    int totalCal, calCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_data);

        peopleDB = new DatabaseHelper(this);

        mDate = (TextView) findViewById(R.id.currentDateTxt);
        mCal = (TextView) findViewById(R.id.txtCalories);

        mBreakfast = (TextView) findViewById(R.id.breakfastList);
        mLunch = (TextView) findViewById(R.id.lunchList);
        mDinner = (TextView) findViewById(R.id.dinnerList);

        titleB = (TextView) findViewById(R.id.breakfastTitle);
        titleL = (TextView) findViewById(R.id.lunchTitle);
        titleD = (TextView) findViewById(R.id.dinnerTitle);

        btnB = (Button) findViewById(R.id.breakfastButton);
        btnL = (Button) findViewById(R.id.lunchButton);
        btnD = (Button) findViewById(R.id.dinnerButton);

        imSleep = (ImageView) findViewById(R.id.unicornSleep);
        imSuccess = (ImageView) findViewById(R.id.imgSuccess);

        mBreakfast.setVisibility(View.GONE);
        mLunch.setVisibility(View.GONE);
        mDinner.setVisibility(View.GONE);

        totalCal = 0;
        calCounter = 0;

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // set home selected
        bottomNavigationView.setSelectedItemId(R.id.calendar);

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

        Intent incomingIntent = getIntent();
        String date = incomingIntent.getStringExtra("date");

        mDate.setText(date);
        getInfo();
        showMore(btnB, mBreakfast);
        showMore(btnL, mLunch);
        showMore(btnD, mDinner);


    }

    public void getInfo() {
        Intent incomingIntent = getIntent();
        String date = incomingIntent.getStringExtra("date");

        mDate.setText(date);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = preferences.getString("pref_userID", "n/a");

        boolean hasData = peopleDB.checkDateData(userID, date);

        if(hasData){
            String dayList = peopleDB.getList(userID,date);
            ParsingHelper helper = new ParsingHelper();

            showList(helper.getArrayList(dayList, "breakfast"), helper.getArrayList(dayList, "bSer"), mBreakfast);
            showList(helper.getArrayList(dayList, "lunch"), helper.getArrayList(dayList, "lSer"), mLunch);
            showList(helper.getArrayList(dayList, "dinner"), helper.getArrayList(dayList, "dSer"), mDinner);

            mCal.setText("Total Calories intake " + totalCal);
            int suc = Integer.parseInt(peopleDB.getSuccess(userID,date));
            if(suc == 0) {
                imSuccess.setImageDrawable(getResources().getDrawable(R.drawable.unicorn_good_job));
            }else if(suc == 1){
                imSuccess.setImageDrawable(getResources().getDrawable(R.drawable.unicorn_thumbs_up));
            }else if(suc == 2){
                imSuccess.setImageDrawable(getResources().getDrawable(R.drawable.unicorn_good_job));
            }else{
                System.out.println(suc);
                imSuccess.setImageDrawable(getResources().getDrawable(R.drawable.unicorn_sad));
            }


        }else{
            mCal.setText("No data recorded for this day.");
            imSuccess.setVisibility(View.GONE);
            imSleep.setVisibility(View.VISIBLE);
            btnB.setVisibility(View.GONE);
            btnL.setVisibility(View.GONE);
            btnD.setVisibility(View.GONE);
            titleB.setVisibility(View.GONE);
            titleL.setVisibility(View.GONE);
            titleD.setVisibility(View.GONE);
        }
    }

    public void showMore(final Button btn, final TextView txtView){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button)v;
                String text = b.getText().toString();

                if(text.equals("View More")) {
                    btn.setText("Hide");
                    txtView.setVisibility(View.VISIBLE);
                }else{
                    btn.setText("View More");
                    txtView.setVisibility(View.GONE);
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

            if(item.getCount() == 0) {
                Cursor data = peopleDB.findmItem(code);
                if(data.getCount() == 0){
                    System.out.println("Error No Data Found.");
                    imSleep.setVisibility(View.VISIBLE);
                }else{
                    data.moveToFirst();
                    mText.append(data.getString(0) + " | " + data.getString(1) + " | " + data.getString(2) + " Calories per serving");
                    mText.append("\n");
                    int calories = Integer.parseInt(data.getString(2)) * serving;
                    mText.append(serving + " Servings taken, " + calories + " Total");
                    mText.append("\n");
                    totalCal += calories;
                }
            }else{
                item.moveToFirst();
                mText.append(item.getString(0) + " | " + item.getString(1) + " | " + item.getString(2) + " Calories per serving");
                mText.append("\n");
                int calories = Integer.parseInt(item.getString(2)) * serving;
                mText.append(serving + " Servings taken, " + calories + " Total");
                mText.append("\n");
                totalCal += calories;
            }

        }

        if(mealList.size() == 0){
            mText.setText("Nothing noted.");
        }else{
            String currentValue = mText.getText().toString();
            String newValue = currentValue.substring(0, currentValue.lastIndexOf("\n"));
            mText.setText(newValue);
        }

    }


}