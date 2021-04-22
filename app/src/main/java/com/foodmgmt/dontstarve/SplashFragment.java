package com.foodmgmt.dontstarve;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class SplashFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        new Handler().postDelayed(() -> {
            Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_viewPagerFragment);
        }, 700);
        return view;
    }
}