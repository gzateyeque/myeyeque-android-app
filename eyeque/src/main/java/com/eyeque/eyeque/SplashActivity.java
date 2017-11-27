package com.eyeque.eyeque;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * File:            SplashActivity.java
 * Description:     A splash screen to display logo and perform device compatibility check
 * Created:         2016/03/19
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class SplashActivity extends Activity {
    private long ms = 0;
    private long splashTime = 1000;
    private boolean splashActive = true;
    private boolean paused = false;
    private static SQLiteDatabase myDb = null;
    private static String dbToken;
    private boolean isOnBoardNeeded = true;
    private static boolean checkDeviceCompatibility = false;
    private static boolean networkConnStatus = false;
    private static boolean debug = true;


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
        setContentView(R.layout.activity_splash);

        SingletonDataHolder.phoneManufacturer = android.os.Build.MANUFACTURER;
        SingletonDataHolder.phoneBrand = android.os.Build.BRAND;
        SingletonDataHolder.phoneProduct = android.os.Build.PRODUCT;
        SingletonDataHolder.phoneModel = android.os.Build.MODEL;
        SingletonDataHolder.phoneDevice = android.os.Build.DEVICE;
        SingletonDataHolder.phoneHardware = android.os.Build.HARDWARE;
        SingletonDataHolder.phoneSerialNum = android.os.Build.SERIAL;
        SingletonDataHolder.phoneDisplay = android.os.Build.DISPLAY;

        Log.d("**** Phone Type ****", SingletonDataHolder.phoneBrand + " " + SingletonDataHolder.phoneModel);

        // Simulate user's phone brand and model
        // SingletonDataHolder.phoneBrand = "Samsung";
        // SingletonDataHolder.phoneModel = "SM-G930T";

        if (debug) {
            NetConnection conn = new NetConnection();
            if (conn.isConnected(getApplicationContext())) {
                Log.i("TAG", "Call JOSN Device");
                CheckPhoneCompatibility();
            }
        }

        if (SingletonDataHolder.token != "") {
             ValidateToken();
        }

        // Check local persistent eyeque.db database
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            myDb = dbHelper.getReadableDatabase();
            Log.d("TAG", "open database successfully");

            String[] projection = {
                    Constants.USER_ENTITY_ID_COLUMN,
                    Constants.USER_ENTITY_TOKEN_COLUMN
            };
            // How you want the results sorted in the resulting Cursor
            String sortOrder =
                    Constants.USER_ENTITY_ID_COLUMN + " DESC";

            Cursor cursor = myDb.query(
                    Constants.USER_ENTITY_TABLE,    // The table to query
                    projection,                     // The columns to return
                    null,                           // The columns for the WHERE clause
                    null,                           // The values for the WHERE clause
                    null,                           // don't group the rows
                    null,                           // don't filter by row groups
                    sortOrder                       // The sort order
            );
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                dbToken = cursor.getString(
                        cursor.getColumnIndexOrThrow(Constants.USER_ENTITY_TOKEN_COLUMN));

                if (dbToken != null && dbToken != "") {
                    SingletonDataHolder.token = dbToken;
                    Log.d("### SP DB Token###", dbToken);
                    // Intent topIntent = new Intent(getBaseContext(), TopActivity.class);
                    // startActivity(topIntent);
                    // finish();
                    // return;
                }

            }
        } catch (IOException e) {
            Log.d("TAG", "open database failed");
        }

        final ImageView splashView = (ImageView) findViewById(R.id.splashImageView);
        splashView.setImageResource(R.drawable.eyeque_logo);

        Thread splashThread = new Thread() {
            public void run() {
                try {
                    while (splashActive && ms < splashTime * 10) {
                        if (!paused)
                            ms += 100;
                        NetConnection conn = new NetConnection();
                        if (conn.isConnected(getApplicationContext()) && !checkDeviceCompatibility) {
                            CheckPhoneCompatibility();
                        }
                        if (ms > 500 && checkDeviceCompatibility)
                            splashActive = false;
                        sleep(100);
                    }
                } catch (Exception e) {
                } finally {
                    if (checkDeviceCompatibility) {
                        if (dbToken != null && dbToken != "")
                            ValidateToken();
                        else {
                            Intent startIntent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(startIntent);
                            finish();
                        }
                        // Intent startIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        // startActivity(startIntent);
                        // finish();
                    } else {
                        Intent startIntent = new Intent(SplashActivity.this, DeviceCompatActivity.class);
                        startIntent.putExtra("NetConnStatus", networkConnStatus);
                        startActivity(startIntent);
                        finish();
                    }
                }
            }
        };
        splashThread.start();
    }

    private void CheckPhoneCompatibility() {

        networkConnStatus = true;
        checkDeviceCompatibility = false;
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
                        checkDeviceCompatibility = true;
                        SingletonDataHolder.phoneType = jsonObj.getString("phone_type");
                        SingletonDataHolder.phonePpi = jsonObj.getInt("phone_ppi");
                        SingletonDataHolder.deviceHeight = jsonObj.getDouble("height");
                        SingletonDataHolder.deviceWidth = jsonObj.getDouble("width");
                        SingletonDataHolder.centerX = jsonObj.getInt("center_x");
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
                    checkDeviceCompatibility = false;
                    // Toast.makeText(SplashActivity.this, "Phone incompatible", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(SplashActivity.this, "Please connect to the Internet and try it again", Toast.LENGTH_SHORT).show();
    }

    private void ValidateToken() {

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
                    Log.i("*** JSON Rep ***", response);

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
                            Log.i("*** Birth Year ***", jsonResponse.getString("dob").substring(1, 5));
                            SingletonDataHolder.birthYear = Integer.valueOf(jsonResponse.getString("dob").substring(0, 4));
                        }
                        SingletonDataHolder.groupId = Integer.parseInt(jsonResponse.getString("group_id"));

                        if (jsonResponse.has("custom_attributes")) {
                            JSONArray jsonCustArrArray = jsonResponse.getJSONArray("custom_attributes");

                            for (int i = 0; i < jsonCustArrArray.length(); i++) {
                                JSONObject objectInArray = jsonCustArrArray.getJSONObject(i);
                                String attrName = objectInArray.getString("attribute_code");
                                String attrValue = objectInArray.getString("value");

                                if (attrName.matches("device_number")) {
                                    if (attrValue.matches("") || (attrValue.matches("null") || attrValue == null)) {
                                        Log.i("********4*********", "true");
                                        isOnBoardNeeded = true;
                                    } else {
                                        Log.i("********5*********", "false");
                                        SingletonDataHolder.deviceSerialNum = attrValue;
                                        isOnBoardNeeded = false;
                                    }
                                }
                                if (attrName.matches("country_preference")) {
                                    Log.i("***** COUNTRY *****", attrValue);
                                    SingletonDataHolder.country = attrValue;
                                }// Retrieve User's subscription status
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
                                Intent startIntent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(startIntent);
                            } else {
                                Intent topIntent = new Intent(getBaseContext(), TopActivity.class);
                                startActivity(topIntent);
                            }
                            finish();
                        } else {
                            Log.i("********4*********", "true");
                            // Intent nameIntent = new Intent(getBaseContext(), NameActivity.class);
                            // startActivity(nameIntent);
                            Intent startIntent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(startIntent);
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
                    Log.d("Error.Response", error.toString());
                    Intent startIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(startIntent);
                    finish();
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
        } else {
            Intent startIntent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(startIntent);
            finish();
        }
    }
}
