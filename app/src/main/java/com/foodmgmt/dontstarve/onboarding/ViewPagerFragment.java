package com.foodmgmt.dontstarve.onboarding;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.foodmgmt.dontstarve.R;
import com.foodmgmt.dontstarve.onboarding.screens.IntroScreen1;
import com.foodmgmt.dontstarve.onboarding.screens.IntroScreen2;
import com.foodmgmt.dontstarve.onboarding.screens.IntroScreen3;
import com.foodmgmt.dontstarve.onboarding.screens.Register;
import com.foodmgmt.dontstarve.onboarding.screens.Verify;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewPagerFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        ArrayList<Fragment> aF = new ArrayList<>();
        aF.add(new IntroScreen1());
        aF.add(new IntroScreen2());
        aF.add(new IntroScreen3());
        ViewPagerAdapter vpa = new ViewPagerAdapter(aF, requireActivity().getSupportFragmentManager(), getLifecycle());
        ViewPager2 vp2 = view.findViewById(R.id.pager);
        vp2.setOffscreenPageLimit(3);
        vp2.setAdapter(vpa);
        return view;
    }
}