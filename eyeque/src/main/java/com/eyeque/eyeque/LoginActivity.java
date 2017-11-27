package com.eyeque.eyeque;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eyeque.eyeque.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;

import static android.Manifest.permission.READ_CONTACTS;

/**
 *
 * File:            LoginActivity.java
 * Description:     Login screen that let user login or sign up using email and password.
 *                  User can also login via Google or Facebook account
 * Created:         2016/08/02
 * Author:          George Zhao
 *
 * Copyright (c) 2017 EyeQue Corp
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static String lang = Locale.getDefault().getLanguage();

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // private UserLoginTask mAuthTask = null;
    private boolean isSignInSucessful = false;
    private boolean isSignUpSucessful = false;
    private boolean isOnBoardNeeded = false;

    // UI references.
    private WebView webview;
    private static final String TAG = "Login";
    private ProgressDialog progressBar;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private static SQLiteDatabase myDb = null;
    private static int loginType = 1;
    private static String socialMediaType;
    private static String socialMediaId;
    private static String socialMediaEmail;
    private static String socialMediaTok;
    private static String gEmail;
    private static String gPassword;
    private static int signUpType = 1;   /* 1: Email 2: Social Media */
    private static AccessToken tok;
    private static int retVal = 0;
    private static boolean showVer = false;

    // Record last clcik time to check the qick double tapping
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        // window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        setContentView(R.layout.activity_login);

        TextView versionTV = (TextView) findViewById(R.id.versionTextView);
        versionTV.setEnabled(showVer);
        if (showVer)
            versionTV.setText(Constants.BuildNumber);
        // Check local persistent eyeque.db database
        try {
            String email, token;
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            myDb = dbHelper.getWritableDatabase();
            Log.d("TAG", "open database successfully");
        } catch (IOException e) {
            Log.d("TAG", "open database failed");
        }

        this.webview = (WebView) findViewById(R.id.bannerWebView);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        // webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.setVerticalScrollBarEnabled(false);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);

        //To disabled the horizontal and vertical scrolling:
        webview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // progressBar = ProgressDialog.show(LoginActivity.this, "Webview", "Loading...");

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "Loading...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                /**
                Log.i(TAG, "Finished loading URL: " + url);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
                 **/
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "Error: " + description);
                Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                view.loadUrl("about:blank");
            }
        });
        NetConnection conn = new NetConnection();
        if (!conn.isConnected(getApplicationContext())) {
            webview.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }
        webview.loadUrl(Constants.UrlBanner);
        // webview.loadUrl("http://192.168.110.151:8989/webview/banner/index.html");

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        if (!SingletonDataHolder.email.matches(""))
            mEmailView.setText(SingletonDataHolder.email);
        // populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button regButton = (Button) findViewById(R.id.email_reg_button);
        regButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                Intent regIntent = new Intent(getBaseContext(), AcctRegActivity.class);
                startActivity(regIntent);
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                loginType = 1;
                attemptLogin();
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });

        ImageButton facebookButton = (ImageButton) findViewById(R.id.FacebookButton);
        ImageButton googleButton = (ImageButton) findViewById(R.id.GoogleButton);

        facebookButton.setImageResource(R.drawable.facebook);
        facebookButton.setBackgroundDrawable(null);
        googleButton.setImageResource(R.drawable.google_plus);
        googleButton.setBackgroundDrawable(null);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Forgot password operation
        TextView forgotPswdTv = (TextView) findViewById(R.id.forgotPasswordTextView);
        forgotPswdTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPswdIntent = new Intent(getBaseContext(), ForgotPswdActivity.class);
                startActivity(forgotPswdIntent);
            }
        });

        //call Facebook onclick on your customized button on click by the following
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Facebook Login");
                        // AccessToken tok;
                        // tok = AccessToken.getCurrentAccessToken();
                        tok = loginResult.getAccessToken();
                        Log.d("*** User ID ***", tok.getUserId());
                        Log.d("*** TOKEN ***", tok.getToken());

                        socialMediaType = "facebook";
                        socialMediaId = tok.getUserId();
                        socialMediaTok = tok.getToken();

                        GraphRequest graphRequest = GraphRequest.newMeRequest(tok, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject user, GraphResponse graphResponse) {

                                if (graphResponse.getError() != null) {
                                    socialMediaEmail = "";
                                } else {
                                    socialMediaEmail = user.optString("email");
                                    Log.d("*** FB EMAIL ***", socialMediaEmail);
                                }
                                ValidateSocMediaAcct();

                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "email");
                        graphRequest.setParameters(parameters);
                        graphRequest.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        ImageButton facebookBtn = (ImageButton) findViewById(R.id.FacebookButton);
        facebookBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginType = 2;
                LoginManager.getInstance().logOut();
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
            }
        });

        // Google+ Login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken("974564109158-7iecgbo5d52rdm2kacvs4kc573p7r7dn.apps.googleusercontent.com").build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        ImageButton googleBtn = (ImageButton) findViewById(R.id.GoogleButton);
        googleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("*** G+ ***", "*** Tap Google+ Login ***");
                loginType = 2;
                if (mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.disconnect();
                    LoginManager.getInstance().logOut();
                    socialMediaId = "";
                    socialMediaTok = "";
                    socialMediaEmail = "";
                    socialMediaType = "";
                    // signOut();
                }
                mGoogleApiClient.connect();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, 200);
            }
        });
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        socialMediaId = "";
                        socialMediaTok = "";
                        socialMediaEmail = "";
                        socialMediaType = "";

                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            // Log.d("Success", "Google Login");
            // Log.i("Google email: ", acct.getEmail());
            // Log.i("Google ID: ", acct.getId());
            // Log.i("Google token: ", acct.getIdToken().toString());

            socialMediaId = acct.getId();
            socialMediaEmail = acct.getEmail();
            socialMediaTok = acct.getIdToken().toString();
            socialMediaType = "googleplus";

            ValidateSocMediaAcct();
        } else {
            mGoogleApiClient.disconnect();
            // Toast.makeText(getApplicationContext(),"Login failed!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // if (mAuthTask != null) {
        // return;
        // }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            SignIn(email, password);

            // showProgress(true);
            // mAuthTask = new UserLoginTask(email, password);
            // mAuthTask.execute((Void) null);
        }
    }

    // Call Rest API to check whether SignUp is needed
    private void ValidateSocMediaAcct() {

        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = Constants.UrlVerifySocMediaLogin;
            final JSONObject params = new JSONObject();
            try {
                params.put("type", socialMediaType);
                params.put("social_id", socialMediaId);
                params.put("social_token", socialMediaTok);
                params.put("email", socialMediaEmail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Intent emailRegIntent;
                    showProgress(false);
                    Log.i("**** Va Response ****", response);

                    switch (Integer.parseInt(response)) {
                        case 0:
                            // Ask for EyeQue registration
                            emailRegIntent = new Intent(getBaseContext(), SmEmailRegActivity.class);
                            emailRegIntent.putExtra("type", socialMediaType);
                            emailRegIntent.putExtra("id", socialMediaId);
                            emailRegIntent.putExtra("token", socialMediaTok);
                            emailRegIntent.putExtra("email", socialMediaEmail);
                            startActivity(emailRegIntent);
                            break;
                        case -2:
                            // Ask for EyeQue registration
                            emailRegIntent = new Intent(getBaseContext(), SmEmailRegActivity.class);
                            emailRegIntent.putExtra("type", socialMediaType);
                            emailRegIntent.putExtra("id", socialMediaId);
                            emailRegIntent.putExtra("token", socialMediaTok);
                            emailRegIntent.putExtra("email", socialMediaEmail);
                            startActivity(emailRegIntent);
                            break;
                        case -1:
                            SignUp(socialMediaEmail, "", 2);
                            break;
                        default:
                            // Acquire session token
                            String username = socialMediaType + socialMediaId;
                            SignIn(username, socialMediaTok);
                            break;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    Log.d("Error.Response", error.toString());
                    // Toast.makeText(LoginActivity.this, "Facebook Login Failed", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    Log.i("$$$---JSON---$$$", params.toString());
                    return params.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json;charset=UTF-8");
                    return headers;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(Constants.NETCONN_TIMEOUT_VALUE, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            queue.add(postRequest);
        } else
            Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    private void SignIn(String email, String password) {

        gEmail = email;
        gPassword = password;
        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = Constants.UrlSignIn;
            final JSONObject params = new JSONObject();
            try {
                params.put("username", email);
                params.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    showProgress(false);
                    Log.i("*** UserToken ***", response);
                    // AccessToken tok;
                    // tok = AccessToken.getCurrentAccessToken();
                    // Log.d(TAG, tok.getUserId());
                    // Pass authentication
                    response = response.replace("\"", "");
                    SingletonDataHolder.token = response;
                    if (loginType != 1)
                        SingletonDataHolder.email = socialMediaEmail;
                    else
                        SingletonDataHolder.email = gEmail;
                    // DbStoreToken();
                    CheckOnboard();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // showProgress(false);
                    Log.d("Error.Response", error.toString());

                    /***
                    if (loginType == 1) {
                        // Try SignUp
                        String email = mEmailView.getText().toString();
                        String password = mPasswordView.getText().toString();
                        SignUp(email, password, 1);
                    } else
                     ***/
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, "Wrong email or password or email is unconfirmed. Please try it again", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    Log.i("$$$---JSON2---$$$", params.toString());
                    return params.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json;charset=UTF-8");
                    return headers;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(Constants.NETCONN_TIMEOUT_VALUE, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            queue.add(postRequest);
        } else
            Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    private void SignUp(String email, String password, int type) {

        gEmail = email;
        gPassword = password;
        signUpType = type;
        String url = Constants.UrlSignUp;
        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            if (signUpType == 2)
                url = Constants.UrlSocMediaSignUp;

            final JSONObject params = new JSONObject();
            final JSONObject tmpParams = new JSONObject();
            try {
                tmpParams.put("email", email);
                tmpParams.put("firstname", "EyeQue");
                tmpParams.put("lastname", "User");
                params.put("customer", tmpParams);
                params.put("password", password);
                if (type == 2) {
                    params.put("redirectUrl", "");
                    params.put("type", socialMediaType);
                    params.put("social_id", socialMediaId);
                    params.put("social_token", socialMediaTok);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG, response);
                    // Pass authentication
                    showProgress(false);
                    SingletonDataHolder.newRegUser = true;
                    if (loginType != 1)
                        SingletonDataHolder.email = socialMediaEmail;
                    else
                        SingletonDataHolder.email = gEmail;

                    if (signUpType == 2) {
                        String username = socialMediaType + socialMediaId;
                        SignIn(username, socialMediaTok);
                    } else
                        SignIn(gEmail, gPassword);
                    // Intent nameIntent = new Intent(getBaseContext(), NameActivity.class);
                    // startActivity(nameIntent);
                    // finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    Log.d("Error.Response", error.toString());
                    if (signUpType == 2)
                        Toast.makeText(LoginActivity.this, "Login failed, please try it again", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(LoginActivity.this, "Wrong email or password or email is unconfirmed. Please try it again", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    Log.i("$$$---JSON1---$$$", params.toString());
                    return params.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json;charset=UTF-8");
                    return headers;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(Constants.NETCONN_TIMEOUT_VALUE, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            queue.add(postRequest);
        } else
            Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    private void CheckOnboard() {

        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = Constants.UrlUserProfile;
            final JSONObject params = new JSONObject();

            StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    showProgress(false);
                    Log.i("*** UserProfile ***", response);

                    //Parse the JOSN response
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        SingletonDataHolder.userId = Integer.parseInt(jsonResponse.getString("id"));
                        SingletonDataHolder.email = jsonResponse.getString("email");
                            SingletonDataHolder.firstName = jsonResponse.getString("firstname");
                            SingletonDataHolder.lastName = jsonResponse.getString("lastname");
                        if (jsonResponse.has("gender"))
                            SingletonDataHolder.gender = Integer.parseInt(jsonResponse.getString("gender"));
                        if (jsonResponse.has("dob")) {
                            Log.i("*** Birth Year ***", jsonResponse.getString("dob").substring(0, 4));
                            SingletonDataHolder.birthYear = Integer.valueOf(jsonResponse.getString("dob").substring(0, 4));
                        }
                        SingletonDataHolder.groupId = Integer.parseInt(jsonResponse.getString("group_id"));
                        Log.i("*** customer id ***", Integer.toString(SingletonDataHolder.userId));

                        if (jsonResponse.has("custom_attributes")) {
                            JSONArray jsonCustArrArray = jsonResponse.getJSONArray("custom_attributes");

                            isOnBoardNeeded = true;
                            // isOnBoardNeeded = true;
                            for (int i = 0; i < jsonCustArrArray.length(); i++) {
                                JSONObject objectInArray = jsonCustArrArray.getJSONObject(i);
                                String attrName = objectInArray.getString("attribute_code");
                                String attrValue = objectInArray.getString("value");
                                Log.i("********1*********", Integer.toString(i));
                                Log.i("********2*********", attrName);
                                Log.i("********3*********", attrValue);

                                if (attrName.matches("device_number")) {
                                    if (attrValue.matches("") || (attrValue.matches("null") || attrValue == null)) {
                                        Log.i("*****Device4*****", attrValue);
                                        isOnBoardNeeded = true;
                                        // Intent nameIntent = new Intent(getBaseContext(), NameActivity.class);
                                        // startActivity(nameIntent);
                                    } else {
                                        Log.i("****Device5******", attrValue);
                                        isOnBoardNeeded = false;
                                        DbStoreToken();
                                        SingletonDataHolder.deviceSerialNum = attrValue;
                                        // Intent topIntent = new Intent(getBaseContext(), TopActivity.class);
                                        // startActivity(topIntent);
                                    }
                                }
                                if (attrName.matches("country_preference")) {
                                    Log.i("***** COUNTRY *****", attrValue);
                                    SingletonDataHolder.country = attrValue;
                                }
                                // Retrieve User's subscription status
                                if (attrName.matches("subscription_expiration")) {
                                    Log.i("*** SUBSCRIPTION ***", attrValue);
                                    String str = attrValue;
                                    String[] strgs = str.split(" ");
                                    SingletonDataHolder.subscriptionExpDate = strgs[0];
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                                    Date now = new Date();
                                    try {
                                        Date expirationDate = format.parse(attrValue);
                                        if (expirationDate.before(now))
                                            SingletonDataHolder.subscriptionStatus = false;
                                        else
                                            SingletonDataHolder.subscriptionStatus = true;
                                    } catch (Exception e) {
                                            e.printStackTrace();
                                    }
                                }
                                if (attrName.matches("wear_glasses_or_contacts")) {
                                    Log.i("***** WEAR EYEGLASS *****", attrValue);
                                    if (attrValue.compareToIgnoreCase("yes") == 0)
                                        SingletonDataHolder.profileWearEyeglass = true;
                                    else
                                        SingletonDataHolder.profileWearEyeglass = false;
                                }
                                if (attrName.matches("wear_reading_glasses")) {
                                    Log.i("***** READING EYEGLASS *****", attrValue);
                                    if (attrValue.compareToIgnoreCase("yes") == 0)
                                        SingletonDataHolder.profileWearEyeglass = true;
                                    else
                                        SingletonDataHolder.profileWearEyeglass = false;
                                }
                                if (attrName.matches("user_nvadd_input")) {
                                    Log.i("***** NVADD INPUT *****", attrValue);
                                    SingletonDataHolder.profileReadingGlassesValue = attrValue;
                                }
                            }
                            if ((SingletonDataHolder.subscriptionExpDate).matches("") || SingletonDataHolder.subscriptionExpDate == null) {
                                Log.i("*** SUBSCRIPTION ***", SingletonDataHolder.subscriptionExpDate);
                                SingletonDataHolder.subscriptionStatus = true;
                                SingletonDataHolder.subscriptionExpDate = "";
                            }

                            if (isOnBoardNeeded) {
                                Intent nameIntent = new Intent(getBaseContext(), SerialActivity.class);
                                startActivity(nameIntent);
                                finish();
                            } else {
                                Intent topIntent = new Intent(getBaseContext(), TopActivity.class);
                                startActivity(topIntent);
                                finish();
                            }

                            // When there is no Device Number field

                        } else {
                            Log.i("********4*********", "true");
                            isOnBoardNeeded = true;
                            Intent nameIntent = new Intent(getBaseContext(), SerialActivity.class);
                            startActivity(nameIntent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // AccessToken tok;
                    // tok = AccessToken.getCurrentAccessToken();
                    // Log.d(TAG, tok.getUserId());
                    // Pass authentication

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    Log.d("Error.Response", error.toString());
                    /***
                    // Try SignUp
                    String email = mEmailView.getText().toString();
                    String password = mPasswordView.getText().toString();
                    SignUp(email, password, 1);
                     ***/
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, "Wrong email or password or email is unconfirmed. Please try it again", Toast.LENGTH_SHORT).show();
                }
            }) {

                /***
                @Override
                public byte[] getBody() throws AuthFailureError {
                    Log.i("$$$---JSON---$$$", params.toString());
                    return params.toString().getBytes();
                }
                 ***/

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String authStr = "Bearer " + SingletonDataHolder.token;
                    headers.put("Content-Type", "application/json;charset=UTF-8");
                    headers.put("Authorization", authStr);
                    return headers;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(Constants.NETCONN_TIMEOUT_VALUE, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            queue.add(postRequest);
        } else
            Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    private void DbStoreToken() {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Constants.USER_ENTITY_VERSION_COLUMN, 1);
        values.put(Constants.USER_ENTITY_TOKEN_COLUMN, SingletonDataHolder.token);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = myDb.insert(
                Constants.USER_ENTITY_TABLE,
                null,
                values);
        Log.d("**** Toekn DB ***", Long.toString(newRowId));
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            /****
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            ***/

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            // mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        /**
         * The Projection.
         */
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        /**
         * The constant ADDRESS.
         */
        int ADDRESS = 0;
        /**
         * The constant IS_PRIMARY.
         */
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    /***
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            Intent nameIntent = new Intent(getBaseContext(), NameActivity.class);
            startActivity(nameIntent);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
        ***/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }
}

