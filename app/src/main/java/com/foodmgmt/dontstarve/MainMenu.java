package com.foodmgmt.dontstarve;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.foodmgmt.dontstarve.mainnav.DailyMenu;
import com.foodmgmt.dontstarve.mainnav.FoodToken;
import com.foodmgmt.dontstarve.mainnav.Statistics;
import com.fxn.BubbleTabBar;
import com.fxn.OnBubbleClickListener;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import static android.nfc.NdefRecord.createMime;

public class MainMenu extends AppCompatActivity{
    final int foodID = R.id.food, dailyID = R.id.daily, statsID = R.id.stats;
    public String name,regno,email;
    public boolean isVerified, isOnboardingDone, nfcPresent;
    public NfcAdapter nfcAdapter;
    public IntentFilter[] intentFiltersArray;
    public String[][] techListsArray;
    public PendingIntent pendingIntent;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_DontStarve);
        setContentView(R.layout.menu_activity);

        Bundle b = getIntent().getExtras();
        name = b.getString("name");
        email = b.getString("email");
        regno = b.getString("regno");
        isVerified = b.getBoolean("verification", false);
        isOnboardingDone = b.getBoolean("onboarding", false);

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

        // Add periodic fetch to see if user is verified & update if it is


        //  NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        PackageManager pm = getPackageManager();
        if (nfcAdapter == null) {
            Toast.makeText(this,"This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            nfcPresent = false;
        }
        else nfcPresent = true;
        if(!nfcAdapter.isEnabled() && nfcPresent){
            Toast.makeText(this, "Please enable NFC.", Toast.LENGTH_SHORT).show();
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[] {ndef, };
        techListsArray = new String[][] { new String[] { NfcA.class.getName() } };
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public void stopNFC() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    public void startNFC() {
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

    public NdefMessage createMsg(String payload) {
        Locale locale = new Locale("en");
        Boolean encodeInUtf8 = true;
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return new NdefMessage(record);
    }
}
