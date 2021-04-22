package com.foodmgmt.dontstarve.onboarding.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.foodmgmt.dontstarve.R;
import com.squareup.picasso.Picasso;

public class IntroScreen2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_intro_screen2, container, false);
        ImageView i = v.findViewById(R.id.imageView2);
        Picasso.get().load(R.drawable.slide2).into(i);
        return v;
    }
}