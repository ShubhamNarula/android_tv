package com.mystatus.nachos.ui.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.vending.billing.IInAppBillingService;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadRequestData;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.mystatus.nachos.Provider.PrefManager;
import com.mystatus.nachos.R;
import com.mystatus.nachos.Utils.imageloader.ImageLoader;
import com.mystatus.nachos.api.apiClient;
import com.mystatus.nachos.api.apiRest;
import com.mystatus.nachos.config.Global;
import com.mystatus.nachos.crypto.PlaylistDownloader;
import com.mystatus.nachos.entity.Actor;
import com.mystatus.nachos.entity.ApiResponse;
import com.mystatus.nachos.entity.Comment;
import com.mystatus.nachos.entity.DownloadItem;
import com.mystatus.nachos.entity.Episode;
import com.mystatus.nachos.entity.FilterOrder;
import com.mystatus.nachos.entity.Language;
import com.mystatus.nachos.entity.Poster;
import com.mystatus.nachos.entity.Season;
import com.mystatus.nachos.entity.Source;
import com.mystatus.nachos.entity.Subtitle;
import com.mystatus.nachos.event.CastSessionEndedEvent;
import com.mystatus.nachos.event.CastSessionStartedEvent;
import com.mystatus.nachos.services.DownloadService;
import com.mystatus.nachos.services.ToastService;
import com.mystatus.nachos.ui.Adapters.ActorAdapter;
import com.mystatus.nachos.ui.Adapters.CommentAdapter;
import com.mystatus.nachos.ui.Adapters.GenreAdapter;
import com.mystatus.nachos.ui.fragments.SubscribeDialogFragment;
import com.mystatus.nachos.ui.listener.FiltersFocusChangedListener;
import com.mystatus.nachos.ui.listener.RecyclerViewClickListener;
import com.mystatus.nachos.ui.listener.SubscribeDialogListener;
import com.mystatus.nachos.ui.views.RecyclerDropdownMenu;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

//import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class SerieActivity extends AppCompatActivity implements PlaylistDownloader.DownloadListener, SubscribeDialogListener, RecyclerViewClickListener, FiltersFocusChangedListener {
    private static String TAG = "SerieActivity";
    private CastContext mCastContext;
    private SessionManager mSessionManager;
    private CastSession mCastSession;
    private final SessionManagerListener mSessionManagerListener =
            new SessionManagerListenerImpl();

    //views
    private PrefManager prf;
    private ImageView image_view_activity_serie_background;
    private ImageView image_view_activity_serie_cover;
    private TextView text_view_activity_serie_title;
    private TextView text_view_activity_serie_sub_title;
    private TextView text_view_activity_serie_description;
    private TextView text_view_activity_serie_year;
    private TextView text_view_activity_serie_duration;
    private TextView text_view_activity_serie_classification;
    private RatingBar rating_bar_activity_serie_rating;
    private RecyclerView recycle_view_activity_serie_genres;
    private LinearLayout action_button_activity_serie_play;
    private View play;
//    private FloatingActionButton floating_action_button_activity_serie_comment;

    private LinearLayout linear_layout_activity_serie_cast;
    private RecyclerView recycle_view_activity_activity_serie_cast;
    private LinearLayoutManager linearLayoutManagerCast;
    private LinearLayout linear_layout_serie_activity_trailer;
    private LinearLayout linear_layout_serie_activity_rate;
    // lists
    private ArrayList<Comment> commentList = new ArrayList<>();
    private ArrayList<Subtitle> subtitlesForCast = new ArrayList<>();
    // serie
    private Poster poster;

    // layout manager
    private LinearLayoutManager linearLayoutManagerComments;
    private LinearLayoutManager linearLayoutManagerSources;
    private LinearLayoutManager linearLayoutManagerGenre;
    private LinearLayoutManager linearLayoutManagerDownloadSources;

    // adapters
    private GenreAdapter genreAdapter;
    private ActorAdapter actorAdapter;
    private CommentAdapter commentAdapter;
    private EpisodeAdapter episodeAdapter;


    private LinearLayout linear_layout_serie_activity_trailer_clicked;
    private RelativeLayout relative_layout_subtitles_loading;
    private RecyclerView recycle_view_activity_activity_serie_episodes;
    private LinearLayout linear_layout_activity_serie_seasons;
    private LinearLayout linear_layout_activity_serie_my_list;
    private ImageView image_view_activity_serie_my_list;
    private List<Source> downloadableList = new ArrayList<>();
    private List<Source> playableList = new ArrayList<>();
    private List<Season> seasonArrayList = new ArrayList<>();
    private Episode selectedEpisode = null;
    int lastPosition = -1;
    private LinearLayout linear_layout_serie_activity_share;
    private String from;
    private AppCompatSpinner spinner_activity_serie_season_list;
    private RelativeLayout relativeSeriesList;
    private TextView tvSpinnerSelectedItem;
    private LinearLayoutManager linearLayoutManagerEpisodes;
    private LinearLayout linear_layout_activity_serie_rating;
    private TextView text_view_activity_serie_imdb_rating;
    private LinearLayout linear_layout_activity_serie_imdb_rating;
    private LinearLayout rate;
    private LinearLayout my_list;
    private ArrayList<FilterOrder> serieSeasonList = new ArrayList<>();
    private RecyclerDropdownMenu categoryDropdownMenu;
    private TextView tvPlayText;

    Context context = this;
    private long downloadID;
    private ProgressBar progressBarAnimation;
    private ObjectAnimator progressAnimator;

    String getSeekTime = "";
    int mSelectedID = 0;
    int mProgressPercentage = 0;
    protected ProgressBar mProgressBar;
    private int seasonSelectedPos;
    private boolean isPlayPerformed = false;

    private class SessionManagerListenerImpl implements SessionManagerListener {
        @Override
        public void onSessionStarting(Session session) {
            Log.d(TAG, "onSessionStarting");
        }

        @Override
        public void onSessionStarted(Session session, String s) {
            Log.d(TAG, "onSessionStarted");
            invalidateOptionsMenu();
            EventBus.getDefault().post(new CastSessionStartedEvent());
        }

        @Override
        public void onSessionStartFailed(Session session, int i) {
            Log.d(TAG, "onSessionStartFailed");
        }

        @Override
        public void onSessionEnding(Session session) {
            Log.d(TAG, "onSessionEnding");
            EventBus.getDefault().post(new CastSessionEndedEvent(session.getSessionRemainingTimeMs()));
        }

        @Override
        public void onSessionEnded(Session session, int i) {
            Log.d(TAG, "onSessionEnded");
        }

        @Override
        public void onSessionResuming(Session session, String s) {
            Log.d(TAG, "onSessionResuming");
        }

        @Override
        public void onSessionResumed(Session session, boolean b) {
            Log.d(TAG, "onSessionResumed");
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumeFailed(Session session, int i) {
            Log.d(TAG, "onSessionResumeFailed");
        }

        @Override
        public void onSessionSuspended(Session session, int i) {
            Log.d(TAG, "onSessionSuspended");
        }
    }


    private RewardedVideoAd mRewardedVideoAd;


    private Boolean DialogOpened = false;
    private Boolean fromLoad = false;
    private int operationAfterAds = 0;

    IInAppBillingService mService;

    private static final String LOG_TAG = "iabv3";
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID = null;

    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };
    private boolean autoDisplay = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        mSessionManager = CastContext.getSharedInstance(this).getSessionManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serie);
