package com.foodmgmt.dontstarve.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.foodmgmt.dontstarve.R;
import com.foodmgmt.dontstarve.onboarding.screens.IntroScreen1;
import com.foodmgmt.dontstarve.onboarding.screens.IntroScreen2;
import com.foodmgmt.dontstarve.onboarding.screens.IntroScreen3;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

public class ViewPagerFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_pager, container, false);
        ArrayList<Fragment> aF = new ArrayList<>();
        aF.add(new IntroScreen1());
        aF.add(new IntroScreen2());
        aF.add(new IntroScreen3());
        ViewPagerAdapter vpa = new ViewPagerAdapter(aF, requireActivity().getSupportFragmentManager(), getLifecycle());
        ViewPager2 vp2 = v.findViewById(R.id.pager);
        vp2.setOffscreenPageLimit(2);
        vp2.setAdapter(vpa);

        DotsIndicator dotsIndicator = v.findViewById(R.id.worm_dots_indicator);
        dotsIndicator.setViewPager2(vp2);

        vp2.setPageTransformer((View view, float position) -> {
            int pageWidth = view.getWidth();
            ImageView iv = view.findViewById(R.id.imageView2);
            TextView tv = view.findViewById(R.id.txt);
            TextView stv = view.findViewById(R.id.subtxt);

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(1);
            } else if (position <= 1) { // [-1,1]
                iv.setTranslationX(Math.abs(position) * (pageWidth / 4f)); //Half the normal speed
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(1);
            }

            if (Math.abs(position) < 0.7) tv.setAlpha(1 - Math.abs(position) / 0.7f);
            else if (position < -1) tv.setAlpha(0);
            else tv.setAlpha(0);

            if (Math.abs(position) < 0.5) stv.setAlpha(1 - Math.abs(position) / 0.5f);
            else if (position < -1) stv.setAlpha(0);
            else stv.setAlpha(0);
        });
        return v;
    }
}