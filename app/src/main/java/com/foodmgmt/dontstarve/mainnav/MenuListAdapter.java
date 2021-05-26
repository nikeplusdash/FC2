package com.foodmgmt.dontstarve.mainnav;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

    public MenuListAdapter(List<?> list)
    {
        menu_list = list;
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder
    {
        TextView item1;
        //TextView card_title;

        public MenuViewHolder(View view)
        {
            super(view);
            item1 = (TextView)view.findViewById(R.id.item1);
        }
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        holder.item1.setText((String)menu_list.get(position));
        holder.item1.animate().alpha(1f).start();
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