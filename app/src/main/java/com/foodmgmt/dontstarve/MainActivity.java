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

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private boolean isOnboardingDone;
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

        SharedPreferences sp = getSharedPreferences("dontstarve", Context.MODE_PRIVATE);
        if(sp.contains("onboarding")) isOnboardingDone = sp.getBoolean("onboarding",false);
        if(isOnboardingDone) {
            Intent myIntent = new Intent(this, MainMenu.class);
            myIntent.putExtra("name", sp.getString("name",""));
            myIntent.putExtra("email", sp.getString("email",""));
            myIntent.putExtra("regno", sp.getString("regno",""));
            myIntent.putExtra("verification", sp.getBoolean("verification",false));
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
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
        setContentView(R.layout.activity_main);
    }
}