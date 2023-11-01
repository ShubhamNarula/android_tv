package com.mystatus.nachos.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.material.navigation.NavigationView;
import com.mystatus.nachos.Provider.PrefManager;
import com.mystatus.nachos.R;
import com.mystatus.nachos.Utils.CustomGridLayoutManager;
import com.mystatus.nachos.api.apiClient;
import com.mystatus.nachos.api.apiRest;
import com.mystatus.nachos.entity.FilterOrder;
import com.mystatus.nachos.entity.Genre;
import com.mystatus.nachos.entity.Poster;
import com.mystatus.nachos.event.CastSessionStartedEvent;
import com.mystatus.nachos.event.MoviesFilterEvent;
import com.mystatus.nachos.event.SeriesFilterEvent;
import com.mystatus.nachos.ui.Adapters.FilterGenreAdapter;
import com.mystatus.nachos.ui.Adapters.FilterOrderAdapter;
import com.mystatus.nachos.ui.Adapters.PosterAdapter;
import com.mystatus.nachos.ui.activities.SearchActivity;
import com.mystatus.nachos.ui.listener.FiltersFocusChangedListener;
import com.mystatus.nachos.ui.listener.RecyclerViewClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment implements View.OnKeyListener, FiltersFocusChangedListener, RecyclerViewClickListener {


    private View view;
    private RelativeLayout relative_layout_movies_fragement_filtres_button;
    private CardView card_view_movies_fragement_filtres_layout;
    private ImageView image_view_movies_fragement_close_filtres;
    private AppCompatSpinner spinner_fragement_movies_orders_list;
    private List<Genre> genreList = new ArrayList<>();
    private AppCompatSpinner spinner_fragement_movies_genre_list;
    private RelativeLayout relative_layout_frament_movies_genres;
    private RecyclerView recycler_view_movies_fragment;
    private LinearLayout linear_layout_page_error_movies_fragment;
    private LinearLayout linear_layout_load_movies_fragment;
    private SwipeRefreshLayout swipe_refresh_layout_movies_fragment;
    private RelativeLayout relative_layout_load_more_movies_fragment;
    private ImageView image_view_empty_list;
    private FilterGenreAdapter filterGenreAdapter;
    private FilterOrderAdapter filterOrderAdapter;
    private RecyclerView rvFilterGenre;
    private RecyclerView rvFilterOrder;
    private TextView tvSelectedGenre;
    private TextView tvSelectedOrder;
    private FrameLayout frameClearFilters;
    private TextView tvClearFilters;
    private NavigationView navViewFilters;
    private RelativeLayout relativeFilterParent;
    public static boolean isOpen = false;

    private GridLayoutManager gridLayoutManager;
    private PosterAdapter adapter;
    private List<Poster> movieList = new ArrayList<>();

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;

    private Integer page = 0;
    private Integer position = 0;
    private Integer item = 0;
    private Button button_try_again;
    private int genreSelected = 0;
    private String orderSelected = "created";

    private boolean firstLoadGenre = true;
    private boolean firstLoadOrder = true;
    private boolean loaded = false;

    private Integer lines_beetween_ads = 2;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false;
    private int type_ads = 0;
    private PrefManager prefManager;


    public MoviesFragment() {
        // Required empty public constructor
    }

    /* @Override
     public void setUserVisibleHint(boolean isVisibleToUser) {
         super.setUserVisibleHint(isVisibleToUser);

         if (isVisibleToUser){
             if (!loaded) {
                 loaded=true;
                 page = 0;
                 loading = true;
                 getGenreList();
                 loadMovies();
             }
         }
     }*/

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onCastSessionStartedEvent(MoviesFilterEvent event) {
        onBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_movies, container, false);
        movieList.add(new Poster().setTypeView(2));
        prefManager = new PrefManager(getApplicationContext());

        initView();
        initActon();
        loaded = true;
        page = 0;
        loading = true;
        getGenreList();
        loadMovies(true);
        return view;
    }

    private void getGenreList() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);

        Call<List<Genre>> call = service.getGenreList();
        call.enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                apiClient.FormatData(getActivity(), response);
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        final String[] countryCodes = new String[response.body().size() + 1];
                        countryCodes[0] = "All genres";
                        Genre genre = new Genre();
                        genre.setTitle("All genres");
                        genreList.add(genre);

                        for (int i = 0; i < response.body().size(); i++) {
                            countryCodes[i + 1] = response.body().get(i).getTitle();
                            genreList.add(response.body().get(i));
                        }
                        ArrayAdapter<String> filtresAdapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.spinner_layout, R.id.textView, countryCodes);
                        filtresAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                        spinner_fragement_movies_genre_list.setAdapter(filtresAdapter);
                        relative_layout_frament_movies_genres.setVisibility(View.VISIBLE);

                        filterGenreAdapter.addAllData(genreList);
                        tvSelectedGenre.setText(countryCodes[0]);
                    } else {
                        relative_layout_frament_movies_genres.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
            }
        });
    }

    private void initActon() {
        this.relative_layout_movies_fragement_filtres_button.setOnClickListener(v -> {
            navViewFilters.setVisibility(View.VISIBLE);
            isOpen = true;
            rvFilterOrder.requestFocus();
//            card_view_movies_fragement_filtres_layout.setVisibility(View.VISIBLE);
            relative_layout_movies_fragement_filtres_button.setVisibility(View.INVISIBLE);
        });
        this.image_view_movies_fragement_close_filtres.setOnClickListener(v -> {
            card_view_movies_fragement_filtres_layout.setVisibility(View.INVISIBLE);
            relative_layout_movies_fragement_filtres_button.setVisibility(View.VISIBLE);
        });
        spinner_fragement_movies_genre_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!firstLoadGenre) {
                    if (id == 0) {
                        genreSelected = 0;
                    } else {
                        genreSelected = genreList.get((int) id).getId();
                    }
                    item = 0;
                    page = 0;
                    loading = true;
                    movieList.clear();
                    movieList.add(new Poster().setTypeView(2));
                    adapter.notifyDataSetChanged();
                    loadMovies(true);
                } else {
                    firstLoadGenre = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_fragement_movies_orders_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!firstLoadOrder) {
                    switch ((int) id) {
                        case 0:
                            orderSelected = "created";
                            break;
                        case 1:
                            orderSelected = "rating";
                            break;
                        case 2:
                            orderSelected = "imdb";
                            break;
                        case 3:
                            orderSelected = "title";
                            break;
                        case 4:
                            orderSelected = "year";
                            break;
                        case 5:
                            orderSelected = "views";
                            break;
                    }
                    item = 0;
                    page = 0;
                    loading = true;
                    movieList.clear();
                    movieList.add(new Poster().setTypeView(2));
                    adapter.notifyDataSetChanged();
                    loadMovies(true);
                } else {
                    firstLoadOrder = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        swipe_refresh_layout_movies_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                item = 0;
                page = 0;
                loading = true;
                movieList.clear();
                movieList.add(new Poster().setTypeView(2));
                adapter.notifyDataSetChanged();
                loadMovies(true);

            }
        });
        button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item = 0;
                page = 0;
                loading = true;
                movieList.clear();
                movieList.add(new Poster().setTypeView(2));
                adapter.notifyDataSetChanged();
                loadMovies(true);

            }
        });
        recycler_view_movies_fragment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {

                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            loadMovies(true);
                        }
                    }
                } else {

                }
            }
        });

        frameClearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterGenreAdapter.setSelection(0);
                filterOrderAdapter.setSelection(0);
                tvSelectedGenre.setText(filterGenreAdapter.getItemAtPosition(0).getTitle());
                tvSelectedOrder.setText(filterOrderAdapter.getItemAtPosition(0).getTitle());
                onRecyclerItemClick(frameClearFilters, 0, filterGenreAdapter.getItemAtPosition(0));
                onRecyclerItemClick(frameClearFilters, 0, filterOrderAdapter.getItemAtPosition(0));
            }
        });

        frameClearFilters.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tvClearFilters.setBackground(getResources().getDrawable(R.drawable.bg_main_focus_stroke_10));
                } else {
                    tvClearFilters.setBackground(getResources().getDrawable(R.drawable.button_round_corner_white));
                }
            }
        });

        relativeFilterParent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    navViewFilters.setVisibility(View.GONE);
                    isOpen = false;
                    relative_layout_movies_fragement_filtres_button.setVisibility(View.VISIBLE);
                }
            }
        });

        relative_layout_movies_fragement_filtres_button.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    relative_layout_movies_fragement_filtres_button.performClick();
                }
            }
        });


        frameClearFilters.setOnKeyListener(this);
        relative_layout_movies_fragement_filtres_button.setOnKeyListener(this);
        recycler_view_movies_fragment.setOnKeyListener(this);
    }

    private void setFilterGenreAdapter() {
        filterGenreAdapter = new FilterGenreAdapter(requireActivity(), this, this);
        LinearLayoutManager linearLayoutManagerCategories = new LinearLayoutManager(getActivity());
        rvFilterGenre.setHasFixedSize(true);
        rvFilterGenre.setAdapter(filterGenreAdapter);
        rvFilterGenre.setLayoutManager(linearLayoutManagerCategories);
        rvFilterGenre.addItemDecoration(new DividerItemDecoration(rvFilterGenre.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void setFilterOrderAdapter() {
        filterOrderAdapter = new FilterOrderAdapter(getActivity(), this, this);
        LinearLayoutManager linearLayoutManagerCategories = new LinearLayoutManager(getActivity());
        rvFilterOrder.setHasFixedSize(true);
        rvFilterOrder.setAdapter(filterOrderAdapter);
        rvFilterOrder.setLayoutManager(linearLayoutManagerCategories);
        rvFilterOrder.addItemDecoration(new DividerItemDecoration(rvFilterOrder.getContext(), DividerItemDecoration.VERTICAL));
    }

    public boolean checkSUBSCRIBED() {
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            return false;
        }
        return true;
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
        // prod

        this.button_try_again = (Button) view.findViewById(R.id.button_try_again);
        this.image_view_empty_list = (ImageView) view.findViewById(R.id.image_view_empty_list);
        this.relative_layout_load_more_movies_fragment = (RelativeLayout) view.findViewById(R.id.relative_layout_load_more_movies_fragment);
        this.swipe_refresh_layout_movies_fragment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_movies_fragment);
        this.linear_layout_load_movies_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_load_movies_fragment);
        this.linear_layout_page_error_movies_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_page_error_movies_fragment);
        this.recycler_view_movies_fragment = (RecyclerView) view.findViewById(R.id.recycler_view_movies_fragment);
        this.relative_layout_movies_fragement_filtres_button = (RelativeLayout) view.findViewById(R.id.relative_layout_movies_fragement_filtres_button);
        this.card_view_movies_fragement_filtres_layout = (CardView) view.findViewById(R.id.card_view_movies_fragement_filtres_layout);
        this.image_view_movies_fragement_close_filtres = (ImageView) view.findViewById(R.id.image_view_movies_fragement_close_filtres);
        this.spinner_fragement_movies_orders_list = (AppCompatSpinner) view.findViewById(R.id.spinner_fragement_movies_orders_list);
        this.spinner_fragement_movies_genre_list = (AppCompatSpinner) view.findViewById(R.id.spinner_fragement_movies_genre_list);
        this.relative_layout_frament_movies_genres = (RelativeLayout) view.findViewById(R.id.relative_layout_frament_movies_genres);
        this.navViewFilters = (NavigationView) view.findViewById(R.id.navViewFilters);
        this.rvFilterGenre = (RecyclerView) view.findViewById(R.id.rvFilterGenre);
        this.rvFilterOrder = (RecyclerView) view.findViewById(R.id.rvFilterOrder);
        this.tvSelectedGenre = (TextView) view.findViewById(R.id.tvSelectedGenre);
        this.tvSelectedOrder = (TextView) view.findViewById(R.id.tvSelectedOrder);
        this.frameClearFilters = (FrameLayout) view.findViewById(R.id.frameClearFilters);
        this.tvClearFilters = (TextView) view.findViewById(R.id.tvClearFilters);
        this.relativeFilterParent = (RelativeLayout) view.findViewById(R.id.relativeFilterParent);
        this.relative_layout_movies_fragement_filtres_button = (RelativeLayout) view.findViewById(R.id.relative_layout_movies_fragement_filtres_button);

        setFilterGenreAdapter();
        setFilterOrderAdapter();

        adapter = new PosterAdapter(movieList, getActivity());
        if (native_ads_enabled) {
            Log.v("MYADS", "ENABLED");
            if (tabletSize) {
                this.gridLayoutManager = new CustomGridLayoutManager(getActivity().getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                Log.v("MYADS", "tabletSize");
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return ((position) % (lines_beetween_ads + 1) == 0 || position == 0) ? 6 : 1;
                    }
                });
            } else {
                this.gridLayoutManager = new CustomGridLayoutManager(getActivity().getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return ((position) % (lines_beetween_ads + 1) == 0 || position == 0) ? 6 : 1;
                    }
                });
            }
        } else {
            if (tabletSize) {
                this.gridLayoutManager = new CustomGridLayoutManager(getActivity().getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (position == 0) ? 6 : 1;
                    }
                });
            } else {
                this.gridLayoutManager = new CustomGridLayoutManager(getActivity().getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (position == 0) ? 6 : 1;
                    }
                });
            }
        }

        // TODO: 12/9/20 Fixed Load more scroll issue start
        recycler_view_movies_fragment.setItemAnimator(null);
        /*recycler_view_movies_fragment.setHasFixedSize(true);*/
        // TODO: 12/9/20 Fixed Load more scroll issue end
        adapter.setHasStableIds(true);
        recycler_view_movies_fragment.setAdapter(adapter);
        recycler_view_movies_fragment.setItemAnimator(null);
        recycler_view_movies_fragment.setLayoutManager(gridLayoutManager);
        // test


        final String[] countryCodes = getResources().getStringArray(R.array.orders_list);

        ArrayAdapter<String> ordersAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_layout, R.id.textView, countryCodes);
        ordersAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner_fragement_movies_orders_list.setAdapter(ordersAdapter);

        ArrayList<FilterOrder> filterOrderArrayList = new ArrayList<>();
        for (int i = 0; i < Arrays.asList(countryCodes).size(); i++) {
            FilterOrder filterOrder = new FilterOrder();
            filterOrder.setTitle(Arrays.asList(countryCodes).get(i));
            filterOrder.setSelected(false);
            filterOrderArrayList.add(filterOrder);
        }
        filterOrderAdapter.addAllData(filterOrderArrayList);
        tvSelectedOrder.setText(countryCodes[0]);
    }

    private void loadMovies(boolean showFocusOnList) {
        if (page == 0) {
            linear_layout_load_movies_fragment.setVisibility(View.VISIBLE);
        } else {
            showLoadMoreLoading();
        }
        swipe_refresh_layout_movies_fragment.setRefreshing(false);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Poster>> call = service.getMoviesByFiltres(genreSelected, orderSelected, page);
        call.enqueue(new Callback<List<Poster>>() {
            @Override
            public void onResponse(Call<List<Poster>> call, final Response<List<Poster>> response) {
                if (response.isSuccessful()) {
                    int previousPosition = movieList.size() - 1;
                    if (response.body().size() > 0) {
                        for (int i = 0; i < response.body().size(); i++) {
                            movieList.add(response.body().get(i));

                            if (native_ads_enabled) {
                                item++;
                                if (item == lines_beetween_ads) {
                                    item = 0;
                                    if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")) {
                                        movieList.add(new Poster().setTypeView(4));
                                    } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")) {
                                        movieList.add(new Poster().setTypeView(5));
                                    } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("BOTH")) {
                                        if (type_ads == 0) {
                                            movieList.add(new Poster().setTypeView(4));
                                            type_ads = 1;
                                        } else if (type_ads == 1) {
                                            movieList.add(new Poster().setTypeView(5));
                                            type_ads = 0;
                                        }
                                    }
                                }
                            }
                        }
                        linear_layout_page_error_movies_fragment.setVisibility(View.GONE);
                        recycler_view_movies_fragment.setVisibility(View.VISIBLE);
                        image_view_empty_list.setVisibility(View.GONE);


                        adapter.notifyDataSetChanged();
                        page++;
                        loading = true;

                        if (showFocusOnList) {
                            // TODO: 12/9/20 Fixed Load more scroll issue start
                            recycler_view_movies_fragment.postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                recycler_view_movies_fragment.requestFocus(View.FOCUS_DOWN);
                                    if (page == 1) {
                                        recycler_view_movies_fragment.requestFocus();
                                    } else {
                                        if (recycler_view_movies_fragment.getFocusedChild() != null) {
                                            recycler_view_movies_fragment.requestChildFocus(recycler_view_movies_fragment.getFocusedChild(),
                                                    recycler_view_movies_fragment.getFocusedChild());
                                        } else {
                                            recycler_view_movies_fragment.setFocusable(true);
                                            recycler_view_movies_fragment.setFocusableInTouchMode(true);
                                            recycler_view_movies_fragment.requestFocus(View.FOCUS_BACKWARD);
                                        }
                                    }
                                }
                            }, 500);
                            // TODO: 12/9/20 Fixed Load more scroll issue end
                        }
                    } else {
                        if (page == 0) {
                            linear_layout_page_error_movies_fragment.setVisibility(View.GONE);
                            recycler_view_movies_fragment.setVisibility(View.GONE);
                            image_view_empty_list.setVisibility(View.VISIBLE);
                        } else {
                            if (showFocusOnList) {
                                recycler_view_movies_fragment.requestChildFocus(recycler_view_movies_fragment.getFocusedChild(),
                                        recycler_view_movies_fragment.getFocusedChild());
                            }
                        }
                    }
                } else {
                    linear_layout_page_error_movies_fragment.setVisibility(View.VISIBLE);
                    recycler_view_movies_fragment.setVisibility(View.GONE);
                    image_view_empty_list.setVisibility(View.GONE);
                }
                hideLoadMoreLoading();
                swipe_refresh_layout_movies_fragment.setRefreshing(false);
                linear_layout_load_movies_fragment.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Poster>> call, Throwable t) {
                linear_layout_page_error_movies_fragment.setVisibility(View.VISIBLE);
                recycler_view_movies_fragment.setVisibility(View.GONE);
                image_view_empty_list.setVisibility(View.GONE);
                hideLoadMoreLoading();
                swipe_refresh_layout_movies_fragment.setVisibility(View.GONE);
                linear_layout_load_movies_fragment.setVisibility(View.GONE);

            }
        });
    }

    private void hideLoadMoreLoading() {
        relative_layout_load_more_movies_fragment.setVisibility(View.GONE);
    }

    private void showLoadMoreLoading() {
        relative_layout_load_more_movies_fragment.setVisibility(View.VISIBLE);
    }

    @Override
    public <T> void onRecyclerItemClick(View view, int position, T object) {
        if (object instanceof Genre) {
            if (position == 0) {
                genreSelected = 0;

            } else {
                genreSelected = genreList.get((int) position).getId();
            }
            item = 0;
            page = 0;
            loading = true;
            movieList.clear();
            movieList.add(new Poster().setTypeView(2));
            adapter.notifyDataSetChanged();
            loadMovies(false);
            tvSelectedGenre.setText(((Genre) object).getTitle());

        } else if (object instanceof FilterOrder) {
            switch ((int) position) {
                case 0:
                    orderSelected = "created";
                    break;
                case 1:
                    orderSelected = "rating";
                    break;
                case 2:
                    orderSelected = "imdb";
                    break;
                case 3:
                    orderSelected = "title";
                    break;
                case 4:
                    orderSelected = "year";
                    break;
                case 5:
                    orderSelected = "views";
                    break;
            }
            item = 0;
            page = 0;
            loading = true;
            movieList.clear();
            movieList.add(new Poster().setTypeView(2));
            adapter.notifyDataSetChanged();
            loadMovies(false);
            tvSelectedOrder.setText(((FilterOrder) object).getTitle());
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (v.getId()) {
            case R.id.frameClearFilters:
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        onFiltersFocusChanged(v, false);
                        return true;
                    }
                break;
            case R.id.relative_layout_movies_fragement_filtres_button:
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                            || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        recycler_view_movies_fragment.post(new Runnable() {
                            @Override
                            public void run() {
                                recycler_view_movies_fragment.setFocusable(true);
                                recycler_view_movies_fragment.setFocusableInTouchMode(true);
                                recycler_view_movies_fragment.requestFocus();
                            }
                        });
                        return true;
                    }
                break;

            case R.id.recycler_view_series_fragment:
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        recycler_view_movies_fragment.post(new Runnable() {
                            @Override
                            public void run() {
                                recycler_view_movies_fragment.smoothScrollToPosition(0);
                                recycler_view_movies_fragment.setFocusable(true);
                                recycler_view_movies_fragment.setFocusableInTouchMode(true);
                                recycler_view_movies_fragment.requestFocus(View.FOCUS_LEFT);
                            }
                        });
                        return true;
                    }
                break;
        }
        return false;
    }

    @Override
    public void onFiltersFocusChanged(View view, boolean hasFocus) {
        if (!hasFocus) {
            navViewFilters.setVisibility(View.GONE);
            isOpen = false;
            relative_layout_movies_fragement_filtres_button.setVisibility(View.VISIBLE);

//            relative_layout_movies_fragement_filtres_button.setFocusableInTouchMode(true);
//            relative_layout_movies_fragement_filtres_button.requestFocus();

            recycler_view_movies_fragment.post(new Runnable() {
                @Override
                public void run() {
                    recycler_view_movies_fragment.setFocusable(true);
                    recycler_view_movies_fragment.setFocusableInTouchMode(true);
                    recycler_view_movies_fragment.requestFocus();
                }
            });
        }
    }

    public boolean onBackPressed() {
        /*if (navViewFilters.getVisibility() == View.VISIBLE) {
            navViewFilters.setVisibility(View.GONE);
            relative_layout_movies_fragement_filtres_button.setVisibility(View.VISIBLE);
            isOpen = false;
            return true;
        }*/
        return false;
    }
}
