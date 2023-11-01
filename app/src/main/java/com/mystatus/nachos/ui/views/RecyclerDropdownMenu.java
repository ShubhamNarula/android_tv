package com.mystatus.nachos.ui.views;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mystatus.nachos.R;
import com.mystatus.nachos.entity.FilterOrder;
import com.mystatus.nachos.ui.Adapters.SeriesListAdapter;
import com.mystatus.nachos.ui.listener.FiltersFocusChangedListener;
import com.mystatus.nachos.ui.listener.RecyclerViewClickListener;

import java.util.ArrayList;

public class RecyclerDropdownMenu extends PopupWindow {
    private Activity context;
    private RecyclerView rvCategory;
    private SeriesListAdapter dropdownAdapter;
    private ArrayList<FilterOrder> serieSeasonList;
    private RecyclerViewClickListener recyclerViewClickListener;
    private FiltersFocusChangedListener filtersFocusChangedListener;

    public RecyclerDropdownMenu(Activity context, ArrayList<FilterOrder> serieSeasonList,
                                RecyclerViewClickListener recyclerViewClickListener, FiltersFocusChangedListener filtersFocusChangedListener) {
        super(context);
        this.context = context;
        this.serieSeasonList = serieSeasonList;
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.filtersFocusChangedListener = filtersFocusChangedListener;
        setupView();
    }

    private void setupView() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_dropdown, null);

        rvCategory = view.findViewById(R.id.rvFilterOrder);
        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        dropdownAdapter = new SeriesListAdapter(context, recyclerViewClickListener, filtersFocusChangedListener);
        rvCategory.setAdapter(dropdownAdapter);

        dropdownAdapter.addAllData(serieSeasonList);
        dropdownAdapter.setSelection(0);

        setContentView(view);
    }
}