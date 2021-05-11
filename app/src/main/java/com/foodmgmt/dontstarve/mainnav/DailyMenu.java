package com.foodmgmt.dontstarve.mainnav;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.foodmgmt.dontstarve.R;
import com.loopeer.cardstack.AllMoveDownAnimatorAdapter;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.UpDownAnimatorAdapter;
import com.loopeer.cardstack.UpDownStackAnimatorAdapter;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DailyMenu extends Fragment implements CardStackView.ItemExpendListener{
    private View v;
    Integer[] color = {
            R.drawable.card_bg1,
            R.drawable.card_bg2,
            R.drawable.card_bg3,
            R.drawable.card_bg4,
    };

    String[] food_time = {"Breakfast","Lunch","Snacks","Dinner"};
    String[][] food_name = {{"Bread","Butter/Jam","Egg/Fruits","Tea/Coffee","Poha"},
                            {"Dal","Rice","Roti","Cauliflower","Rasam"},
                            {"Sandwich","Juice","Candy","Tea/Coffee","Samosa"},
                            {"Chicken","Paneer","Roti","Rice","Dal"}};
    String[] timings = {"TIMING: 7:00AM to 9:30AM", "TIMING: 12:00PM to 2:30PM", "TIMING: 4:30PM to 6:00PM","TIMING: 7:30PM to 9:30PM"};
    //TEST DATA

    private CardStackView mCardStack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onItemExpend(boolean expend) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_daily_menu, container, false);
        mCardStack = (CardStackView)v.findViewById(R.id.cardStackView);
        CardStackAdapter adapter = new CardStackAdapter(getActivity());
        mCardStack.setAdapter(adapter);

        mCardStack.setItemExpendListener(this);
        List<List<MenuData>> lists = new LinkedList<>();
        for(int i = 0;i<4;i++)
        {
            List<MenuData> list = new LinkedList<>();
            list.add(new MenuData(food_name[i]));

            lists.add(list);
        }

        adapter.updateData(Arrays.asList(color),lists,timings,food_time);
        //mCardStack.setAnimatorAdapter(new AllMoveDownAnimatorAdapter(mCardStack));
        //mCardStack.setAnimatorAdapter(new UpDownAnimatorAdapter(mCardStack));
        mCardStack.setAnimatorAdapter(new UpDownStackAnimatorAdapter(mCardStack));
        return v;

    }

    @Override
    public void onDestroyView() {
        if (v.getParent() != null) {
            ((ViewGroup) v.getParent()).removeView(v);
        }
        super.onDestroyView();
    }


}