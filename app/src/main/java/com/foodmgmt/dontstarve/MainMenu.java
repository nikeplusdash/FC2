package com.foodmgmt.dontstarve;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.foodmgmt.dontstarve.mainnav.DailyMenu;
import com.foodmgmt.dontstarve.mainnav.FoodToken;
import com.foodmgmt.dontstarve.mainnav.Statistics;
import com.fxn.BubbleTabBar;
import com.fxn.OnBubbleClickListener;

public class MainMenu extends AppCompatActivity {
    final int foodID = R.id.food, dailyID = R.id.daily, statsID = R.id.stats;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_DontStarve);
        setContentView(R.layout.menu_activity);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String regno = intent.getStringExtra("regno");

        Boolean isVerified = intent.getBooleanExtra("verification", false);
        Boolean isOnboardingDone = intent.getBooleanExtra("onboarding", false);

        // Add periodic fetch to see if user is verified & update if it is

        BubbleTabBar bar = findViewById(R.id.bubbleTabBar);
        bar.addBubbleListener(new OnBubbleClickListener() {
            @Override
            public void onBubbleClick(int i) {
                FragmentManager fm = getSupportFragmentManager();
                int frag = -1;
                Fragment f = null;
                switch (i) {
                    case foodID:
                        frag = 0;
                        f = new FoodToken();
                        break;
                    case dailyID:
                        frag = 1;
                        f = new DailyMenu();
                        break;
                    case statsID:
                        frag = 2;
                        f = new Statistics();
                        break;
                    default:
                        System.out.println("None?");
                        break;
                }
                if (frag != -1) {
                    /**
                     * A Decent way to control state in fragments is to know lifecycle and declare static variables and classes, also look up fragment lifecycle
                     *
                     * https://developer.android.com/guide/fragments/lifecycle
                     * https://vinodpattanshetti49.medium.com/fragment-lifecycle-while-doing-add-and-replace-6a3f084364af
                     * https://stackoverflow.com/questions/55610266/how-to-save-instance-state-of-fragment-after-using-replace
                     */
                    fm.beginTransaction().replace(R.id.container2, f).commit();
                } else {
                    System.out.println("EHHHHH?????");
                }
            }
        });
    }

}
