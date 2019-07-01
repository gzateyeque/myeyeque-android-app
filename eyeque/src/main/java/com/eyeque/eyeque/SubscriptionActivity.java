package com.eyeque.eyeque;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
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
    private EditText subscriptionType;
    private EditText subscriptionStatus;
    private EditText expirationDate;
    private TextView learnMoreMembershipTv;
    private Button buyButton;
    private boolean showBuyButton = true;

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

        buyButton = (Button) findViewById(R.id.subscription_buy_button);

        // Set up the account form.
        subscriptionType = (EditText) findViewById(R.id.subscriptionType);
        subscriptionStatus = (EditText) findViewById(R.id.subscriptionStatus);
        expirationDate = (EditText) findViewById(R.id.subscriptionExpiration);
        learnMoreMembershipTv = (TextView) findViewById(R.id.learnMembershipTextView);

        switch (SingletonDataHolder.subscriptionStatus) {
            case 0:
                subscriptionType.setText("All Access Membership");
                subscriptionStatus.setText("Active");
                expirationDate.setVisibility(View.GONE);
                buyButton.setVisibility(View.GONE);
                break;
            case 1:
                subscriptionType.setText("All Access Membership");
                subscriptionStatus.setText("Active");
                expirationDate.setText(SingletonDataHolder.subscriptionExpDate);
                try {
                    Log.i("**** DIFF ****", SingletonDataHolder.subscriptionExpDate);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date expirationDate = format.parse(SingletonDataHolder.subscriptionExpDate);
                    Date now = new Date();
                    Log.i("**** DIFF ****", DateFormat.getDateInstance().format(now));
                    Log.i("**** DIFF ****", DateFormat.getDateInstance().format(expirationDate));
                    long diff = expirationDate.getTime() - now.getTime();
                    Log.i("**** DIFF ****", Long.toString(diff));
                    // if ((diff/ (24*60*60*1000)) <= 30) {
                        buyButton.setVisibility(View.VISIBLE);
                        buyButton.setText("Renew");
                    // }
                    // else {
                        // buyButton.setVisibility(View.INVISIBLE);
                        // showBuyButton = false;
                    // }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SubscriptionActivity.this, "Date Parse Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                subscriptionType.setText("Basic");
                subscriptionStatus.setVisibility(View.GONE);
                subscriptionStatus.setText("Expired");
                buyButton.setText("Upgrade to All Access Membership");
                expirationDate.setText(SingletonDataHolder.subscriptionExpDate);
                expirationDate.setVisibility(View.GONE);
                break;
            case 3:
                subscriptionType.setText("Basic");
                subscriptionStatus.setVisibility(View.GONE);
                subscriptionStatus.setText("Unsubscribed");
                buyButton.setText("Upgrade to All Access Membership");
                expirationDate.setText(SingletonDataHolder.subscriptionExpDate);
                expirationDate.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        learnMoreMembershipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Constants.UrlMembershipInfo);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        if (showBuyButton) {
            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SubscriptionActivity.this);
                    // Setting Dialog Message
                    alertDialog.setTitle("Information");
                    alertDialog.setMessage("You are going to leave the EyeQue PVT app where you will "
                            + "be taken to the EyeQue store. Then you can renew or upgrade to All "
                            + "Access Membership. After the renew or upgrade, you can come back the app to start using "
                            + "the full functionality of the EyeQue PVT app.");
                    // Setting Positive "Buy" Button
                    alertDialog.setPositiveButton("Continue",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri uri =  Uri.parse(SingletonDataHolder.subscriptionBuyLink
                                            + "&token=" + SingletonDataHolder.token);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    // Showing Alert Dialog
                    alertDialog.show();
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
                        SingletonDataHolder.subscriptionStatus = jsonObj.getInt("status");
                        String attrValue = jsonObj.getString("expiration_date");
                        String str = attrValue;
                        String[] strgs = str.split(" ");
                        SingletonDataHolder.subscriptionExpDate = strgs[0];
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
