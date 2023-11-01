package com.mystatus.nachos.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import androidx.core.content.FileProvider;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mystatus.nachos.BuildConfig;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Environment;
import android.content.BroadcastReceiver;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.greenfrvr.rubberloader.RubberLoaderView;
import com.mystatus.nachos.Provider.PrefManager;
import com.mystatus.nachos.R;
import com.mystatus.nachos.api.apiClient;
import com.mystatus.nachos.api.apiRest;
import com.mystatus.nachos.config.Global;
import com.mystatus.nachos.entity.ApiResponse;
import com.mystatus.nachos.entity.Downloadupdate;
import com.mystatus.nachos.services.ToastService;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.DownloadManager;



public class SplashActivity<bytes_downloaded> extends AppCompatActivity  {

    protected ProgressBar mProgressBar;


    private PrefManager prf;
    IInAppBillingService mService;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;
    private static final String LOG_TAG = "iabv3";
    private static final String MERCHANT_ID=null;
    private DownloadManager downloadManager;
    Context context = this;
    private long downloadID;
    private ProgressBar progressBarAnimation;
    private ObjectAnimator progressAnimator;


    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        prf= new PrefManager(getApplicationContext());
        initBuy();
        ( (RubberLoaderView) findViewById(R.id.loader1)).startLoading();
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // If you want to modify a view in your Activity
                SplashActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                       checkAccount();
//                          eldad_build();

                    }
                });
            }
        }, 3000);

        prf.setString("ADMIN_REWARDED_ADMOB_ID","");

        prf.setString("ADMIN_INTERSTITIAL_ADMOB_ID","");
        prf.setString("ADMIN_INTERSTITIAL_FACEBOOK_ID","");
        prf.setString("ADMIN_INTERSTITIAL_TYPE","FALSE");
        prf.setInt("ADMIN_INTERSTITIAL_CLICKS",3);

        prf.setString("ADMIN_BANNER_ADMOB_ID","");
        prf.setString("ADMIN_BANNER_FACEBOOK_ID","");
        prf.setString("ADMIN_BANNER_TYPE","FALSE");

        prf.setString("ADMIN_NATIVE_FACEBOOK_ID","");
        prf.setString("ADMIN_NATIVE_ADMOB_ID","");
        prf.setString("ADMIN_NATIVE_LINES","6");
        prf.setString("ADMIN_NATIVE_TYPE","FALSE");
    }



