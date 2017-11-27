package com.eyeque.eyeque;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * File:            SubscriptionActivity.java
 * Description:     This activity displays the user's subscription status. User can tap the Buy
 *                  button and go to the eStore to purhcase the subscription
 * Created:         2017/08/25
 * Author:          George Zhao
 *
 * Copyright (c) 2017 EyeQue Corp
 */

public class SubscriptionActivity extends AppCompatActivity {

    private static final String TAG = "Subscription";
    private EditText subscriptionStatus;
    private EditText expirationDate;

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
        setContentView(R.layout.activity_subscription);

        GetUserSubscription();
        GetBuySubscriptionData();


        // Set up the account form.
        subscriptionStatus = (EditText) findViewById(R.id.subscriptionStatus);
        expirationDate = (EditText) findViewById(R.id.subscriptionExpiration);
        if (SingletonDataHolder.subscriptionStatus) {
            subscriptionStatus.setText("Active");
            expirationDate.setText(SingletonDataHolder.subscriptionExpDate);
        }
        else {
            if ((SingletonDataHolder.subscriptionExpDate).matches("1900-01-01")) {
                subscriptionStatus.setText("Unsubscribed");
                expirationDate.setText("--");
            }
            else {
                subscriptionStatus.setText("Expired");
                expirationDate.setText(SingletonDataHolder.subscriptionExpDate);
            }
        }
        // subscriptionStatus.setEnabled(false);

        Button buyButton = (Button) findViewById(R.id.subscription_buy_button);
        if ((SingletonDataHolder.subscriptionExpDate).matches("") || SingletonDataHolder.subscriptionExpDate == null) {
            expirationDate.setVisibility(View.GONE);
            buyButton.setVisibility(View.GONE);
        } else {
            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri =  Uri.parse(SingletonDataHolder.subscriptionBuyLink
                            + "&token=" + SingletonDataHolder.token);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }
    }

    public void GetBuySubscriptionData() {

        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            final String url = Constants.UrlBuySubscription;
            final JSONObject params = new JSONObject();

            StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String string) {
                    // Parse response
                    try {
                        // String sphericalStep;
                        Log.i("*** GetBuySubs ***", string);
                        JSONObject jsonObj = new JSONObject(string);
                        SingletonDataHolder.subscriptionAnnualPrice = jsonObj.getString("price");
                        SingletonDataHolder.subscriptionBuyLink = jsonObj.getString("url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SubscriptionActivity.this, "Subscription Parse Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.toString());
                    SingletonDataHolder.deviceApiRespData = error.toString();
                    Toast.makeText(SubscriptionActivity.this, "Subscription Parse Error", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    Log.i("JSON data", params.toString());
                    return params.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String authString = "Bearer " + SingletonDataHolder.token;
                    headers.put("Content-Type", "application/json;charset=UTF-8");
                    headers.put("Authorization", authString);
                    Log.i("$$$---HEADER---$$$", headers.toString());
                    return headers;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(Constants.NETCONN_TIMEOUT_VALUE, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            getRequest.setRetryPolicy(policy);
            queue.add(getRequest);
        }
        else
            Toast.makeText(SubscriptionActivity.this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
    }

    public void GetUserSubscription() {

        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            final String url = Constants.UrlUserSubscription;
            final JSONObject params = new JSONObject();

            StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String string) {
                    // Parse response
                    try {
                        // String sphericalStep;
                        Log.i("*** GetBuySubs ***", string);
                        JSONObject jsonObj = new JSONObject(string);
                        int id = jsonObj.getInt("id");
                        String attrValue = jsonObj.getString("expiration_date");
                        if ((attrValue).matches("") || attrValue == null) {
                            if (id == 0) {
                                Log.i("*** SUBSCRIPTION ***", SingletonDataHolder.subscriptionExpDate);
                                SingletonDataHolder.subscriptionStatus = true;
                                SingletonDataHolder.subscriptionExpDate = "";
                            }
                            else {
                                Log.i("*** SUBSCRIPTION ***", SingletonDataHolder.subscriptionExpDate);
                                SingletonDataHolder.subscriptionStatus = false;
                                SingletonDataHolder.subscriptionExpDate = "";
                            }
                        } else {
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SubscriptionActivity.this, "Subscription Parse Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.toString());
                    SingletonDataHolder.deviceApiRespData = error.toString();
                    Toast.makeText(SubscriptionActivity.this, "Subscription Parse Error", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    Log.i("JSON data", params.toString());
                    return params.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String authString = "Bearer " + SingletonDataHolder.token;
                    headers.put("Content-Type", "application/json;charset=UTF-8");
                    headers.put("Authorization", authString);
                    Log.i("$$$---HEADER---$$$", headers.toString());
                    return headers;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(Constants.NETCONN_TIMEOUT_VALUE, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            getRequest.setRetryPolicy(policy);
            queue.add(getRequest);
        }
        else
            Toast.makeText(SubscriptionActivity.this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onResume() {
        super.onResume();
        GetUserSubscription();
        if (SingletonDataHolder.subscriptionStatus) {
            subscriptionStatus.setText("Active");
            expirationDate.setText(SingletonDataHolder.subscriptionExpDate);
        }
        else {
            if ((SingletonDataHolder.subscriptionExpDate).matches("1900-01-01")) {
                subscriptionStatus.setText("Unsubscribed");
                expirationDate.setText("--");
            }
            else {
                subscriptionStatus.setText("Expired");
                expirationDate.setText(SingletonDataHolder.subscriptionExpDate);
            }
        }
    }

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
