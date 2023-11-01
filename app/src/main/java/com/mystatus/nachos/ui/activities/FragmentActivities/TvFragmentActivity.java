package com.mystatus.nachos.ui.activities.FragmentActivities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.mystatus.nachos.R;
import com.mystatus.nachos.ui.activities.HomeBaseActivity;
import com.mystatus.nachos.ui.fragments.SeriesFragment;
import com.mystatus.nachos.ui.fragments.TvFragment;

public class TvFragmentActivity extends HomeBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initBase();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TvFragment()).commit();
        setSelectedNav(R.id.nav_tv);
    }
}