//    private void eldad_build() {
//
////        prf.setString("ID_USER", id_user);
////        prf.setString("SALT_USER", salt_user);
////        prf.setString("TOKEN_USER", token_user);
//        prf.setString("NAME_USER", "eldad");
//        prf.setString("SUBSCRIBED", "TRUE");
////        prf.setString("IMAGE_USER", image_user);
//        prf.setString("LOGGED", "TRUE");
//
//
//        Intent intent;
//        intent = new Intent(SplashActivity.this, HomeActivity.class);
//        startActivity(intent);
//        finish();
//    }



    private void checkAccount() {

        Integer version = -1;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (version!=-1){
            Integer id_user = 0;

            if (prf.getString("LOGGED").toString().equals("TRUE")) {
                 id_user = Integer.parseInt(prf.getString("ID_USER"));
            }
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<ApiResponse> call = service.check(version,id_user);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    updateTextViews();
                    if (response.isSuccessful()){
                        for (int i = 0; i < response.body().getValues().size(); i++) {
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_REWARDED_ADMOB_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_REWARDED_ADMOB_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_ADMOB_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_INTERSTITIAL_ADMOB_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_FACEBOOK_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_INTERSTITIAL_FACEBOOK_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_TYPE") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_INTERSTITIAL_TYPE",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_INTERSTITIAL_CLICKS") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setInt("ADMIN_INTERSTITIAL_CLICKS",Integer.parseInt(response.body().getValues().get(i).getValue()));
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_BANNER_ADMOB_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_BANNER_ADMOB_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_BANNER_FACEBOOK_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_BANNER_FACEBOOK_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_BANNER_TYPE") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_BANNER_TYPE",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_FACEBOOK_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_NATIVE_FACEBOOK_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_ADMOB_ID") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_NATIVE_ADMOB_ID",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_LINES") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_NATIVE_LINES",response.body().getValues().get(i).getValue());
                            }
                            if ( response.body().getValues().get(i).getName().equals("ADMIN_NATIVE_TYPE") ){
                                if (response.body().getValues().get(i).getValue()!=null)
                                    prf.setString("ADMIN_NATIVE_TYPE",response.body().getValues().get(i).getValue());
                            }
                        }
                        if (response.body().getValues().get(1).getValue().equals("403")){
                            prf.remove("ID_USER");
                            prf.remove("SALT_USER");
                            prf.remove("TOKEN_USER");
                            prf.remove("NAME_USER");
                            prf.remove("TYPE_USER");
                            prf.remove("USERN_USER");
                            prf.remove("IMAGE_USER");
                            prf.remove("LOGGED");
                            Toasty.error(getApplicationContext(),getResources().getString(R.string.account_disabled), Toast.LENGTH_SHORT, true).show();
                        }
                        if (response.body().getCode().equals(200)) {
                            if (!prf.getString("first").equals("true")){
                                Intent intent = new Intent(SplashActivity.this,IntroActivity.class);
                                startActivity(intent);
                               
                                finish();
                                prf.setString("first","true");
                            }else{
                                Intent intent;
                                if(prf.getString("LOGGED").toString().equals("TRUE")){
                                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                                }else{
                                    intent = new Intent(SplashActivity.this, LoginNewActivity.class);
                                }
                                startActivity(intent);
                               
                                finish();
                            }
                        }else if (response.body().getCode().equals(202)) {
                            String title_update=response.body().getValues().get(0).getValue();
                            String featurs_update=response.body().getMessage();
                            View v = (View)  getLayoutInflater().inflate(R.layout.update_message,null);
                            TextView update_text_view_title=(TextView) v.findViewById(R.id.update_text_view_title);
                            TextView update_text_view_updates=(TextView) v.findViewById(R.id.update_text_view_updates);
                            update_text_view_title.setText(title_update);
                            update_text_view_updates.setText(featurs_update);
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(SplashActivity.this);
                            builder.setTitle("עדכון חדש!!!")
                                    //.setMessage(response.body().getValue())
                                    .setView(v)
                                    .setPositiveButton(getResources().getString(R.string.update_now), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            ProgressDialog pd = new ProgressDialog(SplashActivity.this);
                                            pd.setMessage("מוריד עדכון");
//                                            pd.show();

                                            final String appPackageName=getApplication().getPackageName();
                                            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"app-release.apk");
                                            /*  Create a DownloadManager.Request with all the information necessary to start the download*/
                                            if (file.exists())
                                                file.delete();
                                            DownloadManager.Request request=new DownloadManager.Request(Uri.parse("https://nachosisrael.com/app-release.apk"))
                                                    .setTitle("app-release.apk")// Title of the Download Notification
                                                    .setDescription("מוריד")// Description of the Download Notification
                                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                                                    .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                                                    //.setRequiresCharging(false)// Set if charging is required to begin the download
                                                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                                                    .setAllowedOverRoaming(true);// Set if download is allowed on roaming network

                                            DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                            final long downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
                                            Timer myTimer = new Timer();
                                            myTimer.schedule(new TimerTask() {
                                                @Override
                                            public void run() {
                                                DownloadManager.Query q = new DownloadManager.Query();
                                                q.setFilterById(downloadID);
                                                mProgressBar = findViewById(R.id.progress_bar);
                                                Cursor cursor = downloadManager.query(q);
                                                cursor.moveToFirst();
                                                int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                                cursor.close();
                                                double dl_progress = (bytes_downloaded * 1f / bytes_total) * 100;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mProgressBar.setProgress((int) dl_progress);
                                                    }
                                                });
                                            }
                                            }, 0, 10);

                                        BroadcastReceiver  onComplete = new BroadcastReceiver () {
                                                @Override
                                                public void onReceive (Context Context, Intent intent) {
                                                   if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

                                                       pd.hide();
                                                       if(prf.getString("LOGGED").toString().equals("TRUE")){
                                                           intent = new Intent(SplashActivity.this, HomeActivity.class);
                                                       }else{
                                                           intent = new Intent(SplashActivity.this, LoginNewActivity.class);
                                                       }                                                           startActivity(intent);

                                                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                                           intent = new Intent(Intent.ACTION_VIEW);
                                                           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                           intent.setDataAndType(downloadManager.getUriForDownloadedFile(downloadID),
                                                                   "application/vnd.android.package-archive");
                                                           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                           intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                           context.sendBroadcast(intent);
                                                           startActivity(intent);
                                                           //context.unregisterReceiver(this);
                                                           finish();
                                                       } else {


                                                           intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
                                                           Uri data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                                                           intent.setDataAndType(data, "/*");
                                                           context.sendBroadcast(intent);
                                                           intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                           startActivity(intent);
                                                           finish();
                                                       }
                                                   }

                                                }
                                            };
                                            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


                                                }

                                    })
                                    // TODO: 9/7/20 need to comment
