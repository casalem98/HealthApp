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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/*
 * About page of the user. Allows the user to edit information stored about themselves.
 * Allows user to log out of their account.
 */

public class About extends AppCompatActivity {

    DatabaseHelper peopleDB;
    TextView mtitle;
    EditText mAge, mWeight, mFt, mIn;
    Spinner spinnerActivity, spinnerGoal, spinnerGender;
    Button btnSave, btnChange, btnLogOut;
    int tActivity, tGoal, tGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        peopleDB = new DatabaseHelper(this);

        mtitle = (TextView) findViewById(R.id.username);
        mAge = (EditText) findViewById(R.id.ageUser);
        mWeight = (EditText) findViewById(R.id.weightUser);
        mFt = (EditText) findViewById(R.id.feetUser);
        mIn = (EditText) findViewById(R.id.inchUser);

        btnSave = (Button) findViewById(R.id.editBtn);
        btnChange = (Button) findViewById(R.id.changeBtn);
        btnLogOut = (Button) findViewById(R.id.logOutBtn);

        spinnerActivity = (Spinner) findViewById(R.id.activityUser);
        spinnerGoal = (Spinner) findViewById(R.id.goalUser);
        spinnerGender = (Spinner) findViewById(R.id.genderUser);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.activities, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.goals, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerActivity.setAdapter(adapter1);
        spinnerGoal.setAdapter(adapter2);
        spinnerGender.setAdapter(adapter3);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(About.this);
        final String userID = preferences.getString("pref_userID", "n/a");
        System.out.println(userID);
        info(userID);

        spinnerActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tActivity = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        spinnerGoal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tGoal = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tGender = position;;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(userID);
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = preferences.edit();
                edit.clear();
                edit.commit();
                Intent logIn = new Intent(About.this, Login.class);
                startActivity(logIn);

            }
        });

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // set home selected
        bottomNavigationView.setSelectedItemId(R.id.about);

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
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomePage.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }


    public void info(String userID){
        Cursor cursor = peopleDB.getUserInfo(userID);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            mtitle.setText(cursor.getString(0));
            mAge.setHint(cursor.getString(1));
            mWeight.setHint(cursor.getString(3));
            System.out.println(cursor.getString(4));
            int in = Integer.parseInt(cursor.getString(4));
            mFt.setHint(in/12 + " ft.");
            mIn.setHint(in%12 + " in.");
            int num = Integer.parseInt(cursor.getString(5));
            int num2 = Integer.parseInt(cursor.getString(6));
            int num3 = Integer.parseInt(cursor.getString(2));

            spinnerActivity.setSelection(num);
            spinnerGoal.setSelection(num2);
            spinnerGender.setSelection(num3);
        }else{
            Toast.makeText(About.this, "No data found", Toast.LENGTH_LONG).show();
        }

    }

    public void update(String userID){
        String age = mAge.getText().toString();
        int ageNum = 0;
        String weight = mWeight.getText().toString();
        int weightNum = 0;
        String feet = mFt.getText().toString();
        int feetNum = 0;
        String in = mIn.getText().toString();
        int inchNum = 0;

        if(age.equals("")) {
            ageNum = Integer.parseInt(mAge.getHint().toString());
        }else{
            try{
                weightNum = Integer.parseInt(weight.trim());
            } catch (NumberFormatException nfe){
                Toast.makeText(About.this, "Please enter a number", Toast.LENGTH_LONG).show();
            }
        }
        if(weight.equals("")) {
            weightNum = Integer.parseInt(mWeight.getHint().toString());
        }else{
            try{
                ageNum = Integer.parseInt(age.trim());
            } catch (NumberFormatException nfe){
                Toast.makeText(About.this, "Please enter a number", Toast.LENGTH_LONG).show();
            }
        }
        if(feet.equals("")) {
            String str = mFt.getHint().toString();
            feetNum =  Integer.parseInt(str.replaceAll("[^0-9]", ""));
        }else{
            try{
                feetNum = Integer.parseInt(feet.trim());
            } catch (NumberFormatException nfe){
                Toast.makeText(About.this, "Please enter a number", Toast.LENGTH_LONG).show();
            }
        }
        if(in.equals("")) {
            String str = mIn.getHint().toString();
            inchNum = Integer.parseInt(str.replaceAll("[^0-9]", ""));
        }else{
            try{
                inchNum = Integer.parseInt(in.trim());
                inchNum = (12*feetNum) + inchNum;
            } catch (NumberFormatException nfe){
                Toast.makeText(About.this, "Please enter a number", Toast.LENGTH_LONG).show();
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(About.this);
        boolean updated = peopleDB.addInfo(userID, ageNum, tGender, weightNum, inchNum, tActivity, tGoal);

        double BMR = 0;
        double calGoal = 0;
        ParsingHelper helper = new ParsingHelper();
        if(tGender == 0) {
            BMR = 66 + (6.3 * weightNum) + (12.9 * inchNum) - (6.8 * ageNum);
        }else{
            BMR = 655 + (4.3 * weightNum) + (4.7 * inchNum) - (4.7 * ageNum);
        }

        BMR = helper.getActivityInput(BMR, tActivity);
        calGoal = helper.getCal(BMR, tGoal);

        if(updated){
            SharedPreferences.Editor edit = preferences.edit();
            edit.putInt("pref_calGoal", (int)calGoal);
            edit.commit();
            Toast.makeText(About.this, "Update Success!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(About.this, "Update Unsuccessful", Toast.LENGTH_LONG).show();
        }


    }

}