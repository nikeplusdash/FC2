package com.foodmgmt.dontstarve;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    protected boolean isOnboardingDone, isVerified;
    protected String name, email, regno;

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_DontStarve);
        setContentView(R.layout.activity_main);

        // Accessing Shared Preferences to see if User has completed onboarding if not then continue to Onboarding Session
        SharedPreferences sp = getSharedPreferences("dontstarve", Context.MODE_PRIVATE);
        if (sp.contains("onboarding")) isOnboardingDone = sp.getBoolean("onboarding", false);
        if (isOnboardingDone) {
            name = sp.getString("name", "");
            email = sp.getString("email", "");
            regno = sp.getString("regno", "");
            isVerified = sp.getBoolean("verification", false);

            // Bundle up variables and sending to backend
            Intent myIntent = new Intent(this, MainMenu.class);
            myIntent.putExtra("name", name);
            myIntent.putExtra("email", email);
            myIntent.putExtra("regno", regno);
            myIntent.putExtra("verification", isVerified);
            startActivity(myIntent);
            finish();
        }

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}