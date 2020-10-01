package com.example.healthyeats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/*
 * Basic login function that vertifies the user's made account.
 */

public class Login extends AppCompatActivity {
    DatabaseHelper peopleDB;

    Button btnLogin, btnRegister;
    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        peopleDB = new DatabaseHelper(this);

        etUsername = (EditText) findViewById(R.id.txtUsername);
        etPassword = (EditText) findViewById(R.id.txtInPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        Register();
        Login();
    }

    public void Register() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(Login.this, Register.class);
                startActivity(registerIntent);
            }
        });
    }

    public void Login() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String data = peopleDB.checkUser(email, password);
                if (!data.equals("")){
                    //UserInfo user = new UserInfo(data.getString(0), data.getString(1));
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString("pref_userID", data);
                    Cursor user = peopleDB.getUserInfo(data);
                    double calGoal = 0;
                    if(user.getCount() > 0){
                        user.moveToFirst();
                        int act = Integer.parseInt(user.getString(5));
                        int gen = Integer.parseInt(user.getString(2));
                        int weight = Integer.parseInt(user.getString(3));
                        int goal = Integer.parseInt(user.getString(6));
                        int age =  Integer.parseInt(user.getString(1));
                        int height = Integer.parseInt(user.getString(4));

                        double BMR = 0;
                        ParsingHelper helper = new ParsingHelper();
                        if(gen == 0) {
                            BMR = 66 + (6.3 * weight) + (12.9 * height) - (6.8 * age);
                        }else{
                            BMR = 655 + (4.3 * weight) + (4.7 * height) - (4.7 * age);
                        }
                        BMR = helper.getActivityInput(BMR, act);
                        calGoal = helper.getCal(BMR, goal);
                    }
                    edit.putString("pref_userID", data);
                    edit.putInt("pref_calGoal", (int)calGoal);
                    edit.commit();

                    Intent HomePage = new Intent(Login.this, HomePage.class);
                    Toast.makeText(Login.this, "Welcome back!", Toast.LENGTH_LONG).show();
                    //HomePage.putExtra("userInfo", user);
                    startActivity(HomePage);
                }else{
                    Toast.makeText(Login.this, "Incorrect Username/Password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
