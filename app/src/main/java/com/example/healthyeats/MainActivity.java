package com.example.healthyeats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
/*
 * Displays the home screen when app is launched.
 * Redirects the user to either the homepage or login page.
 */

public class MainActivity extends AppCompatActivity {

    private RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                boolean loggedOn = preferences.getBoolean("pref_loggedIn", false);
                if(loggedOn){
                    Intent homeIntent = new Intent(MainActivity.this, HomePage.class);
                    startActivity(homeIntent);
                }else{
                    Intent loginIntent = new Intent(MainActivity.this, Login.class);
                    startActivity(loginIntent);
                }

            }
        });
    }

}