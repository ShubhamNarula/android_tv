package com.mystatus.nachos.ui.Adapters;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mystatus.nachos.R;
import com.mystatus.nachos.entity.FilterOrder;
import com.mystatus.nachos.ui.listener.FiltersFocusChangedListener;
import com.mystatus.nachos.ui.listener.RecyclerViewClickListener;

import java.util.ArrayList;

public class SeriesListAdapter extends RecyclerView.Adapter<SeriesListAdapter.SeasonListHolder> {
    private ArrayList<FilterOrder> filterOrdersList = new ArrayList<>();
    private Activity activity;
    private RecyclerViewClickListener recyclerViewClickListener;
    private FiltersFocusChangedListener filtersFocusChangedListener;

    public SeriesListAdapter(Activity activity, RecyclerViewClickListener recyclerViewClickListener, FiltersFocusChangedListener filtersFocusChangedListener) {
        this.activity = activity;
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.filtersFocusChangedListener = filtersFocusChangedListener;
    }

    @Override
    public SeasonListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.series_season_list_layout, parent, false);
        return new SeasonListHolder(v);
    }

    @Override
    public void onBindViewHolder(SeasonListHolder holder, final int position) {
        holder.textView.setText(filterOrdersList.get(position).getTitle());
        if (filterOrdersList.get(position).getSelected()) {
            holder.itemView.requestFocus();
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

    public int getSelection() {
        for (int i = 0; i < filterOrdersList.size(); i++) {
            if (filterOrdersList.get(i).getSelected()) {
                return i;
            }
        }
        return 0;
    }

    public class SeasonListHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public SeasonListHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            itemView.findViewById(R.id.ivSelectedFilter).setVisibility(View.GONE);

            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        itemView.setBackground(activity.getDrawable(R.drawable.bg_main_focus_stroke));
                    } else {
                        itemView.setBackground(activity.getDrawable(R.drawable.bg_channel_foucus));
                    }
                }
            });

            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN)
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_DOWN:
                                if (getSelection() < filterOrdersList.size() - 1) {
                                    setSelection(getSelection() + 1);
                                }
                                break;
                            case KeyEvent.KEYCODE_DPAD_UP:
                                if (getSelection() > 0) {
                                    setSelection(getSelection() - 1);
                                }
                                break;
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