//                                    .setNegativeButton(getResources().getString(R.string.skip), new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            if (!prf.getString("first").equals("true")){
//                                                Intent intent = new Intent(SplashActivity.this,IntroActivity.class);
//                                                startActivity(intent);
//
//                                                finish();
//                                                prf.setString("first","true");
//                                            }else{
//                                                Intent intent;
//                                                if(prf.getString("LOGGED").toString().equals("TRUE")){
//                                                    intent = new Intent(SplashActivity.this, HomeActivity.class);
//                                                }else{
//                                                    intent = new Intent(SplashActivity.this, LoginNewActivity.class);
//                                                }                                                    startActivity(intent);
//
//                                                finish();
//                                            }
//                                        }
//                                    })
                                    // TODO: 9/7/20 comment end
                                    .setCancelable(false)
                                    .setIcon(R.drawable.ic_update)
                                    .show();
                        } else {
                            if (!prf.getString("first").equals("true")){
                                Intent intent = new Intent(SplashActivity.this,IntroActivity.class);
                                startActivity(intent);
                                finish();
                                prf.setString("first","true");
                            }else{
                                Intent intent;
                                if(prf.getString("LOGGED").toString().equals("TRUE")){
                                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                                }else{
                                    intent = new Intent(SplashActivity.this, LoginNewActivity.class);
                                }
                                startActivity(intent);
                               
                                finish();
                            }
                        }
                    }else {
                        if (!prf.getString("first").equals("true")){
                            Intent intent = new Intent(SplashActivity.this,IntroActivity.class);
                            startActivity(intent);
                           
                            finish();
                            prf.setString("first","true");
                        }else{
                            Intent intent;
                            if(prf.getString("LOGGED").toString().equals("TRUE")){
                                intent = new Intent(SplashActivity.this, HomeActivity.class);
                            }else{
                                intent = new Intent(SplashActivity.this, LoginNewActivity.class);
                            }
                            startActivity(intent);
                           
                            finish();
                        }
                    }
                }


                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    updateTextViews();
                    if (!prf.getString("first").equals("true")){
                        Intent intent = new Intent(SplashActivity.this,IntroActivity.class);
                        startActivity(intent);
                       
                        finish();
                        prf.setString("first","true");
                    }else{
                        Intent intent;
                        if(prf.getString("LOGGED").toString().equals("TRUE")){
                            intent = new Intent(SplashActivity.this, HomeActivity.class);
                        }else{
                            intent = new Intent(SplashActivity.this, LoginNewActivity.class);
                        }
                        startActivity(intent);
                       
                        finish();
                    }
                }
            });
        }else{
            if (!prf.getString("first").equals("true")){
                Intent intent = new Intent(SplashActivity.this,IntroActivity.class);
                startActivity(intent);
               
                finish();
                prf.setString("first","true");
            }else{
                Intent intent;
                if(prf.getString("LOGGED").toString().equals("TRUE")){
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                }else{
                    intent = new Intent(SplashActivity.this, LoginNewActivity.class);
                }
                startActivity(intent);
               
                finish();
            }

        }

    }

    private void setFilterById(long downloadID) {
    }

    private void initBuy() {
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        if(!BillingProcessor.isIabServiceAvailable(this)) {
            //  showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(this, Global.MERCHANT_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                updateTextViews();
            }
            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
            }
            @Override
            public void onBillingInitialized() {
                readyToPurchase = true;
                updateTextViews();
            }
            @Override
            public void onPurchaseHistoryRestored() {
                for(String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });
        bp.loadOwnedPurchasesFromGoogle();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConn);
    }

    private void updateTextViews() {
        PrefManager prf= new PrefManager(getApplicationContext());
        bp.loadOwnedPurchasesFromGoogle();
        if(isSubscribe(Global.SUBSCRIPTION_ID)){
            prf.setString("SUBSCRIBED","TRUE");
        }
        else{
            prf.setString("SUBSCRIBED","FALSE");
        }
    }


    public Boolean isSubscribe(String SUBSCRIPTION_ID_CHECK){

        if (!bp.isSubscribed(Global.SUBSCRIPTION_ID))
            return false;


        Bundle b =  getPurchases();
        if (b==null)
            return  false;
        if( b.getInt("RESPONSE_CODE") == 0){
            ArrayList<String> ownedSkus =
                    b.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            ArrayList<String>  purchaseDataList =
                    b.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            ArrayList<String>  signatureList =
                    b.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
            String continuationToken =
                    b.getString("INAPP_CONTINUATION_TOKEN");


            if(purchaseDataList == null){
                return  false;

            }
            if(purchaseDataList.size()==0){
                return  false;
            }
            for (int i = 0; i < purchaseDataList.size(); ++i) {
                String purchaseData = purchaseDataList.get(i);
                String signature = signatureList.get(i);
                String sku_1 = ownedSkus.get(i);
                //Long tsLong = System.currentTimeMillis()/1000;

                try {
                    JSONObject rowOne = new JSONObject(purchaseData);
                    String  productId =  rowOne.getString("productId") ;

                    if (productId.equals(SUBSCRIPTION_ID_CHECK)){

                        Boolean  autoRenewing =  rowOne.getBoolean("autoRenewing");
                        if (autoRenewing){
                            Long tsLong = System.currentTimeMillis()/1000;
                            Long  purchaseTime =  rowOne.getLong("purchaseTime")/1000;
                            return  true;
                        }else{
                            // Toast.makeText(this, "is not autoRenewing ", Toast.LENGTH_SHORT).show();
                            Long tsLong = System.currentTimeMillis()/1000;
                            Long  purchaseTime =  rowOne.getLong("purchaseTime")/1000;
                            if (tsLong > (purchaseTime + (Global.SUBSCRIPTION_DURATION*86400)) ){
                                //   Toast.makeText(this, "is Expired ", Toast.LENGTH_SHORT).show();
                                return  false;
                            }else{
                                return  true;
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }else{
            return false;
        }

        return  false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    public Bundle getPurchases(){
        if (!bp.isInitialized()) {
            return null;
        }
        try{

            return  mService.getPurchases(Constants.GOOGLE_API_VERSION, getApplicationContext().getPackageName(), Constants.PRODUCT_TYPE_SUBSCRIPTION, null);
        }catch (Exception e) {
            // Toast.makeText(this, "ex", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
        return null;
    }


}
