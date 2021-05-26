package com.foodmgmt.dontstarve.mainnav;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foodmgmt.dontstarve.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MenuViewHolder> {
    List<?> menu_list;
    String time_range;

    public MenuListAdapter(List<?> list, String time_range)
    {
        menu_list = list;
        this.time_range = time_range;
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder
    {
        TextView item1;
        TextView item2;
        TextView item3;
        TextView item4;
        TextView item5;
        TextView timing;
        //TextView card_title;

        public MenuViewHolder(View view)
        {
            super(view);
            item1 = (TextView)view.findViewById(R.id.item1);
            item2 = (TextView)view.findViewById(R.id.item2);
            item3 = (TextView)view.findViewById(R.id.item3);
            item4 = (TextView)view.findViewById(R.id.item4);
            item5 = (TextView)view.findViewById(R.id.item5);
            timing = (TextView)view.findViewById(R.id.timing);
        }
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        holder.item1.setText((String)menu_list.get(0));
        holder.item2.setText((String)menu_list.get(1));
        holder.item3.setText((String)menu_list.get(2));
        holder.item4.setText((String)menu_list.get(3));
        holder.item5.setText((String)menu_list.get(4));
        holder.timing.setText(time_range);
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list,parent,false);
        return new MenuViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return menu_list.size();
    }
}