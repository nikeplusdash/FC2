package com.foodmgmt.dontstarve.onboarding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final ArrayList<Fragment> splashscreens;

    public ViewPagerAdapter(ArrayList<Fragment> screens, @NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.splashscreens = screens;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return splashscreens.get(position);
    }

    @Override
    public int getItemCount() {
        return splashscreens.size();
    }
}
