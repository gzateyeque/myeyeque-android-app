package com.eyeque.eyeque;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * File:            PlayModeResultActivity.java
 * Description:     The screen gives the result of quick test
 * Created:         2016/10/05
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class ResultActivity extends Activity {

    private static int subjectId;
    private static int deviceId;
    private static int serverId;
    private static int testId;
    private static int score;
    private static int confirmCode = 2;
    private static String odSph;
    private static String odCyl;
    private static double odAxis;
    private static String odSe;
    private static double odRmse;
    private static String osSph;
    private static String osCyl;
    private static double osAxis;
    private static String osSe;
    private static double osRmse;

    private static double[] angleList = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static double[] leftPowerList = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private static double[] rightPowerList = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private static int[] rightDistList = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static int[] leftDistList = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static String mStr;
    private static boolean netLinkStatus = true;
    /**
     * The Is upload complete.
     */
    boolean isUploadComplete = false;
    private static int numMeasurement;
    private PatternView patternView;

    // Tag for log message
    private static final String TAG = ResultActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        setContentView(R.layout.activity_result);

        subjectId = getIntent().getIntExtra("subjectId", 0);
        deviceId = getIntent().getIntExtra("deviceId", 0);
        serverId = getIntent().getIntExtra("serverId", 0);

        if (deviceId >= 2)
            numMeasurement = 9;
        else
            numMeasurement = 6;

        testId = getIntent().getIntExtra("TestId", 0);
        score = getIntent().getIntExtra("Score", 0);

        /***
        odSph = getIntent().getDoubleExtra("ODS", 0.00);
        odCyl = getIntent().getDoubleExtra("ODC", 0.00);
        odAxis = getIntent().getDoubleExtra("ODA", 0.00);
        odSe = getIntent().getStringExtra("ODE");
        odRmse = getIntent().getDoubleExtra("ODR", 0.00);

        osSph = getIntent().getDoubleExtra("OSS", 0.00);
        osCyl = getIntent().getDoubleExtra("OSC", 0.00);
        osAxis = getIntent().getDoubleExtra("OSA", 0.00);
        osSe = getIntent().getStringExtra("OSE");
        osRmse = getIntent().getDoubleExtra("OSR", 0.00);
         ***/

        odSph = getIntent().getStringExtra("ODSPH");
        osSph = getIntent().getStringExtra("OSSPH");
        odCyl = getIntent().getStringExtra("ODCYL");
        osCyl = getIntent().getStringExtra("OSCYL");

        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        final TextView testCompleteHeaderTextView = (TextView) findViewById(R.id.testCompleteHeaderTextView);
        final TextView odSpheTextView = (TextView) findViewById(R.id.odSpheTextView);
        final TextView osSpheTextView = (TextView) findViewById(R.id.osSpheTextView);
        final TextView odCylTextView = (TextView) findViewById(R.id.odCylTextView);
        final TextView osCylTextView = (TextView) findViewById(R.id.osCylTextView);

        testCompleteHeaderTextView.setText("Test Complete for " + SingletonDataHolder.firstName);
        odSpheTextView.setText(odSph + "D");
        osSpheTextView.setText(osSph + "D");
        odCylTextView.setText(odCyl);
        osCylTextView.setText(osCyl);

        final TextView btnSphPopup = (TextView)findViewById(R.id.sphText);
        btnSphPopup.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                LayoutInflater layoutInflater
                        = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.window_popup, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                /** Comment out
                 Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
                 btnDismiss.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
                }});
                 ***/
                popupWindow.showAsDropDown(btnSphPopup, -300, 10, Gravity.CENTER);
            }});

        final TextView btnCylPopup = (TextView)findViewById(R.id.cylText);
        btnCylPopup.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                LayoutInflater layoutInflater
                        = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.window_popup_astigmatism, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                /** Comment out
                 Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
                 btnDismiss.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
                }});
                 ***/
                popupWindow.showAsDropDown(btnCylPopup, -500, 10, Gravity.CENTER);
            }});

        // odSphTextView.setText(String.format("%.2f", odSph));
        // odCylTextView.setText(String.format("%.2f", odCyl));
        // odAxisTextView.setText(Integer.toString((int) odAxis));
        // osSphTextView.setText(String.format("%.2f", osSph));
        // osCylTextView.setText(String.format("%.2f", osCyl));
        // osAxisTextView.setText(Integer.toString((int) osAxis));

        /*
        angleList[0] = getIntent().getDoubleExtra("Angle-1", 0.00);
        angleList[1] = getIntent().getDoubleExtra("Angle-2", 0.00);
        angleList[2] = getIntent().getDoubleExtra("Angle-3", 0.00);
        angleList[3] = getIntent().getDoubleExtra("Angle-4", 0.00);
        angleList[4] = getIntent().getDoubleExtra("Angle-5", 0.00);
        angleList[5] = getIntent().getDoubleExtra("Angle-6", 0.00);

        if (deviceId >= 2) {
            angleList[6] = getIntent().getDoubleExtra("Angle-7", 0.00);
            angleList[7] = getIntent().getDoubleExtra("Angle-8", 0.00);
            angleList[8] = getIntent().getDoubleExtra("Angle-9", 0.00);
        }

        leftPowerList[0] = getIntent().getDoubleExtra("L-Power-1", 0.00);
        leftPowerList[1] = getIntent().getDoubleExtra("L-Power-2", 0.00);
        leftPowerList[2] = getIntent().getDoubleExtra("L-Power-3", 0.00);
        leftPowerList[3] = getIntent().getDoubleExtra("L-Power-4", 0.00);
        leftPowerList[4] = getIntent().getDoubleExtra("L-Power-5", 0.00);
        leftPowerList[5] = getIntent().getDoubleExtra("L-Power-6", 0.00);

        if (deviceId >= 2) {
            leftPowerList[6] = getIntent().getDoubleExtra("L-Power-7", 0.00);
            leftPowerList[7] = getIntent().getDoubleExtra("L-Power-8", 0.00);
            leftPowerList[8] = getIntent().getDoubleExtra("L-Power-9", 0.00);
        }

        rightPowerList[0] = getIntent().getDoubleExtra("R-Power-1", 0.00);
        rightPowerList[1] = getIntent().getDoubleExtra("R-Power-2", 0.00);
        rightPowerList[2] = getIntent().getDoubleExtra("R-Power-3", 0.00);
        rightPowerList[3] = getIntent().getDoubleExtra("R-Power-4", 0.00);
        rightPowerList[4] = getIntent().getDoubleExtra("R-Power-5", 0.00);
        rightPowerList[5] = getIntent().getDoubleExtra("R-Power-6", 0.00);

        if (deviceId >= 2) {
            rightPowerList[6] = getIntent().getDoubleExtra("R-Power-7", 0.00);
            rightPowerList[7] = getIntent().getDoubleExtra("R-Power-8", 0.00);
            rightPowerList[8] = getIntent().getDoubleExtra("R-Power-9", 0.00);
        }

        leftDistList[0] = getIntent().getIntExtra("L-Dist-1", 0);
        leftDistList[1] = getIntent().getIntExtra("L-Dist-2", 1);
        leftDistList[2] = getIntent().getIntExtra("L-Dist-3", 2);
        leftDistList[3] = getIntent().getIntExtra("L-Dist-4", 3);
        leftDistList[4] = getIntent().getIntExtra("L-Dist-5", 4);
        leftDistList[5] = getIntent().getIntExtra("L-Dist-6", 5);

        if (deviceId >= 2) {
            leftDistList[6] = getIntent().getIntExtra("L-Dist-7", 6);
            leftDistList[7] = getIntent().getIntExtra("L-Dist-8", 7);
            leftDistList[8] = getIntent().getIntExtra("L-Dist-9", 8);
        }

        rightDistList[0] = getIntent().getIntExtra("R-Dist-1", 0);
        rightDistList[1] = getIntent().getIntExtra("R-Dist-2", 1);
        rightDistList[2] = getIntent().getIntExtra("R-Dist-3", 2);
        rightDistList[3] = getIntent().getIntExtra("R-Dist-4", 3);
        rightDistList[4] = getIntent().getIntExtra("R-Dist-5", 4);
        rightDistList[5] = getIntent().getIntExtra("R-Dist-6", 5);

        if (deviceId >= 2) {
            rightDistList[6] = getIntent().getIntExtra("R-Dist-7", 6);
            rightDistList[7] = getIntent().getIntExtra("R-Dist-8", 7);
            rightDistList[8] = getIntent().getIntExtra("R-Dist-9", 8);
        }


        final TextView l1TextView = (TextView) findViewById(R.id.l1TextView);
        final TextView l2TextView = (TextView) findViewById(R.id.l2TextView);
        final TextView l3TextView = (TextView) findViewById(R.id.l3TextView);
        final TextView l4TextView = (TextView) findViewById(R.id.l4TextView);
        final TextView l5TextView = (TextView) findViewById(R.id.l5TextView);
        final TextView l6TextView = (TextView) findViewById(R.id.l6TextView);
        final TextView l7TextView = (TextView) findViewById(R.id.l7TextView);
        final TextView l8TextView = (TextView) findViewById(R.id.l8TextView);
        final TextView l9TextView = (TextView) findViewById(R.id.l9TextView);

        mStr = String.format("%.2f", leftPowerList[0]) + " " + String.format("%.1f", angleList[0]) + (char) 0x00B0;
        l1TextView.setText(mStr);
        mStr = String.format("%.2f", leftPowerList[1]) + " " + String.format("%.1f", angleList[1]) + (char) 0x00B0;
        l2TextView.setText(mStr);
        mStr = String.format("%.2f", leftPowerList[2]) + " " + String.format("%.1f", angleList[2]) + (char) 0x00B0;
        l3TextView.setText(mStr);
        mStr = String.format("%.2f", leftPowerList[3]) + " " + String.format("%.1f", angleList[3]) + (char) 0x00B0;
        l4TextView.setText(mStr);
        mStr = String.format("%.2f", leftPowerList[4]) + " " + String.format("%.1f", angleList[4]) + (char) 0x00B0;
        l5TextView.setText(mStr);
        mStr = String.format("%.2f", leftPowerList[5]) + " " + String.format("%.1f", angleList[5]) + (char) 0x00B0;
        l6TextView.setText(mStr);

        if (deviceId >= 2) {
            mStr = String.format("%.2f", leftPowerList[6]) + " " + String.format("%.1f", angleList[6]) + (char) 0x00B0;
            l7TextView.setText(mStr);
            mStr = String.format("%.2f", leftPowerList[7]) + " " + String.format("%.1f", angleList[7]) + (char) 0x00B0;
            l8TextView.setText(mStr);
            mStr = String.format("%.2f", leftPowerList[8]) + " " + String.format("%.1f", angleList[8]) + (char) 0x00B0;
            l9TextView.setText(mStr);
        }

        final TextView r1TextView = (TextView) findViewById(R.id.r1TextView);
        final TextView r2TextView = (TextView) findViewById(R.id.r2TextView);
        final TextView r3TextView = (TextView) findViewById(R.id.r3TextView);
        final TextView r4TextView = (TextView) findViewById(R.id.r4TextView);
        final TextView r5TextView = (TextView) findViewById(R.id.r5TextView);
        final TextView r6TextView = (TextView) findViewById(R.id.r6TextView);
        final TextView r7TextView = (TextView) findViewById(R.id.r7TextView);
        final TextView r8TextView = (TextView) findViewById(R.id.r8TextView);
        final TextView r9TextView = (TextView) findViewById(R.id.r9TextView);

        mStr = String.format("%.2f", rightPowerList[0]) + " " + String.format("%.1f", angleList[0]) + (char) 0x00B0;
        r1TextView.setText(mStr);
        mStr = String.format("%.2f", rightPowerList[1]) + " " + String.format("%.1f", angleList[1]) + (char) 0x00B0;
        r2TextView.setText(mStr);
        mStr = String.format("%.2f", rightPowerList[2]) + " " + String.format("%.1f", angleList[2]) + (char) 0x00B0;
        r3TextView.setText(mStr);
        mStr = String.format("%.2f", rightPowerList[3]) + " " + String.format("%.1f", angleList[3]) + (char) 0x00B0;
        r4TextView.setText(mStr);
        mStr = String.format("%.2f", rightPowerList[4]) + " " + String.format("%.1f", angleList[4]) + (char) 0x00B0;
        r5TextView.setText(mStr);
        mStr = String.format("%.2f", rightPowerList[5]) + " " + String.format("%.1f", angleList[5]) + (char) 0x00B0;
        r6TextView.setText(mStr);

        if (deviceId >= 2) {
            mStr = String.format("%.2f", rightPowerList[6]) + " " + String.format("%.1f", angleList[6]) + (char) 0x00B0;
            r7TextView.setText(mStr);
            mStr = String.format("%.2f", rightPowerList[7]) + " " + String.format("%.1f", angleList[7]) + (char) 0x00B0;
            r8TextView.setText(mStr);
            mStr = String.format("%.2f", rightPowerList[8]) + " " + String.format("%.1f", angleList[8]) + (char) 0x00B0;
            r9TextView.setText(mStr);
        }
        */

        // Tap  Don't use textView
        TextView dontUseThisTest = (TextView) findViewById(R.id.dontUseTextView);
        dontUseThisTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        ResultActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle("Confirm");
                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want to discard this test?");
                // Setting Positive "Yes" Btn
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                confirmCode = 1;
                                ConfirmTest();
                            }
                        });

                // Setting Negative "NO" Btn
                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                // Toast.makeText(getApplicationContext(),
                                // "You clicked on NO", Toast.LENGTH_SHORT)
                                // .show();
                                dialog.cancel();
                            }
                        });
                // Showing Alert Dialog
                alertDialog.show();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(ResultActivity.this, "Record Discarded", Toast.LENGTH_SHORT).show();
                confirmCode = 2;
                ConfirmTest();
            }
        });

        /***
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUploadComplete) {
                    NetConnection conn = new NetConnection();
                    if (conn.isConnected(getApplicationContext())) {
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url;
                        // if (serverId == 0)
                        // url = Constants.RestfulBaseURL + "/eyecloud/api/testresults";
                        // else
                        url = Constants.RestfulBaseURL + "/eyecloud/api/testresults?access_token=" + Constants.AccessToken;
                        // String url = Constants.RestfulBaseURL + "/eyecloud/api/testresults";
                        // String urlGet = Constants.RestfulBaseURL + "/eyecloud/api/testresults?access_token=" + Constants.AccessToken;

                        final UUID idOne = UUID.randomUUID();


                        final JSONObject params = new JSONObject();
                        try {

                            params.put("axisOS", Integer.toString((int) osAxis));
                            params.put("axisOD", Integer.toString((int) odAxis));
                            params.put("cylOD", String.format("%.2f", odCyl));
                            params.put("cylOS", String.format("%.2f", osCyl));
                            params.put("sphOD", String.format("%.2f", odSph));
                            params.put("sphOS", String.format("%.2f", osSph));
                            params.put("sphEOD", String.format("%.2f", odSe));
                            params.put("sphEOS", String.format("%.2f", osSe));
                            params.put("rmseOD", String.format("%.2f", odRmse));
                            params.put("rmseOS", String.format("%.2f", osRmse));
                            params.put("bCalcByPhone", "true");
                            params.put("deviceID", "null");
                            params.put("testType", "Full Refraction");
                            if (deviceId == 2)
                                params.put("deviceName", "Device 3");
                            else if (deviceId == 3)
                                params.put("deviceName", "Device 5");
                            else
                                params.put("deviceName", "Device 1");
                            params.put("phoneType", "Galaxy 6");
                            params.put("accomodationPatternName", "AP5G");
                            params.put("beamSplitter", "false");
                            params.put("originalTestResultID", idOne);
                            params.put("subjectID", String.valueOf(subjectId));

                            // if (serverId > 0) {
                            JSONArray mDataArr = new JSONArray();
                            for (int i = 0; i < numMeasurement; i++) {
                                JSONObject mDataObj = new JSONObject();
                                mDataObj.put("angle", String.format("%.1f", angleList[i]));
                                mDataObj.put("distance", Integer.toString(rightDistList[i]));
                                String mIdStr = "R-" + Integer.toString(i);
                                mDataObj.put("mId", mIdStr);
                                mDataObj.put("rightEye", 1);
                                // mDataObj.put("rightEye", "true");
                                mDataObj.put("power", String.format("%.2f", rightPowerList[i]));
                                mDataObj.put("subjectID", String.valueOf(subjectId));
                                mDataObj.put("testID", String.valueOf(idOne));
                                mDataArr.put(mDataObj);
                            }
                            for (int i = 0; i < numMeasurement; i++) {
                                JSONObject mDataObj = new JSONObject();
                                mDataObj.put("angle", String.format("%.1f", angleList[i]));
                                mDataObj.put("distance", Integer.toString(leftDistList[i]));
                                String mIdStr = "L-" + Integer.toString(i);
                                mDataObj.put("mId", mIdStr);
                                mDataObj.put("rightEye", 0);
                                // mDataObj.put("rightEye", "true");
                                mDataObj.put("power", String.format("%.2f", leftPowerList[i]));
                                mDataObj.put("subjectID", String.valueOf(subjectId));
                                mDataObj.put("testID", String.valueOf(idOne));
                                mDataArr.put(mDataObj);
                            }
                            params.put("measurements", mDataArr);
                            // }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i(TAG, response);
                                isUploadComplete = true;
                                Toast.makeText(ResultActivity.this,
                                        "Use this record", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getBaseContext(), TopActivity.class);
                                startActivity(i);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ResultActivity.this,
                                        "Can't connect to the *** server", Toast.LENGTH_SHORT).show();
                                Log.d("Error.Response", error.toString());
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
                        Toast.makeText(ResultActivity.this,
                                "Can't connect to the server", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(ResultActivity.this,
                            "Already Uploaded", Toast.LENGTH_SHORT).show();
            }
        });
        ****/

    }

    private void ConfirmTest() {

        String url = Constants.UrlConfirmTest;
        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            final JSONObject params = new JSONObject();
            try {
                params.put("test_condition_id", testId);
                params.put("discarded", confirmCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG, response);
                    if (confirmCode == 2)
                        Toast.makeText(ResultActivity.this, "Test Saved", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(ResultActivity.this, "Test Discarded", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getBaseContext(), TopActivity.class);
                    startActivity(i);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // showProgress(false);
                    Log.d("Error.Response", error.toString());
                    Toast.makeText(ResultActivity.this, "Save failed, please try it again", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    Log.i("$$$---DC JSON---$$$", params.toString());
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
        } else
            Toast.makeText(ResultActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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
