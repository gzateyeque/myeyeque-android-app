package com.eyeque.eyeque;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
/**
 *
 * File:            ForgotPswdActivity.java
 * Description:     This screen let user entering the email address to recieve a password
 *                  reset email
 * Created:         2016/05/17
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class ForgotPswdActivity extends AppCompatActivity {

    // Tag for log message
    private static final String TAG = ForgotPswdActivity.class.getSimpleName();
    private AutoCompleteTextView mEmailView;
    private WebView webview;
    private View mProgressView;
    EditText emailEt;

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
        setContentView(R.layout.activity_forgot_pswd);

        mProgressView = findViewById(R.id.reset_password_progress);

        emailEt = (EditText) findViewById(R.id.forgot_password_email);
        emailEt.setHint(Html.fromHtml("<small>" + getString(R.string.email_hint) + "</small>" ));

        Button forgotPswdButton = (Button) findViewById(R.id.forgotPasswordButton);
        forgotPswdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString();
                NetConnection conn = new NetConnection();
                if (conn.isConnected(getApplicationContext())) {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    // showProgress(true);

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String url = Constants.UrlForgotPassword;
                    final JSONObject params = new JSONObject();
                    try {
                        params.put("email", email);
                        params.put("template", "email_reset");
                        params.put("website_id", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    StringRequest postRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i(TAG, response);
                            showProgress(false);
                            // Pass authentication
                            finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", error.toString());
                            showProgress(false);
                            Toast.makeText(ForgotPswdActivity.this, "Reset password failed. Please try it again", Toast.LENGTH_SHORT).show();
                            // Try SignUp
                        }
                    }) {
                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            Log.i("$$$---PW JSON---$$$", params.toString());
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
                    showProgress(true);
                    queue.add(postRequest);
                } else
                    Toast.makeText(ForgotPswdActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
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
