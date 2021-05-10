package com.foodmgmt.dontstarve.mainnav;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import static android.nfc.NdefRecord.createMime;

public class FoodToken extends Fragment {
    private View view;
    private String name, regno, email;
    private MainMenu mm;
    private boolean isVerified;
    private Button vB;
    private Handler handlerAnim;
    private ImageView iv0,iv1,iv2;
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
            mm = (MainMenu) getActivity();
            name = mm.name;
            regno =  mm.regno;
            email =  mm.email;
            isVerified =  mm.isVerified;
        }
        if(isVerified) {
            vB.setOnClickListener(v -> {
                if(!mm.nfcPresent) return;
                if(!mm.nfcAdapter.isEnabled()) {
                    Toast.makeText(mm, "Please enable NFC.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                    return;
                }
                if (!click) {
                    vB.setText("Stop");
                    mm.startNFC();
                    iv0.setVisibility(View.VISIBLE);
                    iv1.setVisibility(View.VISIBLE);
                    iv2.setVisibility(View.VISIBLE);
                    anime.run();
                } else {
                    vB.setText("Start");
                    mm.stopNFC();
                    handlerAnim.removeCallbacks(anime);
                }
                click = !click;
            });
        } else {
            vB.setVisibility(View.GONE);
            TextView tv = view.findViewById(R.id.nvText);
            tv.setVisibility(View.VISIBLE);
        }
        if(click) {
            vB.setText("Stop");
            mm.startNFC();
            iv0.setVisibility(View.VISIBLE);
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv0.setAlpha(1f);
            iv1.setAlpha(1f);
            iv2.setAlpha(1f);
            anime.run();
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
            mm.nfcAdapter.setNdefPushMessage(mm.createMsg("RegNo: " + regno+"\nName: "+name),mm);
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
            handlerAnim.postDelayed(anime,2000);
        }
    };
}