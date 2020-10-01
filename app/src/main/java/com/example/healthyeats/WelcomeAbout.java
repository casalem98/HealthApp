package com.example.healthyeats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/*
 * Ask's the user for personalized information in order to get their calorie goal.
 */
public class WelcomeAbout extends AppCompatActivity  {

    Button bSave;
    int tActivity, tGoal, tGender;
    EditText etAge, etWeight, etFeet, etInches;

    DatabaseHelper peopleDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_about);

        peopleDB = new DatabaseHelper(this);

        bSave = (Button) findViewById(R.id.btnSave);
        etAge = (EditText) findViewById(R.id.inAge);
        etWeight = (EditText) findViewById(R.id.inWeight);
        etFeet = (EditText) findViewById(R.id.inFeet);
        etInches = (EditText) findViewById(R.id.inInches);

        Spinner spinnerActivity = findViewById(R.id.spinner1);
        Spinner spinnerGoal = findViewById(R.id.spinner2);
        Spinner spinnerGender = findViewById(R.id.spinner3);

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
                tGender = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        inputInfo();
    }

    public void inputInfo() {
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String age = etAge.getText().toString();
                int ageNum = 0;
                String weight = etWeight.getText().toString();
                int weightNum = 0;
                String feet = etFeet.getText().toString();
                int feetNum = 0;
                String in = etInches.getText().toString();
                int inchNum = 0;

                if(age.equals("")) {
                    Toast.makeText(WelcomeAbout.this, "Please input age", Toast.LENGTH_LONG).show();
                }else if(weight.equals("")) {
                    Toast.makeText(WelcomeAbout.this, "Please input weight", Toast.LENGTH_LONG).show();
                }else if(feet.equals("")) {
                    Toast.makeText(WelcomeAbout.this, "Please input height in feet", Toast.LENGTH_LONG).show();
                }else if(in.equals("")) {
                    Toast.makeText(WelcomeAbout.this, "Please input height in inches", Toast.LENGTH_LONG).show();
                }else{
                    try{
                        ageNum = Integer.parseInt(age.trim());
                        weightNum = Integer.parseInt(weight.trim());
                        feetNum = Integer.parseInt(feet.trim());
                        inchNum = Integer.parseInt(in.trim());
                        inchNum = (12*feetNum) + inchNum;

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WelcomeAbout.this);
                        String userID = preferences.getString("pref_userID", "n/a");

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
                            
                            Intent logging_on = new Intent(WelcomeAbout.this, HomePage.class);
                            startActivity(logging_on);
                        }else{
                            Toast.makeText(WelcomeAbout.this, "Unsuccessful add", Toast.LENGTH_LONG).show();
                        }

                    }catch (NumberFormatException nfe){
                        Toast.makeText(WelcomeAbout.this, "Please enter numbers", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }


}