//        mCastContext = CastContext.getSharedInstance(this);


        checkAccount();
        checkPaid();

        initView();
        initAction();
        getSerie();
        setSerie();
        getPosterCastings();
        getSeasons();
        checkFavorite();
        showAdsBanner();

        initRewarded();
        loadRewardedVideoAd();
        initBuy();


    }


    private void checkAccount() {

        Integer version = -1;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (version != -1) {
            Integer id_user = 0;

            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<ApiResponse> call = service.check(version, id_user);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    updateTextViews();
                    if (response.isSuccessful()) {
                        if (response.body().getCode().equals(202)) {


                            Intent update = new Intent(SerieActivity.this, SplashActivity.class);
                            startActivity(update);
                            finish();

                        }
                    }

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {

                }

            });
        }
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



    public void loadRewardedVideoAd() {
        PrefManager prefManager = new PrefManager(getApplicationContext());

        mRewardedVideoAd.loadAd(prefManager.getString("ADMIN_REWARDED_ADMOB_ID"),
                new AdRequest.Builder().build());
    }

    public void initRewarded() {

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                if (autoDisplay) {
                    autoDisplay = false;
                    mRewardedVideoAd.show();
                }
                Log.d("Rewarded", "onRewardedVideoAdLoaded ");

            }

            @Override
            public void onRewardedVideoAdOpened() {
                Log.d("Rewarded", "onRewardedVideoAdOpened ");
            }

            @Override
            public void onRewardedVideoStarted() {
                Log.d("Rewarded", "onRewardedVideoStarted ");

            }

            @Override
            public void onRewardedVideoAdClosed() {
                loadRewardedVideoAd();
                Log.d("Rewarded", "onRewardedVideoAdClosed ");

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Toasty.success(getApplicationContext(), getString(R.string.use_content_for_free)).show();
                Log.d("Rewarded", "onRewarded ads");
                switch (operationAfterAds) {
                    case 100:
                        selectedEpisode.setDownloadas("1");
                        break;
                    case 200:
                        selectedEpisode.setPlayas("1");
                        break;
                }
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                Log.d("Rewarded", "onRewardedVideoAdLeftApplication ");
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Log.d("Rewarded", "onRewardedVideoAdFailedToLoad " + i);
            }

            @Override
            public void onRewardedVideoCompleted() {
                Log.d("Rewarded", "onRewardedVideoCompleted ");

            }
        });

    }

    private void initBuy() {
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        if (!BillingProcessor.isIabServiceAvailable(this)) {
            //  showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(this, Global.MERCHANT_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                //  showToast("onProductPurchased: " + productId);
                Intent intent = new Intent(SerieActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
                updateTextViews();
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                // showToast("onBillingError: " + Integer.toString(errorCode));
            }

            @Override
            public void onBillingInitialized() {
                //  showToast("onBillingInitialized");
                readyToPurchase = true;
                updateTextViews();
            }

            @Override
            public void onPurchaseHistoryRestored() {
                // showToast("onPurchaseHistoryRestored");
                for (String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for (String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });
        bp.loadOwnedPurchasesFromGoogle();
    }

    private void updateTextViews() {
        PrefManager prf = new PrefManager(getApplicationContext());
        bp.loadOwnedPurchasesFromGoogle();

    }

    public Bundle getPurchases() {
        if (!bp.isInitialized()) {


            //  Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            // Toast.makeText(this, "good", Toast.LENGTH_SHORT).show();

            return mService.getPurchases(Constants.GOOGLE_API_VERSION, getApplicationContext().getPackageName(), Constants.PRODUCT_TYPE_SUBSCRIPTION, null);
        } catch (Exception e) {
            //  Toast.makeText(this, "ex", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
        return null;
    }

    private void setDownloadableList(Episode episode, int pos) {
        selectedEpisode = episode;
        lastPosition = pos;

        downloadableList.clear();
        for (int i = 0; i < episode.getSources().size(); i++) {
            if (!episode.getSources().get(i).getType().equals("youtube") && !episode.getSources().get(i).getType().equals("embed")) {
                downloadableList.add(episode.getSources().get(i));
            }
        }
        if (checkSUBSCRIBED()) {
            showSourcesDownloadDialog();
        } else {
            if (selectedEpisode.getDownloadas().equals("2")) {
                showDialog(false);
            } else if (selectedEpisode.getDownloadas().equals("3")) {
                showDialog(true);
                operationAfterAds = 100;
            } else {
                showSourcesDownloadDialog();
            }
        }
    }

    private void setPlayableList(Episode episode, int pos) {
        selectedEpisode = episode;
        playableList.clear();
        for (int i = 0; i < episode.getSources().size(); i++) {
            playableList.add(episode.getSources().get(i));
        }

        if (checkSUBSCRIBED()) {
            showSourcesPlayDialog();
        } else {
            if (selectedEpisode.getPlayas().equals("2")) {
                showDialog(false);
            } else if (selectedEpisode.getPlayas().equals("3")) {
                showDialog(true);
                operationAfterAds = 200;
            } else {
                showSourcesPlayDialog();
            }
        }
    }

    private void getSeasons() {

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        PrefManager prf = new PrefManager(this);
        String email = prf.getString("USERN_USER");
        System.out.println("SHUBHAM--" + email);
        System.out.println("SHUBHAM--" + poster.getId());
        Call<List<Season>> call = service.getSeasonsBySerie(poster.getId(), email);
        call.enqueue(new Callback<List<Season>>() {
            @Override
            public void onResponse(Call<List<Season>> call, Response<List<Season>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        seasonArrayList.clear();
                        final String[] countryCodes = new String[response.body().size()];

                        for (int i = 0; i < response.body().size(); i++) {
                            countryCodes[i] = response.body().get(i).getTitle();
                            seasonArrayList.add(response.body().get(i));
                        }
                        ArrayAdapter<String> filtresAdapter = new ArrayAdapter<String>(SerieActivity.this,
                                R.layout.spinner_layout_season, R.id.textView, countryCodes);
                        for (String countryCode : countryCodes) {
                            FilterOrder filterOrder = new FilterOrder();
                            filterOrder.setTitle(countryCode);
                            filterOrder.setSelected(false);
                            serieSeasonList.add(filterOrder);
                        }
//                        SeasonListAdapter filtresAdapter = new SeasonListAdapter(SerieActivity.this,
//                                R.layout.spinner_layout_season, R.id.textView, serieSeasonList);
                        spinner_activity_serie_season_list.setAdapter(filtresAdapter);

                        linear_layout_activity_serie_seasons.setVisibility(View.VISIBLE);

                        for (Season season : seasonArrayList) {
                            if (season.getId() == lastPlaySeasonId) {
                                for (Episode episode : season.getEpisodes()) {
                                    if (episode.getId() == lastPlayEpisodeId) {
                                        tvPlayText.setText("Play " + season.getTitle() + "  : " + episode.getTitle());
                                    }
                                }
                            }
                        }
                    } else {
                        linear_layout_activity_serie_seasons.setVisibility(View.GONE);
                    }
                } else {
                    linear_layout_activity_serie_seasons.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Season>> call, Throwable t) {
                linear_layout_activity_serie_seasons.setVisibility(View.GONE);

            }
        });
    }

    private void getPosterCastings() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Actor>> call = service.getRolesByPoster(poster.getId());
        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        linearLayoutManagerCast = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        actorAdapter = new ActorAdapter(response.body(), SerieActivity.this);
                        recycle_view_activity_activity_serie_cast.setHasFixedSize(true);
                        recycle_view_activity_activity_serie_cast.setAdapter(actorAdapter);
                        recycle_view_activity_activity_serie_cast.setLayoutManager(linearLayoutManagerCast);
                        linear_layout_activity_serie_cast.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
            }
        });
    }

    /*private String lastPlayEpisodeTitle;
    private String lastPlaySeasonTitle;*/
    private int lastPlaySeasonId = -1;
    private int lastPlayEpisodeId = -1;

    private void getSerie() {
        poster = getIntent().getParcelableExtra("poster");
        from = getIntent().getStringExtra("from");

        JSONObject jsonObject = getSeasonEpisode(poster.getId());
        if (jsonObject != null) {
            try {
                lastPlaySeasonId = jsonObject.getInt("seasonId");
                lastPlayEpisodeId = jsonObject.getInt("episodeId");
                /*lastPlaySeasonTitle = jsonObject.getString("seasonTitle");
                lastPlayEpisodeTitle = jsonObject.getString("episodeTitle");*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setSerie() {
        Log.i(TAG, "MyClass.getView() — get item number " + poster.getImage());
        Glide.with(SerieActivity.this).load((poster.getCover() != null ? poster.getCover() : poster.getImage()))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        ImageLoader.with(SerieActivity.this).load(poster.getImage()).blur(25).into(image_view_activity_serie_background);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ImageLoader.with(SerieActivity.this).load(poster.getImage()).blur(25).into(image_view_activity_serie_background);
                        return false;
                    }
                })
                .apply(new RequestOptions()
                        .fitCenter()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .dontTransform()
                        .override(Target.SIZE_ORIGINAL))
                .into(image_view_activity_serie_cover);
        // TODO: 8/7/20 need to uncomment - replaced with imageloader

        /*final com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                BlurImage.with(getApplicationContext()).load(bitmap).intensity(25).Async(true).into(image_view_activity_serie_background);
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) { }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) { }
        };
        Picasso.get().load(poster.getImage()).into(target);
        image_view_activity_serie_background.setTag(target);*/

        ViewCompat.setTransitionName(image_view_activity_serie_cover, "imageMain");

        text_view_activity_serie_title.setText(poster.getTitle());
        text_view_activity_serie_sub_title.setText(poster.getTitle());
        text_view_activity_serie_description.setText(poster.getDescription());
        if (poster.getYear() != null) {
            if (!poster.getYear().isEmpty()) {
                text_view_activity_serie_year.setVisibility(View.VISIBLE);
                text_view_activity_serie_year.setText(poster.getYear());
            }
        }

        if (poster.getClassification() != null) {
            if (!poster.getClassification().isEmpty()) {
                text_view_activity_serie_classification.setVisibility(View.VISIBLE);
                text_view_activity_serie_classification.setText(poster.getClassification());
            }
        }

        if (poster.getDuration() != null) {
            if (!poster.getDuration().isEmpty()) {
                text_view_activity_serie_duration.setVisibility(View.VISIBLE);
                text_view_activity_serie_duration.setText(poster.getDuration());
            }
        }
        if (poster.getDuration() != null) {
            if (!poster.getDuration().isEmpty()) {
                text_view_activity_serie_duration.setVisibility(View.VISIBLE);
                text_view_activity_serie_duration.setText(poster.getDuration());
            }
        }

        if (poster.getImdb() != null) {
            if (!poster.getImdb().isEmpty()) {
                linear_layout_activity_serie_imdb_rating.setVisibility(View.VISIBLE);
                text_view_activity_serie_imdb_rating.setText(poster.getImdb());
            }
        }

        rating_bar_activity_serie_rating.setRating(poster.getRating());
        linear_layout_activity_serie_rating.setVisibility(poster.getRating() == 0 ? View.GONE : View.VISIBLE);

        this.linearLayoutManagerGenre = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.genreAdapter = new GenreAdapter(poster.getGenres(), this);
        recycle_view_activity_serie_genres.setHasFixedSize(true);
        recycle_view_activity_serie_genres.setAdapter(genreAdapter);
        recycle_view_activity_serie_genres.setLayoutManager(linearLayoutManagerGenre);

        if (poster.getTrailer() != null) {
            linear_layout_serie_activity_trailer.setVisibility(View.VISIBLE);
        }
//        if (poster.getComment()){
//            floating_action_button_activity_serie_comment.setVisibility(View.VISIBLE);
//        }else{
//            floating_action_button_activity_serie_comment.setVisibility(View.GONE);
//        }
    }

    private void initAction() {
        tvSpinnerSelectedItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e(TAG, "hasFocus: " + hasFocus);
                if (hasFocus) {
                    relativeSeriesList.setBackground(getDrawable(R.drawable.bg_main_focus_stroke_10));
                } else {
                    relativeSeriesList.setBackground(null);
                }
            }
        });
        spinner_activity_serie_season_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                performSeasonListSpinnerClick(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        linear_layout_serie_activity_share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                share();
//            }
//        });
        linear_layout_activity_serie_my_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFavotite();
            }
        });
        linear_layout_serie_activity_trailer.setOnClickListener(v -> {
            playTrailer();
        });
        linear_layout_serie_activity_trailer_clicked.setOnClickListener(v -> {
            playTrailer();
        });

        play.setOnClickListener(v -> {
            play();
        });
        action_button_activity_serie_play.setOnClickListener(v -> {
            play();
        });
        linear_layout_serie_activity_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog();
            }
        });

