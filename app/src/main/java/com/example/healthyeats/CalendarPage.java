package com.example.healthyeats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CalendarView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
/*
 * Shows the user a calendar where they can click on
 * a specific date to check their progress over time.
 */

public class CalendarPage extends AppCompatActivity {

    private static final String TAG = "CalendarPage";
    private CalendarView mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_page);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // set home selected
        bottomNavigationView.setSelectedItemId(R.id.calendar);

        // perform item selected
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomePage.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.calendar:
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(),About.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String sYear = String.valueOf(year);
                String sMonth = String.valueOf((month + 1));
                String sDay = String.valueOf(dayOfMonth);

                if (sDay.length() == 1) {
                    sDay = "0" + sDay;
                }
                if (sMonth.length() == 1){
                    sMonth = "0" + sMonth;
                }
                String date = sYear + "-" + sMonth + "-" + sDay;

                Intent intent = new Intent(CalendarPage.this, DateData.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });
    }
}