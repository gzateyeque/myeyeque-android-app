package com.eyeque.eyeque;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * File:            DeviceCompatActivity.java
 * Description:     Database helper class
 * Created:         2016/05/02
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class DeviceCompatActivity extends AppCompatActivity {
    private WebView webView;
    private boolean netConnStatus;

    // Tag for log message
    private static final String TAG = DeviceCompatActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_device_compat);

        netConnStatus = getIntent().getBooleanExtra("NetConnStatus", true);
        if (!netConnStatus)
            Toast.makeText(DeviceCompatActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

        webView = (WebView) findViewById(R.id.deviceCompatWebView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        // webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("about:blank");
            }
        });
        NetConnection conn = new NetConnection();
        if (!conn.isConnected(getApplicationContext())) {
            webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }
        webView.loadUrl(Constants.UrlDeviceList);

        Button diagButton = (Button) findViewById(R.id.deviceCompatDiagButton);
        diagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), DiagnosticsActivity.class);
            startActivity(intent);
            }
        });


        Button retryButton = (Button) findViewById(R.id.deviceCompatRetryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetConnection conn = new NetConnection();
                if (conn.isConnected(getApplicationContext())) {
                    Toast.makeText(DeviceCompatActivity.this, "Loading device parameters...", Toast.LENGTH_SHORT).show();
                    CheckPhoneCompatibility();
                } else {
                    Toast.makeText(DeviceCompatActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button exitButton = (Button) findViewById(R.id.deviceCompatExitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }
    private void CheckPhoneCompatibility() {

        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            final String url = Constants.UrlPhoneConfig;
            final JSONObject params = new JSONObject();
            try {
                params.put("name", SingletonDataHolder.deviceName);
                params.put("phoneBrand", SingletonDataHolder.phoneBrand);
                params.put("phoneModel", SingletonDataHolder.phoneModel);
                params.put("phoneType", "");
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                params.put("widthPixel",  metrics.widthPixels);
                params.put("heightPixel",  metrics.heightPixels);
                params.put("xdpi", metrics.xdpi);
                params.put("ydpi", metrics.ydpi);
                params.put("scale", metrics.scaledDensity);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String string) {
                    // Parse serial check response
                    try {
                        // String sphericalStep;
                        Log.i("*** JSON Rep ***", string);
                        SingletonDataHolder.deviceApiRespData = string;
                        JSONObject jsonObj = new JSONObject(string);
                        SingletonDataHolder.phoneType = jsonObj.getString("phone_type");
                        SingletonDataHolder.phonePpi = jsonObj.getInt("phone_ppi");
                        SingletonDataHolder.deviceHeight = jsonObj.getDouble("height");
                        SingletonDataHolder.deviceWidth = jsonObj.getDouble("width");
                        // SingletonDataHolder.centerX = jsonObj.getInt("center_x");
                        SingletonDataHolder.centerY = jsonObj.getInt("center_y");
                        SingletonDataHolder.lineLength = jsonObj.getInt("line_length");
                        SingletonDataHolder.lineWidth = jsonObj.getInt("line_width");
                        SingletonDataHolder.initDistance = jsonObj.getInt("initial_distance");
                        SingletonDataHolder.minDistance = jsonObj.getInt("min_distance");
                        SingletonDataHolder.maxDistance = jsonObj.getInt("max_distance");
                        SingletonDataHolder.disOffset = jsonObj.getInt("dis_offset");
                        SingletonDataHolder.sphericalStep = new ArrayList<String>(Arrays.asList(jsonObj.getString("spherical_step").split(",")));
                        for (int i = 0; i < 4; i++)
                            Log.i("*** TOP STEP ***", SingletonDataHolder.sphericalStep.get(i));

                        // ***** Test *****
                        // if (Math.abs(SingletonDataHolder.phoneDpi - SingletonDataHolder.phonePpi) > 50) {
                        if ((SingletonDataHolder.phonePpi - SingletonDataHolder.phoneDpi) > 50) {
                            // Emulate the low resolution screen
                            SingletonDataHolder.correctDisplaySetting = false;
                            // double pixelRatio = (double) SingletonDataHolder.phoneDpi / (double) SingletonDataHolder.phonePpi;
                            // SingletonDataHolder.phonePpi = SingletonDataHolder.phoneDpi;
                            // SingletonDataHolder.centerX = (int) ((double) SingletonDataHolder.centerX * pixelRatio);
                            // SingletonDataHolder.centerY = (int) ((double) SingletonDataHolder.centerY * pixelRatio);
                            // SingletonDataHolder.initDistance = (int) ((double) SingletonDataHolder.initDistance * pixelRatio);
                            // SingletonDataHolder.maxDistance = (int) ((double) SingletonDataHolder.maxDistance * pixelRatio);
                            // SingletonDataHolder.lineLength = (int) ((double) SingletonDataHolder.lineLength * pixelRatio);
                            // SingletonDataHolder.deviceWidth = SingletonDataHolder.deviceWidth * pixelRatio;
                            // SingletonDataHolder.deviceHeight = SingletonDataHolder.deviceHeight * pixelRatio;
                        }

                        // Enter he pragram
                        Intent startIntent = new Intent(DeviceCompatActivity.this, LoginActivity.class);
                        startActivity(startIntent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Toast.makeText(SplashActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.toString());
                    SingletonDataHolder.deviceApiRespData = error.toString();
                    Toast.makeText(DeviceCompatActivity.this, "Phone incompatible", Toast.LENGTH_SHORT).show();
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
            postRequest.setRetryPolicy(policy);
            queue.add(postRequest);
        }
        else
            Toast.makeText(DeviceCompatActivity.this, "Please connect to the Internet and try it again", Toast.LENGTH_SHORT).show();
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

