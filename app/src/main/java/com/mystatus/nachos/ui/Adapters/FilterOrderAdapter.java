package com.mystatus.nachos.ui.Adapters;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mystatus.nachos.R;
import com.mystatus.nachos.entity.FilterOrder;
import com.mystatus.nachos.ui.listener.FiltersFocusChangedListener;
import com.mystatus.nachos.ui.listener.RecyclerViewClickListener;

import java.util.ArrayList;

public class FilterOrderAdapter extends RecyclerView.Adapter<FilterOrderAdapter.FilterOrderHolder> {
    private ArrayList<FilterOrder> filterOrdersList = new ArrayList<>();
    private Activity activity;
    private RecyclerViewClickListener recyclerViewClickListener;
    private FiltersFocusChangedListener filtersFocusChangedListener;

    public FilterOrderAdapter(Activity activity, RecyclerViewClickListener recyclerViewClickListener, FiltersFocusChangedListener filtersFocusChangedListener) {
        this.activity = activity;
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.filtersFocusChangedListener = filtersFocusChangedListener;
    }

    @Override
    public FilterOrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_layout, parent, false);
        return new FilterOrderHolder(v);
    }

    @Override
    public void onBindViewHolder(FilterOrderHolder holder, final int position) {
        holder.textView.setText(filterOrdersList.get(position).getTitle());
        if (filterOrdersList.get(position).getSelected()) {
            holder.ivSelectedFilter.setVisibility(View.VISIBLE);
            holder.itemView.requestFocus();
        } else {
            holder.ivSelectedFilter.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return filterOrdersList.size();
    }

    public void addAllData(ArrayList<FilterOrder> countryCodes) {
        filterOrdersList.addAll(countryCodes);
        notifyDataSetChanged();
    }

    public FilterOrder getItemAtPosition(int position) {
        return filterOrdersList.get(position);
    }

    public void setSelection(int adapterPosition) {
        for (int i = 0; i < filterOrdersList.size(); i++) {
            if (i == adapterPosition) {
                filterOrdersList.get(i).setSelected(true);
            } else {
                filterOrdersList.get(i).setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    public class FilterOrderHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView ivSelectedFilter;

        public FilterOrderHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            this.ivSelectedFilter = (ImageView) itemView.findViewById(R.id.ivSelectedFilter);
            ImageViewCompat.setImageTintList(ivSelectedFilter, ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.red)));

            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        itemView.setBackground(activity.getDrawable(R.drawable.bg_main_focus_stroke_10));
                    } else {
                        itemView.setBackground(null);
                    }
                }
            });

            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN)
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            filtersFocusChangedListener.onFiltersFocusChanged(v, false);
                            return true;
                        }
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition(), filterOrdersList.get(getAdapterPosition()));
                    setSelection(getAdapterPosition());
                }
            });
        }
    }
}
