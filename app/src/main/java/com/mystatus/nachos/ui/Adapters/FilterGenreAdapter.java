package com.mystatus.nachos.ui.Adapters;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mystatus.nachos.R;
import com.mystatus.nachos.entity.Genre;
import com.mystatus.nachos.ui.listener.FiltersFocusChangedListener;
import com.mystatus.nachos.ui.listener.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;

public class FilterGenreAdapter extends RecyclerView.Adapter<FilterGenreAdapter.FilterGenreHolder> {
    private ArrayList<Genre> filterGenreList = new ArrayList();
    private Activity activity;
    private RecyclerViewClickListener recyclerViewClickListener;
    private FiltersFocusChangedListener filtersFocusChangedListener;

    public FilterGenreAdapter(Activity activity, RecyclerViewClickListener recyclerViewClickListener, FiltersFocusChangedListener filtersFocusChangedListener) {
        this.activity = activity;
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.filtersFocusChangedListener = filtersFocusChangedListener;
    }

    @Override
    public FilterGenreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_layout, parent, false);
        return new FilterGenreHolder(v);
    }

    @Override
    public void onBindViewHolder(FilterGenreHolder holder, final int position) {
        holder.textView.setText(filterGenreList.get(position).getTitle());
        if (filterGenreList.get(position).getSelected()) {
            holder.ivSelectedFilter.setVisibility(View.VISIBLE);
            holder.itemView.requestFocus();
        } else {
            holder.ivSelectedFilter.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return filterGenreList.size();
    }

    public void addAllData(List<Genre> countryCodes) {
        filterGenreList.addAll(countryCodes);
        notifyDataSetChanged();
    }

    public void setSelection(int adapterPosition) {
        for (int i = 0; i < filterGenreList.size(); i++) {
            if (i == adapterPosition) {
                filterGenreList.get(i).setSelected(true);
            } else {
                filterGenreList.get(i).setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    public Genre getItemAtPosition(int position) {
        return filterGenreList.get(position);
    }

    public class FilterGenreHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView ivSelectedFilter;

        public FilterGenreHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            this.ivSelectedFilter = (ImageView) itemView.findViewById(R.id.ivSelectedFilter);
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
                    recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition(), filterGenreList.get(getAdapterPosition()));
                    setSelection(getAdapterPosition());
                }
            });
        }
    }
}
