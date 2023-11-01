package com.mystatus.nachos.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mystatus.nachos.Provider.PrefManager;
import com.mystatus.nachos.R;
import com.mystatus.nachos.api.apiClient;
import com.mystatus.nachos.api.apiRest;
import com.mystatus.nachos.entity.ApiResponse;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginNewActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginNewActivity";
    TextView tv_register;
    RelativeLayout relative_layout_google_login;
    private GoogleApiClient mGoogleApiClient;
    private Button btnLogIn;
    private ProgressDialog register_progress;
    private EditText mEtEmailID;
    private EditText mEtPassword;
    private String email,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relative_layout_google_login = findViewById(R.id.relative_layout_google_login);
        tv_register = findViewById(R.id.tv_register);
        btnLogIn = findViewById(R.id.btnLogIn);
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginNewActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
        relative_layout_google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        GoogleSignIn();
        mEtEmailID = findViewById(R.id.et_emailID);
        mEtPassword = findViewById(R.id.et_password);
    }

    private void validate() {
        email=mEtEmailID.getText().toString();
        password=mEtPassword.getText().toString();
        if(!isValidEmail(email)){
            Toasty.error(LoginNewActivity.this, "Please enter correct email address.", Toast.LENGTH_LONG).show();
        }else if(password.equalsIgnoreCase("")){
            Toasty.error(LoginNewActivity.this, "Please enter password.", Toast.LENGTH_LONG).show();
        }else{
            logIn( email,password,"email");
        }
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            getResultGoogle(result);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void GoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

    }

    private void getResultGoogle(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();
            String photo = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg";
            if (acct.getPhotoUrl() != null) {
                photo = acct.getPhotoUrl().toString();
            }

            System.out.println("SHUBHAM--"+acct.toString());
            System.out.println("SHUBHAM--"+acct.getEmail());

            signUp(acct.getEmail(), acct.getId(), acct.getDisplayName().toString(), "google", photo);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        } else {
            Log.d(TAG, "problem!!!");
        }
    }

    public void signUp(String username, String password, String name, String type, String image) {
        register_progress = ProgressDialog.show(this, null, getResources().getString(R.string.operation_progress), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.register(name, username, password, type, image);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 200) {
                        PrefManager prf = new PrefManager(getApplicationContext());
                        prf.setString("USERN_USER", username);

                        String id_user = "0";
                        String name_user = "x";
                        String username_user = "x";
                        String salt_user = "0";
                        String token_user = "0";
                        String type_user = "x";
                        String image_user = "x";
                        String enabled = "x";
                        for (int i = 0; i < response.body().getValues().size(); i++) {
                            if (response.body().getValues().get(i).getName().equals("salt")) {
                                salt_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("token")) {
                                token_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("id")) {
                                id_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("name")) {
                                name_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("type")) {
                                type_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("username")) {
                                username_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("url")) {
                                image_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("enabled")) {
                                enabled = response.body().getValues().get(i).getValue();
                            }
                        }
                        if (enabled.equals("true")) {
                            prf.setString("ID_USER", id_user);
                            prf.setString("SALT_USER", salt_user);
                            prf.setString("TOKEN_USER", token_user);
                            prf.setString("NAME_USER", name_user);
                            prf.setString("TYPE_USER", type_user);
                            prf.setString("IMAGE_USER", image_user);
                            prf.setString("LOGGED", "TRUE");
                            String token = FirebaseInstanceId.getInstance().getToken();
                            if (name_user.equals("null")) {
                                //  linear_layout_otp_confirm_login_activity.setVisibility(View.GONE);
                                //   linear_layout_name_input_login_activity.setVisibility(View.VISIBLE);
                            } else {
                                updateToken(Integer.parseInt(id_user), token_user, token, name_user);

                            }


                        } else {
                            Toasty.error(getApplicationContext(), getResources().getString(R.string.account_disabled), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                    if (response.body().getCode() == 500) {
                        Toasty.error(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                    }
                    if(response.body().getCode() == 600){
                        Toasty.error(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                }
                register_progress.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                register_progress.dismiss();
            }
        });
    }
    public void logIn(String email, String password,  String type) {
        register_progress = ProgressDialog.show(this, null, getResources().getString(R.string.operation_progress), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.login(email, password, type);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 200) {
                        if(response.body().getValues().size()==0){
                            Toasty.error(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                        }else{
                            String id_user = "0";
                            String name_user = "x";
                            String username_user = "x";
                            String salt_user = "0";
                            String token_user = "0";
                            String type_user = "x";
                            String image_user = "x";
                            String enabled = "true";
                            for (int i = 0; i < response.body().getValues().size(); i++) {
                                if (response.body().getValues().get(i).getName().equals("salt")) {
                                    salt_user = response.body().getValues().get(i).getValue();
                                }
                                if (response.body().getValues().get(i).getName().equals("token")) {
                                    token_user = response.body().getValues().get(i).getValue();
                                }
                                if (response.body().getValues().get(i).getName().equals("id")) {
                                    id_user = response.body().getValues().get(i).getValue();
                                }
                                if (response.body().getValues().get(i).getName().equals("name")) {
                                    name_user = response.body().getValues().get(i).getValue();
                                }
                                if (response.body().getValues().get(i).getName().equals("type")) {
                                    type_user = response.body().getValues().get(i).getValue();
                                }
                                if (response.body().getValues().get(i).getName().equals("username")) {
                                    username_user = response.body().getValues().get(i).getValue();
                                }
                                if (response.body().getValues().get(i).getName().equals("url")) {
                                    image_user = response.body().getValues().get(i).getValue();
                                }
                                if (response.body().getValues().get(i).getName().equals("enabled")) {
                                    enabled = response.body().getValues().get(i).getValue();
                                }
                            }
                            if (enabled.equals("true")) {
                                PrefManager prf = new PrefManager(getApplicationContext());
                                prf.setString("ID_USER", id_user);
                                prf.setString("SALT_USER", salt_user);
                                prf.setString("TOKEN_USER", token_user);
                                prf.setString("NAME_USER", name_user);
                                prf.setString("TYPE_USER", type_user);
                                prf.setString("USERN_USER", email);
                                prf.setString("IMAGE_USER", image_user);
                                prf.setString("LOGGED", "TRUE");
                                String token = FirebaseInstanceId.getInstance().getToken();
                                if (name_user.equals("null")) {
                                    //  linear_layout_otp_confirm_login_activity.setVisibility(View.GONE);
                                    //   linear_layout_name_input_login_activity.setVisibility(View.VISIBLE);
                                } else {
                                    prf = new PrefManager(getApplicationContext());
                                    prf.setString("NAME_USER", name_user);
                                    Toasty.success(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                                    register_progress.dismiss();
                                    Intent intent = new Intent(LoginNewActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }


                            } else {
                                Toasty.error(getApplicationContext(), getResources().getString(R.string.account_disabled), Toast.LENGTH_SHORT, true).show();
                            }

                        }



                    }
                    if (response.body().getCode() == 500) {
                        Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                    }
                    if(response.body().getCode() == 600){
                        Toasty.error(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                }
                register_progress.dismiss();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                register_progress.dismiss();
            }
        });
    }

    public void updateToken(Integer id, String key, String token, String name) {
        register_progress = ProgressDialog.show(this, null, getResources().getString(R.string.operation_progress), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.editToken(id, key, token, name);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    PrefManager prf = new PrefManager(getApplicationContext());
                    prf.setString("NAME_USER", name);
                    Toasty.success(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                    register_progress.dismiss();
                    Intent intent = new Intent(LoginNewActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                register_progress.dismiss();
            }
        });
    }


}

