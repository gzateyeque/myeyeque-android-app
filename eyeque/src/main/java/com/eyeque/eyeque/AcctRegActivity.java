package com.eyeque.eyeque;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * File:            AcctRegActivity.java
 * Description:     User email registration page
 *
 * Created:         2017/10/17
 * Author:          George Zhao
 *
 * Copyright (c) 2017 EyeQue Corp
 */
public class AcctRegActivity extends AppCompatActivity {

    // Tag for log message
    private static final String TAG = AcctRegActivity.class.getSimpleName();
    private WebView webview;
    private View mLoginFormView;
    private View mProgressView;
    private AutoCompleteTextView mEmailView;
    EditText mPasswordView;
    EditText mFirstnameView;
    EditText mLastnameView;
    String gEmail;
    String gPassword;

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
        setContentView(R.layout.activity_acct_reg);

        mProgressView = findViewById(R.id.reset_password_progress);

        /*
        emailEt = (EditText) findViewById(R.id.reg_email);
        emailEt.setHint(Html.fromHtml("<small>" + getString(R.string.email_hint) + "</small>" ));

        mPasswordView = (EditText) findViewById(R.id.reg_password);
        mPasswordView.setHint(Html.fromHtml("<small>" + getString(R.string.password_hint) + "</small>" ));
        */

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.reg_email);

        mPasswordView = (EditText) findViewById(R.id.reg_password);
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

        mFirstnameView = (EditText) findViewById(R.id.reg_firstname);
        mLastnameView = (EditText) findViewById(R.id.reg_lastname);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Button regButton = (Button) findViewById(R.id.email_reg_button);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                attemptLogin();
            }
        });
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
        String firstname = mFirstnameView.getText().toString();
        String lastname = mLastnameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_empty_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
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

        // Check for a non-empty firstname and lastname
        if (TextUtils.isEmpty(firstname)) {
            mFirstnameView.setError(getString(R.string.error_invalid_firstname));
            focusView = mFirstnameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(lastname)) {
            mLastnameView.setError(getString(R.string.error_invalid_lastname));
            focusView = mLastnameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            showProgress(false);
            focusView.requestFocus();
        } else {
            SignUp(email, password, firstname, lastname);

            // showProgress(true);
            // mAuthTask = new UserLoginTask(email, password);
            // mAuthTask.execute((Void) null);
        }
    }
    /***
     *        Register the user via Restful API
     * @param email
     * @param password
     * @param type
     */
    private void SignUp(String email, String password, String firstname, String lastname) {

        gEmail = email;
        gPassword = password;
        String url = Constants.UrlSignUp;
        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            final JSONObject params = new JSONObject();
            final JSONObject tmpParams = new JSONObject();
            try {
                tmpParams.put("email", email);
                tmpParams.put("firstname", firstname);
                tmpParams.put("lastname", lastname);
                params.put("customer", tmpParams);
                params.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("*** UserSignUp ***", response);
                    // Pass authentication
                    showProgress(false);
                    SingletonDataHolder.newRegUser = true;
                    SingletonDataHolder.email = gEmail;
                    Intent regCompIntent = new Intent(getBaseContext(), RegCompActivity.class);
                    startActivity(regCompIntent);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    Log.d("Error.Response", error.toString());
                    Toast.makeText(AcctRegActivity.this, "Registration failed, plese try it again", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(AcctRegActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    /**
     * Define the email or password format validation rules
     * @param email
     * @return
     */
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
        }
    }
}
