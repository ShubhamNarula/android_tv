package com.mystatus.nachos.ui.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mystatus.nachos.Provider.PrefManager;
import com.mystatus.nachos.R;
import com.mystatus.nachos.Utils.imageloader.ImageLoader;
import com.mystatus.nachos.api.apiClient;
import com.mystatus.nachos.api.apiRest;
import com.mystatus.nachos.config.Global;
import com.mystatus.nachos.entity.ApiResponse;
import com.mystatus.nachos.entity.CheckIsSubscribe;
import com.mystatus.nachos.event.MoviesFilterEvent;
import com.mystatus.nachos.event.SeriesFilterEvent;
import com.mystatus.nachos.ui.activities.FragmentActivities.MovieFragmentActivity;
import com.mystatus.nachos.ui.activities.FragmentActivities.SerieFragmentActivity;
import com.mystatus.nachos.ui.activities.FragmentActivities.TvFragmentActivity;
import com.mystatus.nachos.ui.fragments.MoviesFragment;
import com.mystatus.nachos.ui.fragments.SeriesFragment;
import com.mystatus.nachos.ui.fragments.SubscribeDialogFragment;
import com.mystatus.nachos.ui.listener.SubscribeDialogListener;

import org.greenrobot.eventbus.EventBus;

import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SubscribeDialogListener {

    private static final String TAG = "MainActivity ----- : ";

    private static final String LOG_TAG = "iabv3";
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID = null;
    private ConsentForm form;
    public DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView text_view_name_nave_header;
    private CircleImageView circle_image_view_profile_nav_header;
    private ImageView image_view_profile_nav_header_bg;
    private Dialog rateDialog;
    private boolean FromLogin;
    private Dialog dialog;
    /*public static String currentSeekTIme = "0";*/
    private EditText edit_text_home_activity_search;
    private RelativeLayout relative_layout_home_activity_search_section;
    private ImageView image_view_activity_home_close_search;
    private ImageView image_view_activity_home_search;
    private ImageView image_view_activity_actors_back;


    private CardView poster_container;

    boolean flag = false;

    boolean flag2 = false;


    protected final void initBase() {


        initViews();
        initActions();
        firebaseSubscribe();
        initGDPR();
        checkIsSubscribe();
    }



    protected void initViews() {

        this.image_view_activity_home_close_search = findViewById(R.id.image_view_activity_home_close_search);
        this.image_view_activity_actors_back = findViewById(R.id.image_view_activity_actors_back);
        this.image_view_activity_home_search = findViewById(R.id.image_view_activity_home_search);
        relative_layout_home_activity_search_section = findViewById(R.id.relative_layout_home_activity_search_section);
        edit_text_home_activity_search = findViewById(R.id.edit_text_home_activity_search);


        drawer = findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                /*getCurrentFocus().clearFocus();*/
                drawerView.clearFocus();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        View focusedView = getCurrentFocus();
                    }
                }, 2000);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        this.navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerview = navigationView.getHeaderView(0);
        this.text_view_name_nave_header = headerview.findViewById(R.id.text_view_name_nave_header);
        this.circle_image_view_profile_nav_header = headerview.findViewById(R.id.circle_image_view_profile_nav_header);
        this.image_view_profile_nav_header_bg = headerview.findViewById(R.id.image_view_profile_nav_header_bg);

    }

    protected final void initActions() {
        image_view_activity_actors_back.setOnClickListener(v -> {
            relative_layout_home_activity_search_section.setVisibility(View.GONE);
            edit_text_home_activity_search.setText("");
        });
        edit_text_home_activity_search.setOnEditorActionListener((v, actionId, event) -> {
            if (edit_text_home_activity_search.getText().length() > 0) {
                Intent intent = new Intent(HomeBaseActivity.this, SearchActivity.class);
                intent.putExtra("query", edit_text_home_activity_search.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

                relative_layout_home_activity_search_section.setVisibility(View.GONE);
                edit_text_home_activity_search.setText("");
            }
            return false;
        });
        image_view_activity_home_close_search.setOnClickListener(v -> {
            edit_text_home_activity_search.setText("");
        });
        image_view_activity_home_search.setOnClickListener(v -> {
            if (edit_text_home_activity_search.getText().length() > 0) {

                Intent intent = new Intent(HomeBaseActivity.this, SearchActivity.class);
                intent.putExtra("query", edit_text_home_activity_search.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                relative_layout_home_activity_search_section.setVisibility(View.GONE);
                edit_text_home_activity_search.setText("");
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_search) {
            Intent intent = new Intent(HomeBaseActivity.this, SearchActivity.class);
            intent.putExtra("query", "");
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.END);
        } else if (id == R.id.nav_home) {
            /*FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            drawer.closeDrawer(GravityCompat.END);*/
            startActivity(new Intent(this, HomeActivity.class));
            drawer.closeDrawer(GravityCompat.END);
            finish();
        } else if (id == R.id.nav_movies) {
            /*FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new MoviesFragment()).commit();
            drawer.closeDrawer(GravityCompat.END);*/
            startActivity(new Intent(this, MovieFragmentActivity.class));
            drawer.closeDrawer(GravityCompat.END);
            finish();
        } else if (id == R.id.nav_series) {
            /*FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new SeriesFragment()).commit();*/
            startActivity(new Intent(this, SerieFragmentActivity.class));
            drawer.closeDrawer(GravityCompat.END);
            finish();
            /*drawer.closeDrawer(GravityCompat.END);
            finish();*/
        } else if (id == R.id.nav_tv) {
            /*FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new TvFragment()).commit();
            drawer.closeDrawer(GravityCompat.END);*/
            startActivity(new Intent(this, TvFragmentActivity.class));
            drawer.closeDrawer(GravityCompat.END);
            finish();
        } else if (id == R.id.login) {
            Intent intent = new Intent(HomeBaseActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            FromLogin = true;
        } else if (id == R.id.nav_exit) {
            super.onBackPressed();
//            final PrefManager prf = new PrefManager(getApplicationContext());
//            if (prf.getString("NOT_RATE_APP").equals("TRUE")) {
//                super.onBackPressed();
//            } else {
//                rateDialog(true);
//            }

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.my_profile) {
            PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("LOGGED").toString().equals("TRUE")) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("id", Integer.parseInt(prf.getString("ID_USER")));
                intent.putExtra("image", prf.getString("IMAGE_USER").toString());
                intent.putExtra("name", prf.getString("NAME_USER").toString());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

            } else {
                Intent intent = new Intent(HomeBaseActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

                FromLogin = true;
            }
        } else if (id == R.id.logout) {
            logout();
        } else if (id == R.id.nav_share) {
            final String appPackageName = getApplication().getPackageName();
            String shareBody = "Download " + getString(R.string.app_name) + " From :  " + "http://play.google.com/store/apps/details?id=" + appPackageName;
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
        } else if (id == R.id.nav_rate) {
            rateDialog(false);
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(HomeBaseActivity.this, SupportActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

        } else if (id == R.id.nav_policy) {
            Intent intent = new Intent(getApplicationContext(), PolicyActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.buy_now) {
            if (checkSUBSCRIBED()) {
                Intent i = new Intent(HomeBaseActivity.this, CancelSubscriptioActivity.class);
                startActivityForResult(i, 101);
            } else {
                /*Intent i = new Intent(HomeActivity.this,PaymentWebview.class);
                startActivityForResult(i, 101);*/
                showDialog();
            }
            //


        }

        return false;
    }

    public boolean checkSUBSCRIBED() {
        PrefManager prefManager = new PrefManager(getApplicationContext());
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            return false;
        }
        return true;
    }



    public void logout() {

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        PrefManager prf = new PrefManager(getApplicationContext());

        String unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String user_id = prf.getString("USERN_USER");

        Call<ApiResponse> call = service.logout(unique_id, user_id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful())
                    prf.remove("ID_USER");
                prf.remove("SALT_USER");
                prf.remove("TOKEN_USER");
                prf.remove("NAME_USER");
                prf.remove("TYPE_USER");
                prf.remove("USERN_USER");
                prf.remove("IMAGE_USER");
                prf.remove("LOGGED");
                if (prf.getString("LOGGED").toString().equals("TRUE")) {
                    text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
                    ImageLoader.with(HomeBaseActivity.this).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
                    if (prf.getString("TYPE_USER").toString().equals("google")) {
                    } else {
                    }
                } else {
                    Menu nav_Menu = navigationView.getMenu();
                    nav_Menu.findItem(R.id.my_profile).setVisible(false);
                    nav_Menu.findItem(R.id.logout).setVisible(false);
                    nav_Menu.findItem(R.id.login).setVisible(true);
                    text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
                    ImageLoader.with(HomeBaseActivity.this).load(R.drawable.placeholder_profile).placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
                }
                image_view_profile_nav_header_bg.setVisibility(View.GONE);
                Toasty.info(getApplicationContext(), getString(R.string.message_logout), Toast.LENGTH_LONG).show();
                startActivity(new Intent(HomeBaseActivity.this, LoginNewActivity.class));


            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.v("HomeActivity", "onFailure : " + t.getMessage().toString());
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("OnKeyDown", keyCode + "");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (navigationView.getCheckedItem() != null && navigationView.getCheckedItem().getItemId() == R.id.nav_series && SeriesFragment.isOpen) {
                EventBus.getDefault().post(new SeriesFilterEvent());
            } else if (navigationView.getCheckedItem() != null && navigationView.getCheckedItem().getItemId() == R.id.nav_movies && MoviesFragment.isOpen) {
                EventBus.getDefault().post(new MoviesFilterEvent());
            } else {
                drawer.openDrawer(Gravity.RIGHT);
                drawer.setScrimColor(getResources().getColor(android.R.color.transparent));
                navigationView.requestFocus();
            }


        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            Log.e("OnKeyDown", "Drawer");
            if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                Log.e("OnKeyDown", "Drawer Close");
                drawer.closeDrawer(Gravity.RIGHT);
                navigationView.clearFocus();
            }
        }
        if (event.getAction() == KeyEvent.ACTION_UP) {
            canMove = true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (!canMove) return true;
            canMove = false;
            new Handler().postDelayed(() -> canMove = true, 100);
        }


        return false;
    }


    public void checkIsSubscribe() {
        System.out.println("SHUBHAM--INN");
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        PrefManager prf = new PrefManager(getApplicationContext());
        String email = prf.getString("USERN_USER");
        String URL = "http://payments.adminpanel.esy.es/Pay/get_user_status/" + email;

        Call<CheckIsSubscribe> call = service.checkSubscribe(URL);
        call.enqueue(new Callback<CheckIsSubscribe>() {
            @Override
            public void onResponse(Call<CheckIsSubscribe> call, Response<CheckIsSubscribe> response) {
                if (response.body() != null) {
                    System.out.println("SHUBHAM--" + response.body().getStatus());
                    if (response.body().getStatus().equalsIgnoreCase("active")) {
                        PrefManager prefManager = new PrefManager(getApplicationContext());
                        prefManager.setString("SUBSCRIBED", "TRUE");
                    } else {
                        PrefManager prefManager = new PrefManager(getApplicationContext());
                        prefManager.setString("SUBSCRIBED", "FALSE");
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckIsSubscribe> call, Throwable t) {
                System.out.println("SHUBHAM--" + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            final PrefManager prf = new PrefManager(getApplicationContext());
//            if (prf.getString("NOT_RATE_APP").equals("TRUE")) {
//                super.onBackPressed();
//            } else {
//                rateDialog(true);
//                return;
//            }

            super.onBackPressed();
        }

    }

    public void showDialog() {
        // TODO: 11/9/20
        /*this.dialog = new Dialog(this,
                R.style.Theme_Dialog);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        final PrefManager prf = new PrefManager(getApplicationContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_subscribe);

        TextView text_view_go_pro = (TextView) dialog.findViewById(R.id.text_view_go_pro);
        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeBaseActivity.this, PaymentWebview.class);
                startActivityForResult(i, 101);
                dialog.dismiss();
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

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                text_view_go_pro.setFocusable(true);
                text_view_go_pro.setFocusableInTouchMode(true);
                text_view_go_pro.requestFocus();
            }
        });
        dialog.show();*/

        // TODO: 11/9/20
        new SubscribeDialogFragment(this, false, R.layout.dialog_subscribe).show(getSupportFragmentManager(),
                SubscribeDialogFragment.class.getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefManager prf = new PrefManager(getApplicationContext());
        Menu nav_Menu = navigationView.getMenu();

        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            nav_Menu.findItem(R.id.my_profile).setVisible(true);

            nav_Menu.findItem(R.id.logout).setVisible(true);
            nav_Menu.findItem(R.id.login).setVisible(false);
            text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
            ImageLoader.with(HomeBaseActivity.this).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);

            // TODO: 8/7/20 need to uncomment - replaced with imageloader
            ImageLoader.with(this).load(prf.getString("IMAGE_USER")).blur(25).into(image_view_profile_nav_header_bg);
            /*final com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    BlurImage.with(getApplicationContext()).load(bitmap).intensity(25).Async(true).into(image_view_profile_nav_header_bg);
                }
                @Override
                public void onBitmapFailed(Drawable errorDrawable) { }
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) { }
            };*/
            /*Picasso.get().load(prf.getString("IMAGE_USER").toString()).into(target);
            image_view_profile_nav_header_bg.setTag(target);*/
            image_view_profile_nav_header_bg.setVisibility(View.VISIBLE);

        } else {
            nav_Menu.findItem(R.id.my_profile).setVisible(false);
            nav_Menu.findItem(R.id.logout).setVisible(false);
            nav_Menu.findItem(R.id.login).setVisible(true);
            image_view_profile_nav_header_bg.setVisibility(View.GONE);

            text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
            ImageLoader.with(HomeBaseActivity.this).load(R.drawable.placeholder_profile).placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
        }
        if (FromLogin) {
            FromLogin = false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                edit_text_home_activity_search.requestFocus();
                relative_layout_home_activity_search_section.setVisibility(View.VISIBLE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void rateDialog(final boolean close) {
        this.rateDialog = new Dialog(this, R.style.Theme_Dialog);

        rateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rateDialog.setCancelable(true);
        rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = rateDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        final PrefManager prf = new PrefManager(getApplicationContext());
        rateDialog.setCancelable(false);
        rateDialog.setContentView(R.layout.dialog_rating_app);
        final AppCompatRatingBar AppCompatRatingBar_dialog_rating_app = (AppCompatRatingBar) rateDialog.findViewById(R.id.AppCompatRatingBar_dialog_rating_app);
        final LinearLayout linear_layout_feedback = (LinearLayout) rateDialog.findViewById(R.id.linear_layout_feedback);
        final LinearLayout linear_layout_rate = (LinearLayout) rateDialog.findViewById(R.id.linear_layout_rate);
        final Button buttun_send_feedback = (Button) rateDialog.findViewById(R.id.buttun_send_feedback);
        final Button button_later = (Button) rateDialog.findViewById(R.id.button_later);
        final Button button_never = (Button) rateDialog.findViewById(R.id.button_never);
        final Button button_cancel = (Button) rateDialog.findViewById(R.id.button_cancel);
        button_never.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prf.setString("NOT_RATE_APP", "TRUE");
                rateDialog.dismiss();
                if (close)
                    finish();
            }
        });
        button_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
                if (close)
                    finish();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
                if (close)
                    finish();
            }
        });
        final EditText edit_text_feed_back = (EditText) rateDialog.findViewById(R.id.edit_text_feed_back);
        buttun_send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prf.setString("NOT_RATE_APP", "TRUE");
                Retrofit retrofit = apiClient.getClient();
                apiRest service = retrofit.create(apiRest.class);
                Call<ApiResponse> call = service.addSupport("Application rating feedback", AppCompatRatingBar_dialog_rating_app.getRating() + " star(s) Rating".toString(), edit_text_feed_back.getText().toString());
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            Toasty.success(getApplicationContext(), getResources().getString(R.string.rating_done), Toast.LENGTH_SHORT).show();
                        } else {
                            Toasty.error(getApplicationContext(), getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                        }
                        rateDialog.dismiss();

                        if (close)
                            finish();

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toasty.error(getApplicationContext(), getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                        rateDialog.dismiss();

                        if (close)
                            finish();
                    }
                });
            }
        });
        AppCompatRatingBar_dialog_rating_app.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    if (rating > 3) {
                        final String appPackageName = getApplication().getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        prf.setString("NOT_RATE_APP", "TRUE");
                        rateDialog.dismiss();
                    } else {
                        linear_layout_feedback.setVisibility(View.VISIBLE);
                        linear_layout_rate.setVisibility(View.GONE);
                    }
                } else {

                }
            }
        });


        rateDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    rateDialog.dismiss();
                    if (close)
                        finish();
                }
                return true;

            }
        });
        rateDialog.show();

    }

    protected final void initGDPR() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        ConsentInformation consentInformation =
                ConsentInformation.getInstance(HomeBaseActivity.this);
