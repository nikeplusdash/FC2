package com.foodmgmt.dontstarve.mainnav;

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.foodmgmt.dontstarve.R;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

import java.util.List;

public class CardStackAdapter extends StackAdapter<Integer> {
    //Context mContext;
    List<List<MenuData>> MenuList;
    String[] food_time;
    String[] timing;
    public CardStackAdapter(Context context)
    {
        super(context);
        //mContext = context;
    }


    public void updateData(List data,List<List<MenuData>> MenuList, String[] timing, String[] food_time) {
        this.food_time = food_time;
        this.MenuList = MenuList;
        this.timing = timing;
        updateData(data);
        System.out.println("Updated!");
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_item,parent,false);
        CardViewHolder holder = new CardViewHolder(view);
        System.out.println("onCreateView");
        return holder;
    }

    @Override
    public void bindView(Integer data, int position, CardStackView.ViewHolder holder) {
        if(holder instanceof CardViewHolder)
        {
            CardViewHolder cardHolder = (CardViewHolder)holder;
            cardHolder.onBind(data,position, MenuList, timing[position], food_time[position]);
        }
        System.out.println("bindView");
    }


    @Override
    public int getItemCount() {
        System.out.println("getItemCount");
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        System.out.println("getItemViewType");
        return super.getItemViewType(position);
    }

    @Override
    public Integer getItem(int position) {
        System.out.println("getItem");
        return super.getItem(position);
    }

    public static class CardViewHolder extends CardStackView.ViewHolder
    {
        View root;
        FrameLayout cardTitle;
        RecyclerView foodList;
        TextView titleText;
        public CardViewHolder(View view)
        {
            super(view);
            root = view;
            cardTitle = (FrameLayout)view.findViewById(R.id.card_title);
            titleText = (TextView)view.findViewById(R.id.card_title_text);
            foodList = (RecyclerView)view.findViewById(R.id.food_list);
            System.out.println("CardViewHolder constructor");
        }

        public void onBind(Integer backgroundColorId,int position,List<List<MenuData>> dataList, String timing,String food_time)
        {
           // cardTitle.getBackground().setColorFilter(ContextCompat.getColor(getContext(),backgroundColorId), PorterDuff.Mode.SRC_IN);
            cardTitle.setBackgroundResource(backgroundColorId);
            MenuListAdapter adapter = new MenuListAdapter(dataList.get(position), timing);
            foodList.setLayoutManager(new LinearLayoutManager(getContext()));
            foodList.setAdapter(adapter);
            titleText.setText(food_time);
            System.out.println("holder onBind");
        }

        @Override
        public void onItemExpand(boolean b) {
            foodList.setVisibility(b ? View.VISIBLE : View.GONE);
            System.out.println("holder onItemExpand");
        }
    }
}
