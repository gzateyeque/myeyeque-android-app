package com.eyeque.eyeque;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.eyeque.eyeque.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * File:            SerialActivity.java
 * Description:     The page let user to enter their devie serial number to proceed the
 *                  account onboarding
 *                  process
 * Created:         2016/07/12
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class SerialActivity extends AppCompatActivity {
    private WebView webview;
    private EditText serialEt;
    private String serialNum;
    private int nextCount = 0;

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
        setContentView(R.layout.activity_serial);

        serialEt = (EditText) findViewById(R.id.serialEditText);
        serialEt.setHint(Html.fromHtml("<small>" + "Serial number" + "</small>" ));

        TextView orderTv = (TextView) findViewById(R.id.deviceOrderTextView);
        orderTv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Uri uri =  Uri.parse(Constants.UrlBuyDevice);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            }
        });

        Button nextButton = (Button) findViewById(R.id.serialNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serialNum = serialEt.getText().toString();
                if (serialNum.matches(""))
                    Toast.makeText(SerialActivity.this, "Please enter device serial number", Toast.LENGTH_SHORT).show();
                else {
                    CheckSeriallNum(serialNum);
                }
                // Intent agreementIntent = new Intent(getBaseContext(), AgreementActivity.class);
                // startActivity(agreementIntent);
            }
        });

        // Fresh login
        SingletonDataHolder.freshLogin = true;

        GetBuySubscriptionData();
        GetUserSubscription();
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
                    int status = 0;
                    // Parse serial check response
                    try {
                        // Parsing json object response
                        // response will be a json object
                        // JsonParser parser = new JsonParser();
                        Log.i("**** SERSER ****", string);
                        JSONObject jsonObj = new JSONObject(string);
                        int ret_code = jsonObj.getInt("return_code");
                        if (ret_code != 0)
                            status = jsonObj.getInt("status");
                        Log.i("*** SNNN 1 ***", Integer.toString(ret_code));
                        Log.i("*** SNNN 2 ***", Integer.toString(status));
                        SingletonDataHolder.deviceSerialNum = serialNum;
                        if (ret_code == 1) {
                            if (status == 0) {
                                Intent genderIntent = new Intent(getBaseContext(), GenderActivity.class);
                                startActivity(genderIntent);
                                /***
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SerialActivity.this);
                                // Setting Dialog Message
                                alertDialog.setTitle("Membership");
                                alertDialog.setMessage("Congratulation, this device comes with a one year free All Access Membership. Please proceed the onboarding process to register an account. As an EyeQue subscriber you will be able to take on line eye tests, receive eyeglass numbers,receive discounts and promotions, and other benefits(see www.eyeque.com/subscription for details).");

                                // Setting  "Ok" Button
                                alertDialog.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                SingletonDataHolder.deviceSerialNum = serialNum;
                                                Intent genderIntent = new Intent(getBaseContext(), GenderActivity.class);
                                                startActivity(genderIntent);
                                            }
                                        });
                                // Showing Alert Dialog
                                alertDialog.show();
                                 ***/
                            } else if (SingletonDataHolder.subscriptionStatus > 1 && nextCount == 0) {
                                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(SerialActivity.this);
                                // Setting Dialog Message
                                alertDialog2.setTitle("All Access Membership required");
                                alertDialog2.setMessage("In order to access the full features of the EyeQue PVT app "
                                         + "you will need to become an All Access Member at $" + SingletonDataHolder.subscriptionAnnualPrice
                                         + ". At the end of this process you will be prompted to do so.");

                                // Setting Positive "Buy" Button
                                alertDialog2.setPositiveButton("Buy All Access Membership",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Uri uri = Uri.parse(SingletonDataHolder.subscriptionBuyLink
                                                        + "&token=" + SingletonDataHolder.token);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                                nextCount++;
                                            }
                                        });
                                // Setting Negative "Cancel" Button
                                alertDialog2.setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                nextCount++;
                                            }
                                        });
                                // Showing Alert Dialog
                                alertDialog2.show();
                            } else if (SingletonDataHolder.subscriptionStatus == 1 && nextCount == 0) {
                                Intent genderIntent = new Intent(getBaseContext(), GenderActivity.class);
                                startActivity(genderIntent);
                                /***
                                AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(SerialActivity.this);
                                // Setting Dialog Message
                                alertDialog3.setTitle("Subscription");
                                alertDialog3.setMessage("You currently have active subscription. You are good to go.");
                                // Setting Positive "Buy" Button
                                // Setting  "Ok" Button
                                alertDialog3.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                SingletonDataHolder.deviceSerialNum = serialNum;
                                                Intent genderIntent = new Intent(getBaseContext(), GenderActivity.class);
                                                startActivity(genderIntent);
                                            }
                                        });
                                // Showing Alert Dialog
                                alertDialog3.show();
                                 ***/
                            } else {
                                Intent genderIntent = new Intent(getBaseContext(), GenderActivity.class);
                                startActivity(genderIntent);
                            }
                        } else
                                Toast.makeText(SerialActivity.this, "Invalid Serial Number", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SerialActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.toString());
                    Toast.makeText(SerialActivity.this, "Validation failed", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(SerialActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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
                        // Toast.makeText(getActivity(), "Subscription Parse Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.toString());
                    SingletonDataHolder.deviceApiRespData = error.toString();
                    // Toast.makeText(getActivity(), "Subscription Parse Error", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SerialActivity.this, "Subscription Parse Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.toString());
                    SingletonDataHolder.deviceApiRespData = error.toString();
                    Toast.makeText(getApplicationContext(), "Subscription Parse Error", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        GetUserSubscription();
    }
}