//// test
/////
        String[] publisherIds = {getResources().getString(R.string.publisher_id)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new
                ConsentInfoUpdateListener() {
                    @Override
                    public void onConsentInfoUpdated(ConsentStatus consentStatus) {
// User's consent status successfully updated.
                        Log.d(TAG, "onConsentInfoUpdated");
                        switch (consentStatus) {
                            case PERSONALIZED:
                                Log.d(TAG, "PERSONALIZED");
                                ConsentInformation.getInstance(HomeBaseActivity.this)
                                        .setConsentStatus(ConsentStatus.PERSONALIZED);
                                break;
                            case NON_PERSONALIZED:
                                Log.d(TAG, "NON_PERSONALIZED");
                                ConsentInformation.getInstance(HomeBaseActivity.this)
                                        .setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                                break;


                            case UNKNOWN:
                                Log.d(TAG, "UNKNOWN");
                                if
                                (ConsentInformation.getInstance(HomeBaseActivity.this).isRequestLocationInEeaOrUnknown
                                        ()) {
                                    URL privacyUrl = null;
                                    try {
// TODO: Replace with your app's privacy policy URL.
                                        privacyUrl = new URL(Global.API_URL.replace("/api/", "/privacy_policy.html"));

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
// Handle error.

                                    }
                                    form = new ConsentForm.Builder(HomeBaseActivity.this,
                                            privacyUrl)
                                            .withListener(new ConsentFormListener() {
                                                @Override
                                                public void onConsentFormLoaded() {
                                                    Log.d(TAG, "onConsentFormLoaded");
                                                    showform();
                                                }

                                                @Override
                                                public void onConsentFormOpened() {
                                                    Log.d(TAG, "onConsentFormOpened");
                                                }

                                                @Override
                                                public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                                                    Log.d(TAG, "onConsentFormClosed");
                                                }

                                                @Override
                                                public void onConsentFormError(String errorDescription) {
                                                    Log.d(TAG, "onConsentFormError");
                                                    Log.d(TAG, errorDescription);
                                                }
                                            })
                                            .withPersonalizedAdsOption()
                                            .withNonPersonalizedAdsOption()
                                            .build();
                                    form.load();
                                } else {
                                    Log.d(TAG, "PERSONALIZED else");
                                    ConsentInformation.getInstance(HomeBaseActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);
                                }
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFailedToUpdateConsentInfo(String errorDescription) {
// User's consent status failed to update.
                        Log.d(TAG, "onFailedToUpdateConsentInfo");
                        Log.d(TAG, errorDescription);
                    }
                });
    }

    protected final void firebaseSubscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic("Flixo")
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Retrofit retrofit = apiClient.getClient();
                        apiRest service = retrofit.create(apiRest.class);
                        PrefManager prf = new PrefManager(getApplicationContext());

                        String unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                        String user_id = prf.getString("USERN_USER");

                        Call<ApiResponse> call = service.addDevice(unique_id, user_id);
                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.isSuccessful())
                                    Log.v("HomeActivity", "Added : " + response.body().getMessage());
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                Log.v("HomeActivity", "onFailure : " + t.getMessage().toString());
                            }
                        });
                    }
                });

    }

    private void showform() {
        if (form != null) {
            form.show();
        }
    }

    public void setSelectedNav(int menuItem) {
        navigationView.setNavigationItemSelectedListener(null);
        navigationView.setCheckedItem(menuItem);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onSubscribeClicked() {
        Intent i = new Intent(HomeBaseActivity.this, PaymentWebview.class);
        startActivityForResult(i, 101);
        dialog.dismiss();
    }

    @Override
    public void onAdClicked() {

    }

    private boolean canMove = true;


}
