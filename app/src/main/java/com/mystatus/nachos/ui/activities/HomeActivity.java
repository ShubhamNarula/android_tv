package com.mystatus.nachos.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.mystatus.nachos.Provider.PrefManager;
import com.mystatus.nachos.R;
import com.mystatus.nachos.api.apiClient;
import com.mystatus.nachos.api.apiRest;
import com.mystatus.nachos.entity.ApiResponse;
import com.mystatus.nachos.entity.Genre;
import com.mystatus.nachos.ui.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeActivity extends HomeBaseActivity {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    /*private ImageView main_background;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        checkPaid();
        getGenreList();
        initBase();
    }




    private void checkPaid() {

        PrefManager prf = new PrefManager(getApplicationContext());
        String email = prf.getString("USERN_USER");

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.checkpaid(email);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCode().equals(200)) {
                        prf.setString("SUBSCRIBED", "TRUE");

                    }
                    if (response.body().getCode().equals(600)) {
                        prf.setString("SUBSCRIBED", "FALSE");

                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }

        });

    }




    protected void initViews() {
        super.initViews();

        viewPager = findViewById(R.id.vp_horizontal_ntb);
        viewPager.setOffscreenPageLimit(100);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        /*adapter.addFragment(new MoviesFragment());
        adapter.addFragment(new SeriesFragment());
        adapter.addFragment(new TvFragment());*/
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
//        final BubbleNavigationConstraintView bubbleNavigationLinearView = findViewById(R.id.top_navigation_constraint);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
//                bubbleNavigationLinearView.setCurrentActiveItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
//        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
//            @Override
//            public void onNavigationChanged(View view, int position) {
//                viewPager.setCurrentItem(position, true);
//            }
//        });

        /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();*/

        setSelectedNav(R.id.nav_home);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);
        return true;
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

    private void getGenreList() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);

        Call<List<Genre>> call = service.getGenreList();
        call.enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {

            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
            }
        });
    }
}
