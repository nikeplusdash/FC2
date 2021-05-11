package com.foodmgmt.dontstarve.mainnav;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.BaseAdapter;
import android.widget.ListView;


public class MenuData {
    String[] food_menu;
    public MenuData(String[] food_menu) {
        this.food_menu = food_menu;
    }

    public String[] getItemList() {
        return food_menu;
    }

}

