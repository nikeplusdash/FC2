package com.foodmgmt.dontstarve.mainnav;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.foodmgmt.dontstarve.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopeer.cardstack.CardStackView;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class DailyMenu extends Fragment implements CardStackView.ItemExpendListener {
    private View v;
    Integer[] color = {
            R.drawable.card_bg1,
            R.drawable.card_bg2,
            R.drawable.card_bg3,
            R.drawable.card_bg4,
    };
    String[] timings = {"TIMING: 7:00AM to 9:30AM", "TIMING: 7:30PM to 9:30PM", "TIMING: 4:30PM to 6:00PM", "TIMING: 12:00PM to 2:30PM"};

    private CardStackView mCardStack;
    private CardStackAdapter adapter;
    private DatabaseReference mDatabase;
    private HashMap<String, Object> menu_map;
    private Collection<Object> food_items;
    private Set<String> food_type;
    String[] food_timings;
    private HashMap<String, String> new_menu_map;


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

        mCardStack = (CardStackView) v.findViewById(R.id.cardStackView);
        adapter = new CardStackAdapter(getActivity());
        mCardStack.setAdapter(adapter);
        mCardStack.setItemExpendListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("menu");
        push_data();

        //mCardStack.setAnimatorAdapter(new AllMoveDownAnimatorAdapter(mCardStack));
        //mCardStack.setAnimatorAdapter(new UpDownAnimatorAdapter(mCardStack));
        mCardStack.setAnimationType(CardStackView.UP_DOWN_STACK);
        return v;

    }

    private void push_data() {

        // calling add value event listener method
        // for getting the values from database.
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                menu_map = (HashMap<String, Object>) snapshot.getValue();
                food_items = menu_map.values();
                food_type = menu_map.keySet();
                food_timings = new String[food_type.size()];
                int i = 0;
                for (String s : food_type) {
                    food_timings[i++] = s;
                }
                adapter.updateData(Arrays.asList(color), food_items, timings, food_timings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(getContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        if (v.getParent() != null) {
            ((ViewGroup) v.getParent()).removeView(v);
        }
        super.onDestroyView();
    }


}