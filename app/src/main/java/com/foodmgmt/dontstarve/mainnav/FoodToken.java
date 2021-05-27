package com.foodmgmt.dontstarve.mainnav;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.foodmgmt.dontstarve.MainMenu;
import com.foodmgmt.dontstarve.R;
import com.foodmgmt.dontstarve.Verify;

public class FoodToken extends Fragment {
    private View view;
    private String name, regno, email;
    private MainMenu mm;
    private boolean isVerified;
    private Button vB,vB2;
    private Handler handlerAnim;
    private ImageView iv0, iv1, iv2;
    private static boolean click = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handlerAnim = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_food_token, container, false);

            iv0 = view.findViewById(R.id.vimageView);
            iv1 = view.findViewById(R.id.vimageView2);
            iv2 = view.findViewById(R.id.vimageView3);
            vB = view.findViewById(R.id.vbutton);
            vB2 = view.findViewById(R.id.vbuttonDuplicate);

            mm = (MainMenu) getActivity();
            name = mm.name;
            regno = mm.regno;
            email = mm.email;
            isVerified = mm.isVerified;
        }

        if (isVerified) {
            if (!mm.nfcPresent || mm.nfcAdapter == null) {
                vB.setVisibility(View.GONE);
                vB2.setVisibility(View.GONE);
                TextView tv = view.findViewById(R.id.nvText);
                tv.setVisibility(View.VISIBLE);
                tv.setText("NFC not present in the device");
            }
            else {
                vB.setOnClickListener(v -> {
                    if (!mm.nfcAdapter.isEnabled()) {
                        Toast.makeText(mm, "Please enable NFC.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                        return;
                    }
                    else {
                        vB.setVisibility(View.GONE);
                        mm.startNFC();

                        iv0.setVisibility(View.VISIBLE);
                        iv1.setVisibility(View.VISIBLE);
                        iv2.setVisibility(View.VISIBLE);

                        iv0.setAlpha(1f);
                        iv1.setAlpha(1f);
                        iv2.setAlpha(1f);

                        anime.run();
                    }
                    click = !click;
                });
            }
        } else {
            vB.setVisibility(View.GONE);
            vB2.setVisibility(View.VISIBLE);
            vB2.setOnClickListener(v -> {
                Intent myIntent = new Intent(getActivity(), Verify.class);
                myIntent.putExtra("name", mm.name);
                myIntent.putExtra("email", mm.email);
                myIntent.putExtra("regno", mm.regno);
                startActivity(myIntent);
            });
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        super.onDestroyView();
    }

    private Runnable anime = new Runnable() {
        @Override
        public void run() {
            // Start Transmitting NFC
            mm.nfcAdapter.setNdefPushMessage(mm.createMsg("RegNo: " + regno + "\nName: " + name), mm);

            iv0.animate().scaleX(2.25f).scaleY(2.25f).alpha(0f).setDuration(1800).withEndAction(() -> {
                iv0.setScaleX(0f);
                iv0.setScaleY(0f);
                iv0.setAlpha(1f);
            });
            iv1.animate().scaleX(2.25f).scaleY(2.25f).alpha(0f).setDuration(1800).withEndAction(() -> {
                iv1.setScaleX(0f);
                iv1.setScaleY(0f);
                iv1.setAlpha(1f);
            });
            iv2.animate().scaleX(2.25f).scaleY(2.25f).alpha(0f).setDuration(1800).withEndAction(() -> {
                iv2.setScaleX(0f);
                iv2.setScaleY(0f);
                iv2.setAlpha(1f);
            });

            handlerAnim.postDelayed(anime, 2000);
        }
    };
}