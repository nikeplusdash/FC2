package com.foodmgmt.dontstarve.mainnav;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodmgmt.dontstarve.R;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CardStackAdapter extends StackAdapter<Integer> {
    //Context mContext;

    Set<String> food_type;
    Collection<Object> food_items;
    String[] food_time;
    String[] timing;

    public CardStackAdapter(Context context) {
        super(context);
        //mContext = context;
    }


    public void updateData(List data, Collection<Object> food_items, String[] timing, String[] food_time) {
        System.out.println(food_items);
        this.food_time = food_time;
        this.food_items = food_items;
        this.timing = timing;
        updateData(data);
        System.out.println("Updated!");
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_item_2, parent, false);
        CardViewHolder holder = new CardViewHolder(view);
        System.out.println("onCreateView");
        return holder;
    }

    @Override
    public void bindView(Integer data, int position, CardStackView.ViewHolder holder) {
        if (holder instanceof CardViewHolder) {
            CardViewHolder cardHolder = (CardViewHolder) holder;
            cardHolder.onBind(data, position, food_items, timing[position], food_time[position]);
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

    public static class CardViewHolder extends CardStackView.ViewHolder {
        View root;
        FrameLayout cardTitle;
        RecyclerView foodList;
        TextView titleText, timingText;

        public CardViewHolder(View view) {
            super(view);
            root = view;
            cardTitle = (FrameLayout) view.findViewById(R.id.card_title);
            titleText = (TextView) view.findViewById(R.id.card_title_text);
            timingText = (TextView) view.findViewById(R.id.timing);
            foodList = (RecyclerView) view.findViewById(R.id.food_list);
            System.out.println("CardViewHolder constructor");
        }

        public void onBind(Integer backgroundColorId, int position, Collection<Object> dataList, String timing, String food_time) {

            Object[] trial = dataList.toArray();
            Object items = trial[position];
            List<?> item_list = (List) items;
            // cardTitle.getBackground().setColorFilter(ContextCompat.getColor(getContext(),backgroundColorId), PorterDuff.Mode.SRC_IN);
            cardTitle.setBackgroundResource(backgroundColorId);
            MenuListAdapter adapter = new MenuListAdapter(item_list);
            foodList.setLayoutManager(new LinearLayoutManager(getContext()));
            foodList.setAdapter(adapter);
            foodList.setItemViewCacheSize(7);
            titleText.setText(food_time);
            timingText.setText(timing);
            System.out.println("holder onBind");
        }

        @Override
        public void onItemExpand(boolean b) {
            foodList.setVisibility(b ? View.VISIBLE : View.GONE);
            timingText.setVisibility(b ? View.VISIBLE : View.GONE);
            System.out.println("holder onItemExpand");
        }
    }
}
