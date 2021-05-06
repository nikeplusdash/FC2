package com.foodmgmt.dontstarve.mainnav;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.foodmgmt.dontstarve.R;

public class DailyMenu extends Fragment {
    DailyMenuHandler dmh;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dmh = new DailyMenuHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) view = inflater.inflate(R.layout.fragment_daily_menu, container, false);

        Button b = view.findViewById(R.id.button2);
        TextView tv = view.findViewById(R.id.textView6);
        tv.setText(dmh.getCount() + "");
        b.setOnClickListener(v1 -> {
            dmh.increment();
            tv.setText(dmh.getCount() + "");
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        super.onDestroyView();
    }

    public static class DailyMenuHandler {
        public static int count;

        static {
            count = 0;
        }

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }
}