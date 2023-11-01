package com.mystatus.nachos.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mystatus.nachos.Provider.PrefManager;
import com.mystatus.nachos.R;

import java.util.Set;

import es.dmoral.toasty.Toasty;

public class PaymentWebview extends AppCompatActivity {

    private WebView webView;
    String myURL = "";
    Context mContext= this;
    ProgressBar pd_loading;
    public static final String USER_AGENT_FAKE = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
    Handler handler;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_payment);

        //Get webview
        pd_loading =  findViewById(R.id.pd_loading);
        webView = (WebView) findViewById(R.id.webView1);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //webView.getSettings().setUserAgentString(USER_AGENT_FAKE);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);

        handler = new Handler();
        int delay = 5000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                webView.evaluateJavascript("document.getElementById('status').value", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        value = value.replace("\"", "");
                        //Toast.makeText(mContext, "VALUE--"+value, Toast.LENGTH_SHORT).show();
                        if(value.equalsIgnoreCase("active")){
                            PrefManager prefManager= new PrefManager(getApplicationContext());
                            prefManager.setString("SUBSCRIBED","TRUE");
                            Toasty.success(getApplicationContext(), "Thank you for subscribing us!", Toast.LENGTH_SHORT, true).show();
                            if(handler != null){
                                handler.removeCallbacksAndMessages(null);
                            }
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result",value);
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                        }else if(value.equalsIgnoreCase("fail")){
                            PrefManager prefManager= new PrefManager(getApplicationContext());
                            prefManager.setString("SUBSCRIBED","FALSE");
                            Toasty.error(getApplicationContext(), "Subscription has been failed! Please try again later.", Toast.LENGTH_SHORT, true).show();
                            if(handler != null){
                                handler.removeCallbacksAndMessages(null);
                            }
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result",value);
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                        }
                    }
                });
                //do something
                handler.postDelayed(this, delay);
            }
        }, delay);


        PrefManager prf = new PrefManager(getApplicationContext());
        String   key_user=  prf.getString("USERN_USER");

        System.out.println("SHUBHAM--"+key_user);

        myURL = "http://payments.adminpanel.esy.es/Pay/index/"+key_user;

        startWebView(myURL);

    }

    private void startWebView(String url) {

        webView.setWebChromeClient(new WebChromeClient() {


            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog,
                                          boolean isUserGesture, Message resultMsg) {

                WebView newWebView = new WebView(PaymentWebview.this);
                newWebView.getSettings().setJavaScriptEnabled(true);
                newWebView.getSettings().setSupportZoom(true);
                newWebView.getSettings().setBuiltInZoomControls(true);
                newWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
                newWebView.getSettings().setSupportMultipleWindows(true);
                view.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        //Toast.makeText(PaymentWebview.this, ""+url, Toast.LENGTH_SHORT).show();
                        view.loadUrl(url);
                        return true;
                    }

                });

                newWebView.setFocusable(true);

                return true;
            }
            
    });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pd_loading.setVisibility(View.VISIBLE);
            }

            public void onPageFinished(WebView view, String url) {
                pd_loading.setVisibility(View.GONE);
            }
        });
        webView.loadUrl(url);

    }

    // Open previous opened link from history on webview when back button pressed

    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            if(handler != null){
                handler.removeCallbacksAndMessages(null);
            }
            // Let the system handle the back button
            super.onBackPressed();
        }
    }

}
