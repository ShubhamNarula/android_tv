package com.mystatus.nachos.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mystatus.nachos.Provider.PrefManager;
import com.mystatus.nachos.R;
import com.mystatus.nachos.api.apiClient;
import com.mystatus.nachos.api.apiRest;
import com.mystatus.nachos.entity.CheckIsSubscribe;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CancelSubscriptioActivity extends AppCompatActivity {

    private Button btnCancel;
    Context mContext= this;
    String myURL = "";
    private ProgressDialog register_progress;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_cancel);

        //Get webview
        btnCancel =  findViewById(R.id.btnCancel);
              PrefManager prf = new PrefManager(getApplicationContext());
        String   key_user=  prf.getString("USERN_USER");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacnelSubscribe();
            }
        });

        myURL = "http://payments.adminpanel.esy.es/Pay/cancel_subs/"+key_user;

       // startWebView(myURL);

    }


    public void cacnelSubscribe() {
        System.out.println("SHUBHAM--"+myURL);
        register_progress = ProgressDialog.show(this, null, getResources().getString(R.string.cancel_subscription), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        PrefManager prf = new PrefManager(getApplicationContext());
        String   email=  prf.getString("USERN_USER");
        Call<CheckIsSubscribe> call = service.cancelSubscription(myURL);
        call.enqueue(new Callback<CheckIsSubscribe>() {
            @Override
            public void onResponse(Call<CheckIsSubscribe> call, Response<CheckIsSubscribe> response) {
                if (response.body() != null) {
                    System.out.println("SHUBHAM--"+response.body().getStatus());
                    PrefManager prefManager= new PrefManager(getApplicationContext());
                    prefManager.setString("SUBSCRIBED","FALSE");
                    Toasty.error(mContext,"Your subscription has been cancelled").show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CheckIsSubscribe> call, Throwable t) {
            }
        });
    }


}
