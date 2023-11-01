package com.mystatus.nachos.ui.listener;

import android.view.View;

public interface RecyclerViewClickListener {
    <T> void onRecyclerItemClick(View view, int position, T object);
}
