package com.foodmgmt.dontstarve;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.foodmgmt.dontstarve.mainnav.DailyMenu;
import com.foodmgmt.dontstarve.mainnav.FoodToken;
import com.foodmgmt.dontstarve.mainnav.GeofenceHelper;
import com.foodmgmt.dontstarve.mainnav.Statistics;
import com.foodmgmt.dontstarve.onboarding.Users;
import com.fxn.BubbleTabBar;
import com.fxn.OnBubbleClickListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainMenu extends AppCompatActivity {
    private static final String TAG = "Geofence";
    final int foodID = R.id.food, dailyID = R.id.daily, statsID = R.id.stats;
    public String name, regno, email;
    public boolean isVerified, nfcPresent;
    public NfcAdapter nfcAdapter;
    public IntentFilter[] intentFiltersArray;
    public String[][] techListsArray;
    public PendingIntent pendingIntent;
    public int No_of_people = 0;
    LatLng FC2 = new LatLng(13.345614740234748, 74.7964322234682);
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private float GEOFENCE_RADIUS = 200;
    private String GEOFENCE_ID = "FC_2";
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private List<Geofence> geofenceList = new ArrayList<>();
    private PendingIntent geofencePendingIntent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_DontStarve);
        setContentView(R.layout.menu_activity);

        // Retrieve Bundle from Intent
        Bundle b = getIntent().getExtras();
        name = b.getString("name");
        email = b.getString("email");
        regno = b.getString("regno");
        isVerified = b.getBoolean("verification", false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Check if user was verified by Admin later
        DatabaseReference user = database.getReference();
        user.child("users").child(regno).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Users u = snapshot.getValue(Users.class);
                if (!isVerified)
                    Toast.makeText(MainMenu.this, "You have been verified", Toast.LENGTH_SHORT).show();
                SharedPreferences sp = getSharedPreferences("dontstarve", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("verification", u.getVerified());
                editor.commit();
                isVerified = u.getVerified();

                FragmentManager fm = getSupportFragmentManager();
                Fragment f = fm.findFragmentById(R.id.container2);
                fm.beginTransaction()
                        .detach(f)
                        .attach(f)
                        .commit();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });

        // Getting reference of crowd from Database
        DatabaseReference crowd = database.getReference("crowd");
        crowd.setValue(101);

        // Bottom Navigation Bar to respond accordingly
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

        //  NFC Adapter Implementation to check if NFC is present
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            nfcPresent = false;
        } else nfcPresent = true;
        if (nfcPresent && !nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC.", Toast.LENGTH_SHORT).show();
        }

        // Initiate an NFC Intent Handler
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            // Handles all MIME based dispatches.
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[]{ndef,};
        // Allowed NFC Device Types
        techListsArray = new String[][]{new String[]{NfcA.class.getName()}};

        // Permission Access for Background/Fine Location
        enableUserLocation();

        // Logging data change
        crowd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Integer value = dataSnapshot.getValue(Integer.class);
                No_of_people = value;
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                //mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission..

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                addGeofence(FC2, GEOFENCE_RADIUS);
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                        Toast.makeText(MainMenu.this, "Geofence created Successfully.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Toast.makeText(MainMenu.this, "Geofence Failed.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }

    // Disable NFC Intent Capture
    public void stopNFC() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    // Enable NFC Intent Capture
    public void startNFC() {
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

    // Sending User's NAME & REGNO to the NFC host detected nearby
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
