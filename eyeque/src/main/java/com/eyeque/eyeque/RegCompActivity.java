package com.eyeque.eyeque;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * File:            RegCompActivity.java
 * Description:     The page will display after user has registered an account
 *                  process
 * Created:         2016/07/12
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class RegCompActivity extends AppCompatActivity {
    private EditText serialEt;
    private String serialNum;

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
        setContentView(R.layout.activity_reg_complete);

        TextView resendConfEmailTv = (TextView) findViewById(R.id.resendConfirmEmailTextView);
        resendConfEmailTv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Uri uri =  Uri.parse(Constants.UrlResendEmailConfirmation + SingletonDataHolder.email);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            }
        });

        Button nextButton = (Button) findViewById(R.id.regCompNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(loginIntent);
            finish();
            }
        });
    }

    private void CheckSeriallNum(String serial) {
        serialNum = serial;
        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            final String url = Constants.UrlCheckSerialNum;
            final JSONObject params = new JSONObject();
            try {
                params.put("sn", serialNum);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // final String url = Constants.UrlCheckSerialNum + serial;
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String string) {
                    // Parse serial check response
                    try {
                        // Parsing json object response
                        // response will be a json object
                        // JsonParser parser = new JsonParser();
                        // String retVal = parser.parse(string);
                        JSONObject jsonObj = new JSONObject(string);
                        int ret_code = jsonObj.getInt("return_code");
                        if (ret_code == 1) {
                            SingletonDataHolder.deviceSerialNum = serialNum;
                            Intent nameIntent = new Intent(getBaseContext(), NameActivity.class);
                            startActivity(nameIntent);
                        } else
                            Toast.makeText(RegCompActivity.this, "Invalid Serial Number", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegCompActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.toString());
                    Toast.makeText(RegCompActivity.this, "Validation failed", Toast.LENGTH_SHORT).show();
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
                    headers.put("Content-Type", "application/json;charset=UTF-8");
                    return headers;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(Constants.NETCONN_TIMEOUT_VALUE, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            queue.add(postRequest);
        }
        else
            Toast.makeText(RegCompActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }
}