//        floating_action_button_activity_serie_comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showCommentsDialog();
//            }
//        });

        tvSpinnerSelectedItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                spinner_activity_serie_season_list.performClick();
                showCategoryMenu();
            }
        });
    }

    private void performSeasonListSpinnerClick(int position) {
        tvSpinnerSelectedItem.setText(seasonArrayList.get(position).getTitle());
        linearLayoutManagerEpisodes = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        episodeAdapter = new EpisodeAdapter(seasonArrayList.get(position).getEpisodes());
        recycle_view_activity_activity_serie_episodes.setHasFixedSize(true);
        recycle_view_activity_activity_serie_episodes.setAdapter(episodeAdapter);
        recycle_view_activity_activity_serie_episodes.setLayoutManager(linearLayoutManagerEpisodes);
        recycle_view_activity_activity_serie_episodes.setNestedScrollingEnabled(false);
    }

    private void showCategoryMenu() {
        categoryDropdownMenu = new RecyclerDropdownMenu(this, serieSeasonList, this, this);
        categoryDropdownMenu.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        categoryDropdownMenu.setWidth(getPxFromDp(150));
        categoryDropdownMenu.setOutsideTouchable(true);
        categoryDropdownMenu.setFocusable(true);
        //Get the height of 2/3rd of the height of the screen
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int height = (displayMetrics.heightPixels * 2) / 3;
        System.out.println("Height:" + height);

        //First we get the position of the menu icon in the screen
        int[] values = new int[2];
        tvSpinnerSelectedItem.getLocationInWindow(values);
        int positionOfIcon = values[1];
        //If the position of menu icon is in the bottom 2/3rd part of the screen then we provide menu height as offset
        // but in negative as we want to open our menu to the top
        if (positionOfIcon > height) {
            categoryDropdownMenu.showAsDropDown(tvSpinnerSelectedItem, 0, -280);
        } else {
            categoryDropdownMenu.showAsDropDown(tvSpinnerSelectedItem, 0, 0);
        }
        categoryDropdownMenu.showAsDropDown(tvSpinnerSelectedItem);
    }

    private int getPxFromDp(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void play() {
        if (selectedEpisode == null) {
            if (seasonArrayList != null) {
                if (seasonArrayList.size() > 0) {
                    boolean isFound = false;
                    for (Season season : seasonArrayList) {
                        if (season.getId() == lastPlaySeasonId) {
                            for (Episode episode : season.getEpisodes()) {
                                if (episode.getId() == lastPlayEpisodeId) {
                                    isFound = true;
                                    seasonSelectedPos = seasonArrayList.indexOf(season);
                                    setPlayableList(episode, 0);
                                }
                            }
                        }
                    }
                    if (!isFound) {
                        setPlayableList(seasonArrayList.get(0).getEpisodes().get(0), 0);
                    }
                }
            }
        }
        if (selectedEpisode == null) {
            showSourcesPlayDialog();
        } else {
            if (checkSUBSCRIBED()) {
                showSourcesPlayDialog();
            } else {
                if (selectedEpisode.getPlayas().equals("2")) {
                    showDialog(false);
                } else if (selectedEpisode.getPlayas().equals("3")) {
                    showDialog(true);
                    operationAfterAds = 200;
                } else {
                    showSourcesPlayDialog();
                }
            }
        }
    }



    public void playSource(int position) {
        if (!isPlayPerformed) {
            isPlayPerformed = true;

            addView();

            if (playableList.get(position).getType().equals("youtube")) {
                Intent intent = new Intent(SerieActivity.this, YoutubeActivity.class);
                intent.putExtra("url", playableList.get(position).getUrl());
                startActivity(intent);
                return;
            }
            if (playableList.get(position).getType().equals("embed")) {
                Intent intent = new Intent(SerieActivity.this, EmbedActivity.class);
                intent.putExtra("url", playableList.get(position).getUrl());
                startActivity(intent);
                return;
            }
            if (mCastSession == null) {
                // mCastSession = mSessionManager.getCurrentCastSession();
            }
            if (mCastSession != null) {
                loadSubtitles(position);
            } else {

                Intent intent = new Intent(SerieActivity.this, PlayerActivity.class);
                intent.putExtra("id", selectedEpisode.getId());
                intent.putExtra("isEpisode", true);
                intent.putExtra("url", playableList.get(position).getUrl());
                intent.putExtra("type", playableList.get(position).getType());
                intent.putExtra("kind", "episode");
                intent.putExtra("image", poster.getImage());
                intent.putExtra("title", poster.getTitle());
                intent.putExtra("seekTime", selectedEpisode.getMili());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                /*int seasonSelectedPos = spinner_activity_serie_season_list.getSelectedItemPosition();*/

                Season selectedSeason = seasonArrayList.get(seasonSelectedPos);
                intent.putExtra("subtitle", selectedSeason.getTitle() + " : " + selectedEpisode.getTitle());
                startActivityForResult(intent, 110);

                putSeasonEpisode(poster.getId(),
                        selectedSeason.getId(),
                        selectedEpisode.getId(),
                        selectedEpisode.getTitle(),
                        selectedSeason.getTitle());
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isPlayPerformed = false;
                }
            },500);

        }

    }

    public void addView() {

        final PrefManager prefManager = new PrefManager(this);
        if (!prefManager.getString(poster.getId() + "_episode_view").equals("true")) {
            prefManager.setString(poster.getId() + "_episode_view", "true");
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<Integer> call = service.addEpisodeView(selectedEpisode
                    .getId());
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {

                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });
        }

        List<Episode> episodes_watched = Hawk.get("episodes_watched");
        Boolean exist = false;
        if (episodes_watched == null) {
            episodes_watched = new ArrayList<>();
        }

        for (int i = 0; i < episodes_watched.size(); i++) {
            if (episodes_watched.get(i).getId().equals(selectedEpisode.getId())) {
                exist = true;
            }
        }
        if (exist == false) {
            episodes_watched.add(selectedEpisode);
            Hawk.put("episodes_watched", episodes_watched);
        }

        episodeAdapter.notifyDataSetChanged();
    }

    public void playTrailer() {
        if (poster.getTrailer().getType().equals("youtube")) {
            Intent intent = new Intent(SerieActivity.this, YoutubeActivity.class);
            intent.putExtra("url", poster.getTrailer().getUrl());
            startActivity(intent);
            return;
        }
        if (poster.getTrailer().getType().equals("embed")) {
            Intent intent = new Intent(SerieActivity.this, EmbedActivity.class);
            intent.putExtra("url", poster.getTrailer().getUrl());
            startActivity(intent);
            return;
        }

        if (mCastSession == null) {
            //mCastSession = mSessionManager.getCurrentCastSession();
        }
        if (mCastSession != null) {
            loadRemoteMedia(0, true);
        } else {
            Intent intent = new Intent(SerieActivity.this, PlayerActivity.class);
            intent.putExtra("url", poster.getTrailer().getUrl());
            intent.putExtra("type", poster.getTrailer().getType());
            intent.putExtra("image", poster.getImage());
            intent.putExtra("title", poster.getTitle());
            intent.putExtra("seekTime", poster.getMili());
            intent.putExtra("subtitle", poster.getTitle() + " Trailer");
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivityForResult(intent, 1000);
        }
    }

    public void rateDialog() {
        Dialog rateDialog = new Dialog(this,
                R.style.Theme_Dialog);
        rateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rateDialog.setCancelable(true);
        rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = rateDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        rateDialog.setContentView(R.layout.dialog_rate);
        final AppCompatRatingBar AppCompatRatingBar_dialog_rating_app = (AppCompatRatingBar) rateDialog.findViewById(R.id.AppCompatRatingBar_dialog_rating_app);
        final Button buttun_send = (Button) rateDialog.findViewById(R.id.buttun_send);
        final Button button_cancel = (Button) rateDialog.findViewById(R.id.button_cancel);
        final TextView text_view_rate_title = (TextView) rateDialog.findViewById(R.id.text_view_rate_title);
        text_view_rate_title.setText(getResources().getString(R.string.rate_this_serie_tv));
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
            }
        });
        buttun_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefManager prf = new PrefManager(getApplicationContext());
                if (prf.getString("LOGGED").toString().equals("TRUE")) {
                    Integer id_user = Integer.parseInt(prf.getString("ID_USER"));
                    String key_user = prf.getString("TOKEN_USER");
                    Retrofit retrofit = apiClient.getClient();
                    apiRest service = retrofit.create(apiRest.class);
                    Call<ApiResponse> call = service.addPosterRate(id_user + "", key_user, poster.getId(), AppCompatRatingBar_dialog_rating_app.getRating());
                    call.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getCode() == 200) {
                                    Toasty.success(SerieActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    if (response.body().getValues().size() > 0) {
                                        if (response.body().getValues().get(0).getName().equals("rate")) {
                                            linear_layout_activity_serie_imdb_rating.setVisibility(View.VISIBLE);
                                            rating_bar_activity_serie_rating.setRating(Float.parseFloat(response.body().getValues().get(0).getValue()));
                                        }
                                    }
                                } else {
                                    Toasty.error(SerieActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                            rateDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            rateDialog.dismiss();
                        }
                    });
                } else {
                    rateDialog.dismiss();
                    Intent intent = new Intent(SerieActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

                }
            }
        });
        rateDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    rateDialog.dismiss();
                }
                return true;
            }
        });
        rateDialog.show();

    }

    public void showCommentsDialog() {

        Dialog dialog = new Dialog(this,
                R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        dialog.setContentView(R.layout.dialog_comment);
        TextView text_view_comment_dialog_count = dialog.findViewById(R.id.text_view_comment_dialog_count);
        ImageView image_view_comment_dialog_close = dialog.findViewById(R.id.image_view_comment_dialog_close);
        ImageView image_view_comment_dialog_empty = dialog.findViewById(R.id.image_view_comment_dialog_empty);
        ImageView image_view_comment_dialog_add_comment = dialog.findViewById(R.id.image_view_comment_dialog_add_comment);
        ProgressBar progress_bar_comment_dialog_comments = dialog.findViewById(R.id.progress_bar_comment_dialog_comments);
        ProgressBar progress_bar_comment_dialog_add_comment = dialog.findViewById(R.id.progress_bar_comment_dialog_add_comment);
        EditText edit_text_comment_dialog_add_comment = dialog.findViewById(R.id.edit_text_comment_dialog_add_comment);
        RecyclerView recycler_view_comment_dialog_comments = dialog.findViewById(R.id.recycler_view_comment_dialog_comments);

        image_view_comment_dialog_empty.setVisibility(View.GONE);
        recycler_view_comment_dialog_comments.setVisibility(View.GONE);
        progress_bar_comment_dialog_comments.setVisibility(View.VISIBLE);
        commentAdapter = new CommentAdapter(commentList, SerieActivity.this);
        linearLayoutManagerComments = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recycler_view_comment_dialog_comments.setHasFixedSize(true);
        recycler_view_comment_dialog_comments.setAdapter(commentAdapter);
        recycler_view_comment_dialog_comments.setLayoutManager(linearLayoutManagerComments);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Comment>> call = service.getCommentsByPoster(poster.getId());
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        commentList.clear();
                        for (int i = 0; i < response.body().size(); i++)
                            commentList.add(response.body().get(i));

                        commentAdapter.notifyDataSetChanged();

                        text_view_comment_dialog_count.setText(commentList.size() + " Comments");
                        image_view_comment_dialog_empty.setVisibility(View.GONE);
                        recycler_view_comment_dialog_comments.setVisibility(View.VISIBLE);
                        progress_bar_comment_dialog_comments.setVisibility(View.GONE);
                        recycler_view_comment_dialog_comments.scrollToPosition(recycler_view_comment_dialog_comments.getAdapter().getItemCount() - 1);
                        recycler_view_comment_dialog_comments.scrollToPosition(recycler_view_comment_dialog_comments.getAdapter().getItemCount() - 1);
                    } else {
                        image_view_comment_dialog_empty.setVisibility(View.VISIBLE);
                        recycler_view_comment_dialog_comments.setVisibility(View.GONE);
                        progress_bar_comment_dialog_comments.setVisibility(View.GONE);
                    }
                } else {
                    image_view_comment_dialog_empty.setVisibility(View.VISIBLE);
                    recycler_view_comment_dialog_comments.setVisibility(View.GONE);
                    progress_bar_comment_dialog_comments.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                image_view_comment_dialog_empty.setVisibility(View.VISIBLE);
                recycler_view_comment_dialog_comments.setVisibility(View.GONE);
                progress_bar_comment_dialog_comments.setVisibility(View.GONE);
            }
        });

        image_view_comment_dialog_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text_comment_dialog_add_comment.getText().length() > 0) {
                    PrefManager prf = new PrefManager(SerieActivity.this.getApplicationContext());
                    if (prf.getString("LOGGED").toString().equals("TRUE")) {
                        Integer id_user = Integer.parseInt(prf.getString("ID_USER"));
                        String key_user = prf.getString("TOKEN_USER");
                        byte[] data = new byte[0];
                        String comment_final = "";
                        try {
                            data = edit_text_comment_dialog_add_comment.getText().toString().getBytes("UTF-8");
                            comment_final = Base64.encodeToString(data, Base64.DEFAULT);
                        } catch (UnsupportedEncodingException e) {
                            comment_final = edit_text_comment_dialog_add_comment.getText().toString();
                            e.printStackTrace();
                        }
                        progress_bar_comment_dialog_add_comment.setVisibility(View.VISIBLE);
                        image_view_comment_dialog_add_comment.setVisibility(View.GONE);
                        Retrofit retrofit = apiClient.getClient();
                        apiRest service = retrofit.create(apiRest.class);
                        Call<ApiResponse> call = service.addPosterComment(id_user + "", key_user, poster.getId(), comment_final);
                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.isSuccessful()) {
                                    if (response.body().getCode() == 200) {
                                        recycler_view_comment_dialog_comments.setVisibility(View.VISIBLE);
                                        image_view_comment_dialog_empty.setVisibility(View.GONE);
                                        Toasty.success(SerieActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        edit_text_comment_dialog_add_comment.setText("");
                                        String id = "";
                                        String content = "";
                                        String user = "";
                                        String image = "";

                                        for (int i = 0; i < response.body().getValues().size(); i++) {
                                            if (response.body().getValues().get(i).getName().equals("id")) {
                                                id = response.body().getValues().get(i).getValue();
                                            }
                                            if (response.body().getValues().get(i).getName().equals("content")) {
                                                content = response.body().getValues().get(i).getValue();
                                            }
                                            if (response.body().getValues().get(i).getName().equals("user")) {
                                                user = response.body().getValues().get(i).getValue();
                                            }
                                            if (response.body().getValues().get(i).getName().equals("image")) {
                                                image = response.body().getValues().get(i).getValue();
                                            }
                                        }
                                        Comment comment = new Comment();
                                        comment.setId(Integer.parseInt(id));
                                        comment.setUser(user);
                                        comment.setContent(content);
                                        comment.setImage(image);
                                        comment.setEnabled(true);
                                        comment.setCreated(getResources().getString(R.string.now_time));
                                        commentList.add(comment);
                                        commentAdapter.notifyDataSetChanged();
                                        text_view_comment_dialog_count.setText(commentList.size() + " Comments");

                                    } else {
                                        Toasty.error(SerieActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                recycler_view_comment_dialog_comments.scrollToPosition(recycler_view_comment_dialog_comments.getAdapter().getItemCount() - 1);
                                recycler_view_comment_dialog_comments.scrollToPosition(recycler_view_comment_dialog_comments.getAdapter().getItemCount() - 1);
                                commentAdapter.notifyDataSetChanged();
                                progress_bar_comment_dialog_add_comment.setVisibility(View.GONE);
                                image_view_comment_dialog_add_comment.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                progress_bar_comment_dialog_add_comment.setVisibility(View.GONE);
                                image_view_comment_dialog_add_comment.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        Intent intent = new Intent(SerieActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                    }
                }
            }
        });
        image_view_comment_dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showSourcesDownloadDialog() {
        if (ContextCompat.checkSelfPermission(SerieActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SerieActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }
        if (downloadableList.size() == 0) {
            Toasty.warning(getApplicationContext(), getResources().getString(R.string.no_source_available), Toast.LENGTH_LONG).show();
            return;
        }
        if (downloadableList.size() == 1) {
            DownloadSource(downloadableList.get(0));

            return;
        }

        Dialog download_source_dialog = new Dialog(this,
                R.style.Theme_Dialog);
        download_source_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        download_source_dialog.setCancelable(true);
        download_source_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = download_source_dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        download_source_dialog.setContentView(R.layout.dialog_download);

        RelativeLayout relative_layout_dialog_source_close = download_source_dialog.findViewById(R.id.relative_layout_dialog_source_close);
        RecyclerView recycle_view_activity_dialog_sources = download_source_dialog.findViewById(R.id.recycle_view_activity_dialog_sources);
        this.linearLayoutManagerDownloadSources = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        DownloadsAdapter sourceDownloadAdapter = new DownloadsAdapter();
        recycle_view_activity_dialog_sources.setHasFixedSize(true);
        recycle_view_activity_dialog_sources.setAdapter(sourceDownloadAdapter);
        recycle_view_activity_dialog_sources.setLayoutManager(linearLayoutManagerDownloadSources);

        relative_layout_dialog_source_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download_source_dialog.dismiss();
            }
        });
        download_source_dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    download_source_dialog.dismiss();
                }
                return true;
            }
        });
        download_source_dialog.show();


    }

    public void showSourcesPlayDialog() {
        if (playableList.size() == 0) {
            Toasty.warning(getApplicationContext(), getResources().getString(R.string.no_source_available), Toast.LENGTH_LONG).show();
            return;
        }
        if (playableList.size() == 1) {
            playSource(0);
            return;
        }

        Dialog play_source_dialog = new Dialog(this,
                R.style.Theme_Dialog);
        play_source_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        play_source_dialog.setCancelable(true);
        play_source_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = play_source_dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        play_source_dialog.setContentView(R.layout.dialog_sources);

        RelativeLayout relative_layout_dialog_source_close = play_source_dialog.findViewById(R.id.relative_layout_dialog_source_close);
        RecyclerView recycle_view_activity_dialog_sources = play_source_dialog.findViewById(R.id.recycle_view_activity_dialog_sources);
        this.linearLayoutManagerSources = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        SourceAdapter sourceAdapter = new SourceAdapter();
        recycle_view_activity_dialog_sources.setHasFixedSize(true);
        recycle_view_activity_dialog_sources.setAdapter(sourceAdapter);
        recycle_view_activity_dialog_sources.setLayoutManager(linearLayoutManagerSources);

        relative_layout_dialog_source_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_source_dialog.dismiss();
            }
        });
        play_source_dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    play_source_dialog.dismiss();
                }
                return true;
            }
        });
        play_source_dialog.show();


    }

    private void initView() {
//        if(getSupportActionBar() != null)
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        this.play = findViewById(R.id.play);
        play.setFocusableInTouchMode(true);
        play.requestFocus();
        this.tvPlayText = findViewById(R.id.tvPlayText);
        this.linear_layout_activity_serie_rating = (LinearLayout) findViewById(R.id.linear_layout_activity_serie_rating);
        this.linear_layout_activity_serie_imdb_rating = (LinearLayout) findViewById(R.id.linear_layout_activity_serie_imdb_rating);
        this.text_view_activity_serie_imdb_rating = (TextView) findViewById(R.id.text_view_activity_serie_imdb_rating);
        this.spinner_activity_serie_season_list = (AppCompatSpinner) findViewById(R.id.spinner_activity_serie_season_list);
        this.relativeSeriesList = (RelativeLayout) findViewById(R.id.relativeSeriesList);
        this.tvSpinnerSelectedItem = (TextView) findViewById(R.id.tvSpinnerSelectedItem);
//        this.linear_layout_serie_activity_share =  (LinearLayout) findViewById(R.id.linear_layout_serie_activity_share);
//        this.floating_action_button_activity_serie_comment =  (FloatingActionButton) findViewById(R.id.floating_action_button_activity_serie_comment);
        this.relative_layout_subtitles_loading = (RelativeLayout) findViewById(R.id.relative_layout_subtitles_loading);
        this.action_button_activity_serie_play = (LinearLayout) findViewById(R.id.floating_action_button_activity_serie_play);
        this.image_view_activity_serie_background = (ImageView) findViewById(R.id.image_view_activity_serie_background);
        this.image_view_activity_serie_cover = (ImageView) findViewById(R.id.image_view_activity_serie_cover);
        this.text_view_activity_serie_title = (TextView) findViewById(R.id.text_view_activity_serie_title);
        this.text_view_activity_serie_sub_title = (TextView) findViewById(R.id.text_view_activity_serie_sub_title);
        this.text_view_activity_serie_description = (TextView) findViewById(R.id.text_view_activity_serie_description);
        this.text_view_activity_serie_duration = (TextView) findViewById(R.id.text_view_activity_serie_duration);
        this.text_view_activity_serie_year = (TextView) findViewById(R.id.text_view_activity_serie_year);
        this.text_view_activity_serie_classification = (TextView) findViewById(R.id.text_view_activity_serie_classification);
        this.rating_bar_activity_serie_rating = (RatingBar) findViewById(R.id.rating_bar_activity_serie_rating);
        this.recycle_view_activity_serie_genres = (RecyclerView) findViewById(R.id.recycle_view_activity_serie_genres);
        this.recycle_view_activity_activity_serie_cast = (RecyclerView) findViewById(R.id.recycle_view_activity_activity_serie_cast);
        this.recycle_view_activity_activity_serie_episodes = (RecyclerView) findViewById(R.id.recycle_view_activity_activity_serie_episodes);
        this.linear_layout_activity_serie_cast = (LinearLayout) findViewById(R.id.linear_layout_activity_serie_cast);
        this.linear_layout_serie_activity_trailer = (LinearLayout) findViewById(R.id.linear_layout_serie_activity_trailer);
        this.linear_layout_serie_activity_trailer_clicked = (LinearLayout) findViewById(R.id.linear_layout_serie_activity_trailer_clicked);
        this.linear_layout_serie_activity_rate = (LinearLayout) findViewById(R.id.linear_layout_serie_activity_rate);
        this.linear_layout_activity_serie_seasons = (LinearLayout) findViewById(R.id.linear_layout_activity_serie_seasons);
        this.linear_layout_activity_serie_my_list = (LinearLayout) findViewById(R.id.linear_layout_activity_serie_my_list);
        this.image_view_activity_serie_my_list = (ImageView) findViewById(R.id.image_view_activity_serie_my_list);
        this.my_list = (LinearLayout) findViewById(R.id.my_list);
        this.rate = (LinearLayout) findViewById(R.id.rate);
    }

    private void loadRemoteMedia(int position, boolean autoPlay) {
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            //mCastSession = mSessionManager.getCurrentCastSession();
            mSessionManager.addSessionManagerListener(mSessionManagerListener);
            if (mCastSession == null) {
                //mCastSession = mSessionManager.getCurrentCastSession();
            }
            playTrailer();
            return;
        }

        remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
            @Override
            public void onStatusUpdated() {
                Log.d(TAG, "onStatusUpdated");
                if (remoteMediaClient.getMediaStatus() != null) {

                }
            }
        });
        remoteMediaClient.load(new MediaLoadRequestData.Builder()
                .setMediaInfo(getTrailerMediaInfos())
                .setAutoplay(autoPlay).build());
    }

    private void loadRemoteMediaSource(int position, boolean autoPlay) {
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            //mCastSession = mSessionManager.getCurrentCastSession();
            //mSessionManager.addSessionManagerListener(mSessionManagerListener);
            if (mCastSession == null) {
                // mCastSession = mSessionManager.getCurrentCastSession();
            }
            playSource(position);
            return;
        }

        remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
            @Override
            public void onStatusUpdated() {
                Log.d(TAG, "onStatusUpdated");
                if (remoteMediaClient.getMediaStatus() != null) {

                }
            }
        });
        remoteMediaClient.load(new MediaLoadRequestData.Builder()
                .setMediaInfo(getSourceMediaInfos(position))
                .setAutoplay(autoPlay).build());
    }


    @Override
    protected void onResume() {
        super.onResume();
        //mCastSession = mSessionManager.getCurrentCastSession();
        //mSessionManager.addSessionManagerListener(mSessionManagerListener);
    }

    @Override
    protected void onPause() {
        //mSessionManager.removeSessionManagerListener(mSessionManagerListener);
        mCastSession = null;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_cast, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);
        return true;
    }


    private MediaInfo getSourceMediaInfos(int position) {
        MediaMetadata serieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        serieMetadata.putString(MediaMetadata.KEY_TITLE, poster.getTitle());
        serieMetadata.putString(MediaMetadata.KEY_SUBTITLE, seasonArrayList.get(spinner_activity_serie_season_list.getSelectedItemPosition()).getTitle() + " : " + selectedEpisode.getTitle());

        serieMetadata.addImage(new WebImage(Uri.parse(poster.getImage())));
        serieMetadata.addImage(new WebImage(Uri.parse(poster.getImage())));
        List<MediaTrack> tracks = new ArrayList<>();

        for (int i = 0; i < subtitlesForCast.size(); i++) {
            tracks.add(buildTrack(i + 1, "text", "captions", subtitlesForCast.get(i).getUrl(), subtitlesForCast.get(i).getLanguage(), "en-US"));
        }

        MediaInfo mediaInfo = new MediaInfo.Builder(playableList.get(position).getUrl())
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(serieMetadata)
                .setMediaTracks(tracks)
                .build();
        return mediaInfo;
    }

    private MediaInfo getTrailerMediaInfos() {
        MediaMetadata serieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        serieMetadata.putString(MediaMetadata.KEY_TITLE, poster.getTitle());
        serieMetadata.putString(MediaMetadata.KEY_SUBTITLE, poster.getTitle() + " Trailer");

        serieMetadata.addImage(new WebImage(Uri.parse(poster.getImage())));
        serieMetadata.addImage(new WebImage(Uri.parse(poster.getImage())));


        List<MediaTrack> tracks = new ArrayList<>();
        MediaInfo mediaInfo = new MediaInfo.Builder(poster.getTrailer().getUrl())
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(serieMetadata)
                .setMediaTracks(tracks)
                .build();

        return mediaInfo;
    }

    private static MediaTrack buildTrack(long id, String type, String subType, String contentId,
                                         String name, String language) {

        int trackType = MediaTrack.TYPE_UNKNOWN;
        if ("text".equals(type)) {
            trackType = MediaTrack.TYPE_TEXT;
        } else if ("video".equals(type)) {
            trackType = MediaTrack.TYPE_VIDEO;
        } else if ("audio".equals(type)) {
            trackType = MediaTrack.TYPE_AUDIO;
        }

        int trackSubType = MediaTrack.SUBTYPE_NONE;
        if (subType != null) {
            if ("captions".equals(type)) {
                trackSubType = MediaTrack.SUBTYPE_CAPTIONS;
            } else if ("subtitle".equals(type)) {
                trackSubType = MediaTrack.SUBTYPE_SUBTITLES;
            }
        }

        return new MediaTrack.Builder(id, trackType)
                .setContentType(MediaFormat.MIMETYPE_TEXT_VTT)
                .setName(name)
                .setSubtype(trackSubType)
                .setContentId(contentId)
                .setLanguage(language).build();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            View newView = getCurrentFocus();
//            rate
            if (newView == rate) {
                rateDialog();
            } else if (newView == my_list) {
                addFavotite();
            }

        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            super.onBackPressed();
        }

        return false;
    }


    @Override
    public void onBackPressed() {
        if (from != null) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (from != null) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.SourceHolder> {


        @Override
        public SourceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_source_play, parent, false);
            SourceHolder mh = new SourceHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(SourceHolder holder, final int position) {
            holder.text_view_item_source_type.setText(playableList.get(position).getType());
            switch (playableList.get(position).getType()) {
                case "mov":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mov_file));
                    break;
                case "mp4":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mp4_file));
                    break;
                case "mkv":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mkv_file));
                    break;
                case "webm":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_webm_file));
                    break;
                case "m3u8":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_m3u_file));
                    break;
                case "youtube":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_youtube));
                    break;
                case "embed":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_embed_file));
                    break;
            }

            holder.text_view_item_source_source.setText(playableList.get(position).getUrl());
            holder.image_view_item_source_type_play.setOnClickListener(v -> {
                playSource(position);
            });

        }

        @Override
        public int getItemCount() {
            return playableList.size();
        }

        public class SourceHolder extends RecyclerView.ViewHolder {
            private final ImageView image_view_item_source_type_play;
            private final ImageView image_view_item_source_type_image;
            private final TextView text_view_item_source_source;
            private final TextView text_view_item_source_type;

            public SourceHolder(View itemView) {
                super(itemView);
                this.text_view_item_source_type = (TextView) itemView.findViewById(R.id.text_view_item_source_type);
                this.text_view_item_source_source = (TextView) itemView.findViewById(R.id.text_view_item_source_source);
                this.image_view_item_source_type_image = (ImageView) itemView.findViewById(R.id.image_view_item_source_type_image);
                this.image_view_item_source_type_play = (ImageView) itemView.findViewById(R.id.image_view_item_source_type_play);
            }
        }
    }

    public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.DownloadHolder> {

        @Override
        public DownloadHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_source_download, parent, false);
            DownloadHolder mh = new DownloadHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(DownloadHolder holder, final int position) {
            holder.text_view_item_source_type.setText(downloadableList.get(position).getType());
            switch (downloadableList.get(position).getType()) {
                case "mov":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mov_file));
                    break;
                case "mp4":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mp4_file));
                    break;
                case "mkv":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_mkv_file));
                    break;
                case "webm":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_webm_file));
                    break;
                case "m3u8":
                    holder.image_view_item_source_type_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_m3u_file));
                    break;
            }
            holder.text_view_item_source_source.setText(downloadableList.get(position).getUrl());
            holder.image_view_item_source_type_download.setOnClickListener(v -> {
                DownloadSource(downloadableList.get(position));

            });
        }

        @Override
        public int getItemCount() {
            return downloadableList.size();
        }

        public class DownloadHolder extends RecyclerView.ViewHolder {
            private final ImageView image_view_item_source_type_download;
            private final ImageView image_view_item_source_type_image;
            private final TextView text_view_item_source_source;
            private final TextView text_view_item_source_type;

            public DownloadHolder(View itemView) {
                super(itemView);
                this.text_view_item_source_type = (TextView) itemView.findViewById(R.id.text_view_item_source_type);
                this.text_view_item_source_source = (TextView) itemView.findViewById(R.id.text_view_item_source_source);
                this.image_view_item_source_type_image = (ImageView) itemView.findViewById(R.id.image_view_item_source_type_image);
                this.image_view_item_source_type_download = (ImageView) itemView.findViewById(R.id.image_view_item_source_type_download);
            }
        }
    }


    public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeHolder> {
        private List<Episode> episodeList;

        public EpisodeAdapter(List<Episode> episodeList) {
            this.episodeList = episodeList;
        }

        @Override
        public EpisodeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_episode, null);
            EpisodeHolder mh = new EpisodeHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(EpisodeHolder holder, final int position) {
            if (episodeList.get(position).getImage() != null) {
                ImageLoader.with(SerieActivity.this).load(episodeList.get(position).getImage()).into(holder.image_view_item_episode_thumbail);
            } else {
                ImageLoader.with(SerieActivity.this).load(poster.getImage()).into(holder.image_view_item_episode_thumbail);
            }
            holder.text_view_item_episode_title.setText(episodeList.get(position).getTitle());
            if (episodeList.get(position).getDuration() != null) {
                holder.text_view_item_episode_duration.setText(episodeList.get(position).getDuration());
            }

            if (episodeList.get(position).getId() == mSelectedID) {
                episodeList.get(position).setMili(getSeekTime);
                episodeList.get(position).setPercentage(String.valueOf(mProgressPercentage));
            }


            String lastTime = episodeList.get(position).getMili();
            if (lastTime != null && !lastTime.equalsIgnoreCase("0") && !lastTime.isEmpty()) {
                int seekTime = Integer.parseInt(episodeList.get(position).getPercentage());
                holder.progressBar.setProgress(seekTime);
                holder.progressBar.setVisibility(View.VISIBLE);
            } else {
                holder.progressBar.setVisibility(View.GONE);
            }

            List<Episode> episodes_watched = Hawk.get("episodes_watched");
            Boolean exist = false;
            if (episodes_watched == null) {
                episodes_watched = new ArrayList<>();
            }

            for (int i = 0; i < episodes_watched.size(); i++) {
                if (episodes_watched.get(i).getId().equals(episodeList.get(position).getId())) {
                    exist = true;
                }
            }

            if (exist) {
//                holder.image_view_item_episode_viewed.setVisibility(View.VISIBLE);
            } else {
//                holder.image_view_item_episode_viewed.setVisibility(View.GONE);

            }


            holder.text_view_item_episode_description.setText(episodeList.get(position).getDescription());
//            holder.image_view_item_episode_download.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    setDownloadableList(episodeList.get(position),position);
//                }
//            });
            holder.image_view_item_episode_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkSUBSCRIBED()) {
                        setPlayableList(episodeList.get(position), position);
                    } else {
                        if (episodeList.get(position).getPlayas().equals("2")) {
                            showDialog(false);
                        } else if (episodeList.get(position).getPlayas().equals("3")) {
                            showDialog(true);
                            operationAfterAds = 200;
                        } else {
                            setPlayableList(episodeList.get(position), position);
                        }
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return episodeList.size();
        }

        public class EpisodeHolder extends RecyclerView.ViewHolder {

            //            private final ImageView image_view_item_episode_viewed;
            private final ImageView image_view_item_episode_play;
            private final ImageView image_view_item_episode_thumbail;
            //            private final ImageView image_view_item_episode_download;
            private final TextView text_view_item_episode_duration;
            private final TextView text_view_item_episode_title;
            private final TextView text_view_item_episode_description;
            private final ProgressBar progressBar;

            public EpisodeHolder(View itemView) {
                super(itemView);
//                this.image_view_item_episode_viewed =(ImageView) itemView.findViewById(R.id.image_view_item_episode_viewed);
                this.image_view_item_episode_play = (ImageView) itemView.findViewById(R.id.image_view_item_episode_play);
//                this.image_view_item_episode_download =(ImageView) itemView.findViewById(R.id.image_view_item_episode_download);
                this.image_view_item_episode_thumbail = (ImageView) itemView.findViewById(R.id.image_view_item_episode_thumbail);
                this.text_view_item_episode_description = (TextView) itemView.findViewById(R.id.text_view_item_episode_description);
                this.text_view_item_episode_title = (TextView) itemView.findViewById(R.id.text_view_item_episode_title);
                this.text_view_item_episode_duration = (TextView) itemView.findViewById(R.id.text_view_item_episode_duration);
                this.progressBar = itemView.findViewById(R.id.progressBar);
                this.progressBar.getProgressDrawable().setColorFilter(
                        Color.parseColor("#ed1c24"), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }

    }

    private void loadSubtitles(int position) {
        if (subtitlesForCast.size() > 0) {
            loadRemoteMediaSource(position, true);
            return;
        }
        relative_layout_subtitles_loading.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Language>> call = service.getSubtitlesByEpisode(poster.getId());
        call.enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(Call<List<Language>> call, Response<List<Language>> response) {
                relative_layout_subtitles_loading.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        subtitlesForCast.clear();
                        for (int i = 0; i < response.body().size(); i++) {
                            for (int l = 0; l < response.body().get(i).getSubtitles().size(); l++) {
                                Subtitle subtitletocast = response.body().get(i).getSubtitles().get(l);
                                subtitletocast.setLanguage(response.body().get(i).getLanguage());
                                subtitlesForCast.add(subtitletocast);
                            }
                        }
                    }
                }
                loadRemoteMediaSource(position, true);
            }

            @Override
            public void onFailure(Call<List<Language>> call, Throwable t) {
                relative_layout_subtitles_loading.setVisibility(View.GONE);
                loadRemoteMediaSource(position, true);
            }
        });
    }

    private void checkFavorite() {
        List<Poster> favorites_list = Hawk.get("my_list");
        Boolean exist = false;
        if (favorites_list == null) {
            favorites_list = new ArrayList<>();
        }

        for (int i = 0; i < favorites_list.size(); i++) {
            if (favorites_list.get(i).getId().equals(poster.getId())) {
                exist = true;
            }
        }
        if (exist) {
            image_view_activity_serie_my_list.setImageResource(R.drawable.ic_close);
        } else {
            image_view_activity_serie_my_list.setImageResource(R.drawable.ic_check);
        }
    }

    private void addFavotite() {


        List<Poster> favorites_list = Hawk.get("my_list");
        Boolean exist = false;
        if (favorites_list == null) {
            favorites_list = new ArrayList<>();
        }
        int fav_position = -1;
        for (int i = 0; i < favorites_list.size(); i++) {
            if (favorites_list.get(i).getId().equals(poster.getId())) {
                exist = true;
                fav_position = i;
            }
        }
        if (exist == false) {
            favorites_list.add(poster);
            Hawk.put("my_list", favorites_list);
            image_view_activity_serie_my_list.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
            Toasty.info(this, "This serie has been added to your list", Toast.LENGTH_SHORT).show();
        } else {
            favorites_list.remove(fav_position);
            Hawk.put("my_list", favorites_list);
            image_view_activity_serie_my_list.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
            Toasty.warning(this, "This serie has been removed from your list", Toast.LENGTH_SHORT).show();
        }
    }

    public void share() {
        String shareBody = poster.getTitle() + "\n\n" + getResources().getString(R.string.get_this_serie_here) + "\n" + Global.API_URL.replace("api", "share") + poster.getId() + ".html";
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
        addShare();
    }

    public void addShare() {
        final PrefManager prefManager = new PrefManager(this);
        if (!prefManager.getString(poster.getId() + "_share").equals("true")) {
            prefManager.setString(poster.getId() + "_share", "true");
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<Integer> call = service.addPosterShare(poster.getId());
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {

                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onProgressUpdate(int progress) {

    }

    @Override
    public void onStartDownload(String url) {

    }

    @Override
    public void OnDownloadCompleted() {

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void DownloadSource(Source source) {

        switch (source.getType()) {
            case "mov": {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    DownloadQ(source);
                } else {
                    Download(source);
                }
            }
            break;
            case "mkv": {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    DownloadQ(source);
                } else {
                    Download(source);
                }
            }
            break;
            case "webm": {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    DownloadQ(source);
                } else {
                    Download(source);
                }
            }
            break;
            case "mp4": {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    DownloadQ(source);
                } else {
                    Download(source);
                }
            }
            break;
            case "m3u8":
                if (!isMyServiceRunning(DownloadService.class)) {

                    Intent intent = new Intent(SerieActivity.this, DownloadService.class);
                    intent.putExtra("type", "episode");
                    intent.putExtra("url", source.getUrl());
                    intent.putExtra("title", poster.getTitle() + " - " + selectedEpisode.getTitle());
                    intent.putExtra("image", selectedEpisode.getImage());
                    intent.putExtra("id", source.getId());
                    intent.putExtra("element", selectedEpisode.getId());
                    if (selectedEpisode.getDuration() != null)
                        intent.putExtra("duration", selectedEpisode.getDuration());
                    else
                        intent.putExtra("duration", "");

                    Toasty.info(this, "Download has been started ...", Toast.LENGTH_LONG).show();
                    startService(intent);
                    expandPanel(this);

                } else {
                    Toasty.warning(SerieActivity.this, "Multi-download not supported with m3u8 files. please wait !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private static void expandPanel(Context _context) {
        try {
            @SuppressLint("WrongConstant") Object sbservice = _context.getSystemService("statusbar");
            Class<?> statusbarManager;
            statusbarManager = Class.forName("android.app.StatusBarManager");
            Method showsb;
            if (Build.VERSION.SDK_INT >= 17) {
                showsb = statusbarManager.getMethod("expandNotificationsPanel");
            } else {
                showsb = statusbarManager.getMethod("expand");
            }
            showsb.invoke(sbservice);
        } catch (ClassNotFoundException _e) {
            _e.printStackTrace();
        } catch (NoSuchMethodException _e) {
            _e.printStackTrace();
        } catch (IllegalArgumentException _e) {
            _e.printStackTrace();
        } catch (IllegalAccessException _e) {
            _e.printStackTrace();
        } catch (InvocationTargetException _e) {
            _e.printStackTrace();
        }
    }

    public void showDialog(Boolean withAds) {
        // TODO: 11/9/20
     /*Dialog dialog = new Dialog(this,
                R.style.Theme_Dialog);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        dialog.setContentView(R.layout.dialog_subscribe);


        RelativeLayout relative_layout_watch_ads=(RelativeLayout) dialog.findViewById(R.id.relative_layout_watch_ads);
        TextView text_view_watch_ads=(TextView) dialog.findViewById(R.id.text_view_watch_ads);
        if (withAds){
            relative_layout_watch_ads.setVisibility(View.VISIBLE);
        }else{
            relative_layout_watch_ads.setVisibility(View.GONE);
        }
        relative_layout_watch_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedVideoAd.isLoaded()){
                    mRewardedVideoAd.show();
                }else{
                    autoDisplay =  true;
                    loadRewardedVideoAd();
                    Toasty.info(getApplicationContext(),"ADS LOADING...",Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
        TextView text_view_go_pro=(TextView) dialog.findViewById(R.id.text_view_go_pro);
        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SerieActivity.this,PaymentWebview.class);
                startActivityForResult(i, 101);
            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }
        });


        dialog.setOnShowListener(new DialogInterface.OnShowListener(){

            @Override
            public void onShow(DialogInterface dialog) {

                text_view_go_pro.setFocusable(true);
                text_view_go_pro.setFocusableInTouchMode(true);
                text_view_go_pro.requestFocus();
            }
        });
        dialog.show();*/

        // TODO: 11/9/20
        new SubscribeDialogFragment(this, withAds, R.layout.dialog_subscribe).show(getSupportFragmentManager(),
                SubscribeDialogFragment.class.getSimpleName());
    }

    public boolean checkSUBSCRIBED() {
        PrefManager prefManager = new PrefManager(getApplicationContext());
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showSourcesDownloadDialog();
                }
                return;
            }
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConn);
    }

    public void Download(Source source) {
        com.mystatus.nachos.Utils.Log.log("Android P<=");

        final int min = 1000;
        final int max = 9999;
        final int random = new Random().nextInt((max - min) + 1) + min;
        File fileName = new File(Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.download_foler) + "/", poster.getTitle().replace(" ", "_").replace(".", "").replaceAll("[^\\.A-Za-z0-9_]", "") + selectedEpisode.getTitle().replace(" ", "_").replaceAll("[^\\.A-Za-z0-9_]", "") + "_" + source.getId() + "_" + random + "." + source.getType());
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(source.getUrl()))
                .setTitle(poster.getTitle())// Title of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDescription("Downloading")// Description of the Download Notification
                .setVisibleInDownloadsUi(true)
                .setDestinationUri(Uri.fromFile(fileName));// Uri of the destination file

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
        if (!isMyServiceRunning(SerieActivity.class)) {
            startService(new Intent(SerieActivity.this, ToastService.class));
        }
        Toasty.info(this, "Download has been started ...", Toast.LENGTH_LONG).show();
        expandPanel(this);
        DownloadItem downloadItem = new DownloadItem(source.getId(), poster.getTitle() + " - " + selectedEpisode.getTitle(), "episode", Uri.fromFile(fileName).getPath(), selectedEpisode.getImage(), "", "", selectedEpisode.getId(), downloadId);
        if (selectedEpisode.getDuration() != null)
            downloadItem.setDuration(selectedEpisode.getDuration());
        else
            downloadItem.setDuration("");

        List<DownloadItem> my_downloads_temp = Hawk.get("my_downloads_temp");
        if (my_downloads_temp == null) {
            my_downloads_temp = new ArrayList<>();
        }
        for (int i = 0; i < my_downloads_temp.size(); i++) {
            if (my_downloads_temp.get(i).getId().equals(source.getId())) {
                my_downloads_temp.remove(my_downloads_temp.get(i));
                Hawk.put("my_downloads_temp", my_downloads_temp);
            }
        }
        my_downloads_temp.add(downloadItem);
        Hawk.put("my_downloads_temp", my_downloads_temp);
    }

    public void DownloadQ(Source source) {
        final int min = 1000;
        final int max = 9999;
        final int random = new Random().nextInt((max - min) + 1) + min;
        File fileName = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/", poster.getTitle().replace(" ", "_").replace(".", "").replaceAll("[^\\.A-Za-z0-9_]", "") + selectedEpisode.getTitle().replace(" ", "_").replaceAll("[^\\.A-Za-z0-9_]", "").replace(".", "") + "_" + source.getId() + "_" + random + "." + source.getType());
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(source.getUrl()))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(poster.getTitle())// Title of the Download Notification
                .setDescription("Downloading")// Description of the Download Notification
                .setVisibleInDownloadsUi(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, poster.getTitle().replace(" ", "_").replace(".", "").replaceAll("[^\\.A-Za-z0-9_]", "").replace(".", "_") + selectedEpisode.getTitle().replace(" ", "_").replaceAll("[^\\.A-Za-z0-9_]", "").replace(".", "") + "_" + source.getId() + "_" + random + "." + source.getType());// Uri of the destination file
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
        if (!isMyServiceRunning(SerieActivity.class)) {
            startService(new Intent(SerieActivity.this, ToastService.class));
        }
        Toasty.info(this, "Download has been started ...", Toast.LENGTH_LONG).show();
        expandPanel(this);
        DownloadItem downloadItem = new DownloadItem(source.getId(), poster.getTitle() + " - " + selectedEpisode.getTitle(), "episode", Uri.fromFile(fileName).getPath(), selectedEpisode.getImage(), "", "", selectedEpisode.getId(), downloadId);
        if (selectedEpisode.getDuration() != null)
            downloadItem.setDuration(selectedEpisode.getDuration());
        else
            downloadItem.setDuration("");

        List<DownloadItem> my_downloads_temp = Hawk.get("my_downloads_temp");
        if (my_downloads_temp == null) {
            my_downloads_temp = new ArrayList<>();
        }
        for (int i = 0; i < my_downloads_temp.size(); i++) {
            if (my_downloads_temp.get(i).getId().equals(source.getId())) {
                my_downloads_temp.remove(my_downloads_temp.get(i));
                Hawk.put("my_downloads_temp", my_downloads_temp);
            }
        }
        my_downloads_temp.add(downloadItem);
        Hawk.put("my_downloads_temp", my_downloads_temp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 110 && resultCode == RESULT_OK) {
                getSeekTime = data.getStringExtra("key");
                mSelectedID = data.getIntExtra("mSelectedID", 0);
                mProgressPercentage = data.getIntExtra("progressPercentage", 0);
                episodeAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
        }

    }

    @Override
    public void onSubscribeClicked() {
        Intent i = new Intent(SerieActivity.this, PaymentWebview.class);
        startActivityForResult(i, 101);
    }

    @Override
    public void onAdClicked() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        } else {
            autoDisplay = true;
            loadRewardedVideoAd();
            Toasty.info(getApplicationContext(), "ADS LOADING...", Toasty.LENGTH_LONG).show();
        }
    }


    @Override
    public <T> void onRecyclerItemClick(View view, int position, T object) {
        if (object instanceof FilterOrder) {
            performSeasonListSpinnerClick(position);
            categoryDropdownMenu.dismiss();
            seasonSelectedPos = position;
        }
    }

    @Override
    public void onFiltersFocusChanged(View view, boolean hasFocus) {

    }

    public void putSeasonEpisode(int poster, int season, int episode, String episodeTitle, String seasonTitle) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("posterId", poster);
            jsonObject.put("seasonId", season);
            jsonObject.put("episodeId", episode);
            jsonObject.put("episodeTitle", episodeTitle);
            jsonObject.put("seasonTitle", seasonTitle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new PrefManager(this).setString("Serie#" + poster, jsonObject.toString());
    }

    public JSONObject getSeasonEpisode(int posterId) {
        String value = new PrefManager(this).getString("Serie#" + posterId);
        if (!TextUtils.isEmpty(value)) {
            try {
                JSONObject jsonObject = new JSONObject(value);
                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
