package com.mystatus.nachos.ui.activities.FragmentActivities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;

import com.mystatus.nachos.R;
import com.mystatus.nachos.ui.activities.HomeBaseActivity;
import com.mystatus.nachos.ui.fragments.SeriesFragment;

public class SerieFragmentActivity extends HomeBaseActivity {

    private SeriesFragment seriesFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initBase();
        seriesFragment = new SeriesFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, seriesFragment).commit();


        /*ViewPager viewPager = findViewById(R.id.vp_horizontal_ntb);
        viewPager.setOffscreenPageLimit(100);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(seriesFragment);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        viewPager.setAdapter(adapter);
        setSelectedNav(R.id.nav_series);*/
    }

    /*class ViewPagerAdapter extends FragmentPagerAdapter {

        Fragment fragment;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragment;
        }

        @Override
        public int getCount() {
            return 1;
        }

        public void addFragment(Fragment fragment) {
            this.fragment = fragment;
        }

    }
*/
    @Override
    public void onBackPressed() {
        if (!seriesFragment.onBackPressed()) {
            super.onBackPressed();
        }

        /*if (!drawer.isDrawerOpen(GravityCompat.START)) {
            seriesFragment.setRecyclerviewFocus();
        }*/
    }
}
