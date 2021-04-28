package com.foodmgmt.dontstarve;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class DailyMenu extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_DontStarve);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String regno = intent.getStringExtra("regno");

        Boolean isVerified = intent.getBooleanExtra("verification",false);
        Boolean isOnboardingDone = intent.getBooleanExtra("onboarding",false);

        setContentView(R.layout.menu_activity);
    }

}
