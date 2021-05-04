package com.foodmgmt.dontstarve;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainMenu extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_DontStarve);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String regno = intent.getStringExtra("regno");


        Boolean isVerified = intent.getBooleanExtra("verification",false);
        Boolean isOnboardingDone = intent.getBooleanExtra("onboarding",false);

        // Add periodic fetch to see if user is verified & update if it is

        setContentView(R.layout.menu_activity);
    }

}
