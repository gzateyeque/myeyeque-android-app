package com.eyeque.eyeque;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
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
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Locale;

/**
 *
 * File:            TopActivity.java
 * Description:     This container class provide a bottom tabbar layout structure
 *                  1: Dashboard  2: Test  3: Account
 *                  this into account for test result
 * Created:         2016/07/10
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class TopActivity extends AppCompatActivity
        implements TutorialFragment.OnFragmentInteractionListener,
                   Test2Fragment.OnFragmentInteractionListener,
                    DashboardFragment.OnFragmentInteractionListener,
                    SettingFragment.OnFragmentInteractionListener {

    private CoordinatorLayout coordinatorLayout;
    private static final String TAG = "Home";
    private static boolean checkDeviceCompatibility = false;
    static private Fragment dashboardFragment;
    static private Fragment tutorialFragment;
    static private Fragment deviceCompatFragment;
    static private Fragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_top);
        setContentView(R.layout.activity_top);
        SingletonDataHolder.lang = Locale.getDefault().getLanguage();

        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        // window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        // Get the phone Dpi information
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        SingletonDataHolder.phoneDpi = (int) metrics.xdpi;
        SingletonDataHolder.phoneDisplay = android.os.Build.DISPLAY;
        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SingletonDataHolder.phoneWidth = size.x;
        SingletonDataHolder.phoneHeight = size.y;

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.top_activity);
        // get fragment manager
        /*
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment dashboardFragment = new DashboardFragment();
        Fragment testFragment = new TestFragment();
        // ft.add(R.id.frame_container, dashboardFragment, "dashboard");
        ft.add(R.id.frame_container, testFragment, "test");
        ft.commit();
        */

        BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.three_buttons_menu, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(int itemId) {
                selectMenuItem(itemId);
                /*
                switch (itemId) {
                    case R.id.home_item:
                        // setContentView(R.layout.activity_top);
                        Snackbar.make(coordinatorLayout, "Home Item Selected", Snackbar.LENGTH_LONG).show();
                        ft.add(R.id.frame_container, dashboardFragment, "dashboard");
                        ft.addToBackStack(null);
                        ft.commit();
                        break;
                    case R.id.test_item:
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                        i.putExtra("subjectId", 21);
                        i.putExtra("deviceId", 3);
                        i.putExtra("serverId", 1);
                        startActivity(i);
                        break;
                    case R.id.account_item:
                        Snackbar.make(coordinatorLayout, "Account Item Selected", Snackbar.LENGTH_LONG).show();
                        // ft.remove(dashboardFragment);
                        ft.add(R.id.frame_container, testFragment, "test");
                        ft.addToBackStack(null);
                        ft.commit();
                        break;
                }
                */
            }
        });

        // Set the color for the active tab. Ignored on mobile when there are more than three tabs.
        // bottomBar.setActiveTabColor("#C2185B");
        bottomBar.setActiveTabColor("#046EEA");
        selectMenuItem(R.id.home_item);
        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {
            CheckPhoneCompatibility();
        }


        // ft.add(R.id.frame_container, new DashboardFragment(), "dashboard");
        // alternatively add it with a tag
        // trx.add(R.id.your_placehodler, new YourFragment(), "detail");
        // ft.commit();


        // Use the dark theme. Ignored on mobile when there are more than three tabs.
        //bottomBar.useDarkTheme(true);

        // Use custom text appearance in tab titles.
        //bottomBar.setTextAppearance(R.style.MyTextAppearance);

        // Use custom typeface that's located at the "/src/main/assets" directory. If using with
        // custom text appearance, set the text appearance first.
        //bottomBar.setTypeFace("MyFont.ttf");
    }

    /**
     * Select menu item.
     *
     * @param menuItem the menu item
     */
    public void selectMenuItem(int menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        FragmentManager fragmentManager = getFragmentManager();


        try {
            switch(menuItem) {
                case R.id.home_item:
                    GetUserSubscription();
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fragmentClass = DashboardFragment.class;
                    dashboardFragment = (Fragment) fragmentClass.newInstance();
                    break;
                case R.id.test_item:
                    GetUserSubscription();
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    if (checkDeviceCompatibility) {
                            // fragmentClass = AttachDeviceFragment.class;
                            fragmentClass = TutorialFragment.class;
                            deviceCompatFragment = (Fragment) fragmentClass.newInstance();
                    }
                    else {
                        fragmentClass = Test2Fragment.class;
                        tutorialFragment = (Fragment) fragmentClass.newInstance();
                    }
                    break;
                case R.id.account_item:
                    GetUserSubscription();
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fragmentClass = SettingFragment.class;
                    settingFragment = (Fragment) fragmentClass.newInstance();
                    if (tutorialFragment != null)
                        fragmentManager.beginTransaction().remove(tutorialFragment);
                    break;
                default:
                    fragmentClass = Dashboard1Fragment.class;
            }
            fragment = (Fragment) fragmentClass.newInstance();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /***
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentClass != TutorialFragment.class && fragmentClass != null)
            fragmentManager.beginTransaction().remove(fragment)
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
         ***/

    }

    @Override
    public void onTutorialFragmentInteraction(Uri uri) {}

    @Override
    public void onTest2FragmentInteraction(Uri uri) {}

    @Override
    public void onDashboardFragmentInteraction(Uri uri) {}

    @Override
    public void onSettingFragmentInteraction(Uri uri) {}

    private void CheckPhoneCompatibility() {

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
                // params.put("phoneModel", "LGLS992");
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
                        Log.i("*** JSON Device Rtn ***", string);
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

                        // Overwrite with dynamic calculated setting
                        SingletonDataHolder.centerX = (int) (SingletonDataHolder.phoneWidth / 2.0);
                        SingletonDataHolder.centerY = (int) ((SingletonDataHolder.phoneDpi / 570.0) * 500.0);
                        SingletonDataHolder.lineLength = (int) ((SingletonDataHolder.phoneDpi / 570.0) * 200.0);
                        SingletonDataHolder.lineWidth = (int) ((SingletonDataHolder.phoneDpi / 570.0) * 50.0);

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
                        Toast.makeText(TopActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.toString());
                    SingletonDataHolder.deviceApiRespData = error.toString();
                    checkDeviceCompatibility = false;
                    Toast.makeText(TopActivity.this, "Phone incompatible", Toast.LENGTH_SHORT).show();
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
        // else
            // Toast.makeText(TopActivity.this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TopActivity.this, "Subscription Parse Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.toString());
                    SingletonDataHolder.deviceApiRespData = error.toString();
                    // Toast.makeText(AgreementActivity.this, "Subscription Parse Error", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(TopActivity.this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        GetUserSubscription();
    }

    /****
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
    ****/
}
