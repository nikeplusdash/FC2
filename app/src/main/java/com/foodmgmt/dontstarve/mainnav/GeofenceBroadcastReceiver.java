package com.foodmgmt.dontstarve.mainnav;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "Geofence";
    private String id;
    private static boolean flag;

    @Override
    public void onReceive(Context context, Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        id = intent.getExtras().getString("id");
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference crowd = database.getReference("crowd");

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                if (!flag) {
                    crowd.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                DataSnapshot data = task.getResult();
                                Long count = (Long) data.child("count").getValue();
                                ArrayList<String> people = (ArrayList<String>) data.child("people").getValue();
                                if (!people.contains(id)) {
                                    crowd.child("count").setValue(count + 1);
                                    people.add(id);
                                    crowd.child("people").setValue(people);
                                }
                                flag = true;
                            }
                        }
                    });
                }
                break;

            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                crowd.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("geofence enter", String.valueOf(task.getResult().getValue()));
                            DataSnapshot data = task.getResult();
                            Long count = (Long) data.child("count").getValue();
                            ArrayList<String> people = (ArrayList<String>) data.child("people").getValue();
                            if (!people.contains(id)) {
                                crowd.child("count").setValue(count + 1);
                                people.add(id);
                                crowd.child("people").setValue(people);
                            }
                        }
                    }
                });
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                crowd.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("geofence exit", String.valueOf(task.getResult().getValue()));
                            DataSnapshot data = task.getResult();
                            Long count = (Long) data.child("count").getValue();
                            ArrayList<String> people = (ArrayList<String>) data.child("people").getValue();
                            if (people.contains(id)) {
                                crowd.child("count").setValue(count - 1);
                                people.remove(id);
                                crowd.child("people").setValue(people);
                            }
                            flag = false;
                        }
                    }
                });
                break;
        }

    }
}