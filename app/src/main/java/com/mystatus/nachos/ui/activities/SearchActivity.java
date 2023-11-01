package com.mystatus.nachos.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.mystatus.nachos.Provider.PrefManager;
import com.mystatus.nachos.R;
import com.mystatus.nachos.api.apiClient;
import com.mystatus.nachos.api.apiRest;
import com.mystatus.nachos.entity.Channel;
import com.mystatus.nachos.entity.Data;
import com.mystatus.nachos.entity.Poster;
import com.mystatus.nachos.ui.Adapters.PosterAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {

    private String query;
    private SwipeRefreshLayout swipe_refresh_layout_list_search_search;
    private Button button_try_again;
    private LinearLayout linear_layout_layout_error;
    private RecyclerView recycler_view_activity_search;
    private ImageView image_view_empty_list;
    private GridLayoutManager gridLayoutManager;
    private PosterAdapter adapter;

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;

    private Integer page = 0;
    private Integer position = 0;
    private Integer item = 0;
    ArrayList<Poster> posterArrayList = new ArrayList<>();
    ArrayList<Channel> channelArrayList = new ArrayList<>();
    private LinearLayout linear_layout_load_search_activity;

    private Integer lines_beetween_ads = 2;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false;
    private int type_ads = 0;
    private PrefManager prefManager;
    private ImageView ivSearch;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        prefManager = new PrefManager(getApplicationContext());

        initView();
        initAction();

        showAdsBanner();

        searchView = findViewById(R.id.searchview);
        ivSearch = findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadPosters(newText);
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Toast.makeText(SearchActivity.this, "Show toast", Toast.LENGTH_SHORT).show();
                if (hasFocus) {
                    showInputMethod(v);
                }
            }
        });
        View searchEditText = findViewById(R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        showInputMethod(v);
                    }
                }
            });
        }

        loadPosters(searchView.getQuery().toString());
    }

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    private void initView() {

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (!prefManager.getString("ADMIN_NATIVE_TYPE").equals("FALSE")) {
            native_ads_enabled = true;
            if (tabletSize) {
                lines_beetween_ads = 6 * Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
            } else {
                lines_beetween_ads = 3 * Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
            }
        }
        if (checkSUBSCRIBED()) {
            native_ads_enabled = false;
        }

        Bundle bundle = getIntent().getExtras();
        this.query = bundle.getString("query");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(query);
        setSupportActionBar(toolbar);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/


        this.linear_layout_load_search_activity = findViewById(R.id.linear_layout_load_search_activity);
        this.swipe_refresh_layout_list_search_search = findViewById(R.id.swipe_refresh_layout_list_search_search);
        button_try_again = findViewById(R.id.button_try_again);
        image_view_empty_list = findViewById(R.id.image_view_empty_list);
        linear_layout_layout_error = findViewById(R.id.linear_layout_layout_error);
        recycler_view_activity_search = findViewById(R.id.recycler_view_activity_search);
        adapter = new PosterAdapter(posterArrayList, channelArrayList, this);

        recycler_view_activity_search.setHasFixedSize(true);
        recycler_view_activity_search.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);

        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.setIconifiedByDefault(false);
        searchView.onActionViewExpanded();
        searchView.requestFocus();
        return true;
    }

    private void loadPosters(String query) {
        swipe_refresh_layout_list_search_search.setRefreshing(false);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Data> call = service.searchData(query);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, final Response<Data> response) {
                if (response.isSuccessful()) {

                    posterArrayList.clear();
                    channelArrayList.clear();
                   /* if (response.body().getChannels() != null) {
                        for (int i = 0; i < response.body().getChannels().size(); i++) {
                            channelArrayList.add(response.body().getChannels().get(i));
                        }
                    }*/

                    if (channelArrayList.size() > 0) {
                        posterArrayList.add(new Poster().setTypeView(3));
                        if (native_ads_enabled) {
                            Log.v("MYADS", "ENABLED");
                            if (tabletSize) {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                                Log.v("MYADS", "tabletSize");
                                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        return ((position) % (lines_beetween_ads + 1) == 0 || position == 0) ? 6 : 1;
                                    }
                                });
                            } else {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        return ((position) % (lines_beetween_ads + 1) == 0 || position == 0) ? 3 : 1;
                                    }
                                });
                            }
                        } else {
                            if (tabletSize) {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        return (position == 0) ? 6 : 1;
                                    }
                                });
                            } else {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        return (position == 0) ? 3 : 1;
                                    }
                                });
                            }
                        }
                    } else {
                        if (native_ads_enabled) {
                            Log.v("MYADS", "ENABLED");
                            if (tabletSize) {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                                Log.v("MYADS", "tabletSize");
                                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        return ((position + 1) % (lines_beetween_ads + 1) == 0 && position != 0) ? 6 : 1;
                                    }
                                });
                            } else {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        return ((position + 1) % (lines_beetween_ads + 1) == 0 && position != 0) ? 3 : 1;
                                    }
                                });
                            }
                        } else {
                            if (tabletSize) {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                            } else {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                            }
                        }
                    }

                    if (response.body().getPosters() != null) {
                        for (int i = 0; i < response.body().getPosters().size(); i++) {
                            posterArrayList.add(response.body().getPosters().get(i).setTypeView(1));
                            if (native_ads_enabled) {
                                item++;
                                if (item == lines_beetween_ads) {
                                    item = 0;
                                    if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")) {
                                        posterArrayList.add(new Poster().setTypeView(4));
                                    } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")) {
                                        posterArrayList.add(new Poster().setTypeView(5));
                                    } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("BOTH")) {
                                        if (type_ads == 0) {
                                            posterArrayList.add(new Poster().setTypeView(4));
                                            type_ads = 1;
                                        } else if (type_ads == 1) {
                                            posterArrayList.add(new Poster().setTypeView(5));
                                            type_ads = 0;
                                        }
                                    }
                                }
                            }

                        }
                    }
                    if (channelArrayList.size() == 0 && posterArrayList.size() == 0) {
                        linear_layout_layout_error.setVisibility(View.GONE);
                        recycler_view_activity_search.setVisibility(View.GONE);
                        image_view_empty_list.setVisibility(View.VISIBLE);
                    } else {
                        linear_layout_layout_error.setVisibility(View.GONE);
                        recycler_view_activity_search.setVisibility(View.VISIBLE);
                        image_view_empty_list.setVisibility(View.GONE);
                    }
                } else {
                    linear_layout_layout_error.setVisibility(View.VISIBLE);
                    recycler_view_activity_search.setVisibility(View.GONE);
                    image_view_empty_list.setVisibility(View.GONE);
                }
                swipe_refresh_layout_list_search_search.setRefreshing(false);
                linear_layout_load_search_activity.setVisibility(View.GONE);
                recycler_view_activity_search.setLayoutManager(gridLayoutManager);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                linear_layout_layout_error.setVisibility(View.VISIBLE);
                recycler_view_activity_search.setVisibility(View.GONE);
                image_view_empty_list.setVisibility(View.GONE);
                swipe_refresh_layout_list_search_search.setVisibility(View.GONE);
                linear_layout_load_search_activity.setVisibility(View.GONE);

            }
        });
    }

    private void initAction() {
        swipe_refresh_layout_list_search_search.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                item = 0;
                page = 0;
                loading = true;
                posterArrayList.clear();
                channelArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters(searchView.getQuery().toString());
            }
        });
        button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item = 0;
                page = 0;
                loading = true;
                posterArrayList.clear();
                channelArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters(searchView.getQuery().toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem itemMenu) {
        switch (itemMenu.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(itemMenu);
    }

    public boolean checkSUBSCRIBED() {
        PrefManager prefManager = new PrefManager(getApplicationContext());
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            return false;
        }
        return true;
    }

    public void showAdsBanner() {
        if (getString(R.string.AD_MOB_ENABLED_BANNER).equals("true")) {
            if (!checkSUBSCRIBED()) {
                PrefManager prefManager = new PrefManager(getApplicationContext());
                if (prefManager.getString("ADMIN_BANNER_TYPE").equals("FACEBOOK")) {
                    showFbBanner();
                }
                if (prefManager.getString("ADMIN_BANNER_TYPE").equals("ADMOB")) {
                    showAdmobBanner();
                }
                if (prefManager.getString("ADMIN_BANNER_TYPE").equals("BOTH")) {
                    if (prefManager.getString("Banner_Ads_display").equals("FACEBOOK")) {
                        prefManager.setString("Banner_Ads_display", "ADMOB");
                        showAdmobBanner();
                    } else {
                        prefManager.setString("Banner_Ads_display", "FACEBOOK");
                        showFbBanner();
                    }
                }
            }
        }
    }

    public void showAdmobBanner() {
        PrefManager prefManager = new PrefManager(getApplicationContext());
        LinearLayout linear_layout_ads = (LinearLayout) findViewById(R.id.linear_layout_ads);
        final AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(prefManager.getString("ADMIN_BANNER_ADMOB_ID"));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        linear_layout_ads.addView(mAdView);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void showFbBanner() {
        PrefManager prefManager = new PrefManager(getApplicationContext());
        LinearLayout linear_layout_ads = (LinearLayout) findViewById(R.id.linear_layout_ads);
        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(this, prefManager.getString("ADMIN_BANNER_FACEBOOK_ID"), com.facebook.ads.AdSize.BANNER_HEIGHT_90);
        linear_layout_ads.addView(adView);
        adView.loadAd();

    }
}
