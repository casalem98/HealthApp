package com.example.healthyeats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/*
 * Basic account registration page that takes in the user's username, email, and password
 * and stores the information in the database.
 */

public class Register extends AppCompatActivity {
    DatabaseHelper peopleDB;

    Button btnBack, btnCreate;
    EditText etUser, etEmail, etPassword, etConfirmPass;

    ImageView img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        peopleDB = new DatabaseHelper(this);

        etUser = (EditText) findViewById(R.id.txtUsername);
        etEmail = (EditText) findViewById(R.id.txtEmail);
        etPassword = (EditText) findViewById(R.id.txtPassword);
        etConfirmPass = (EditText) findViewById(R.id.txtConfirmPass);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnCreate = (Button) findViewById(R.id.btnCreate);

        Back();
        CreateAccount();
    }

    public void Back() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(Register.this, Login.class);
                startActivity(backIntent);
            }
        });
    }

    public void CreateAccount() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUser.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String password2 = etConfirmPass.getText().toString();

                if (password.equals(password2)) {
                    int result = peopleDB.addUser(username, email, password);
                    if(result == 0) {
                        Toast.makeText(Register.this, "Email already in user", Toast.LENGTH_LONG).show();
                    }else if(result == 2){
                        Toast.makeText(Register.this, "Registration Successful!", Toast.LENGTH_LONG).show();
                        String data = peopleDB.checkUser(email, password);
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Register.this);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putString("pref_userID", data);
                        edit.putBoolean("pref_loggedIn",true);
                        edit.commit();

                        Intent moreInfo = new Intent(Register.this, WelcomeAbout.class);
                        startActivity(moreInfo);
                    }else{
                        Toast.makeText(Register.this, "Registration failed.", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Register.this, "Mismatch Passwords entered", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
