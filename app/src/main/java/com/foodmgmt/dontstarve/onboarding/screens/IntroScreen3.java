package com.foodmgmt.dontstarve.onboarding.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.foodmgmt.dontstarve.R;
import com.squareup.picasso.Picasso;

public class IntroScreen3 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_intro_screen3, container, false);
        ImageView i = v.findViewById(R.id.imageView2);
        ImageButton ib = v.findViewById(R.id.button);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_viewPagerFragment_to_register);
            }
        });
        Picasso.get().load(R.drawable.slide3).into(i);
        return v;
    }

}