package com.eyeque.eyeque;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.eyeque.eyeque.Constants;
import com.eyeque.eyeque.CountryListActivity;
import com.eyeque.eyeque.NetConnection;
import com.eyeque.eyeque.R;
import com.eyeque.eyeque.SingletonDataHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.eyeque.eyeque.R.id.readingGlassNoCheckbox;
import static com.eyeque.eyeque.R.id.readingGlassYesCheckbox;
import static com.eyeque.eyeque.R.id.wearEyeglassNoCheckbox;
import static com.eyeque.eyeque.R.id.wearEyeglassYesCheckbox;

/**
 *
 * File:            AccountActivity.java
 * Description:     This activity displays the user profile and allows user
 *                  to edit and save some profile parameters
 * Created:         2016/06/25
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "Account Acvitity";
    private EditText mEmailView;
    private EditText firstnameEt;
    private EditText lastnameEt;
    private EditText birthYearEt;
    private CheckBox maleCheckBox;
    private CheckBox femaleCheckBox;
    private CheckBox noPrefCheckBox;
    private boolean maleChecked;
    private boolean femaleChecked;
    private boolean noPrefChecked;
    private String firstName;
    private String lastName;
    private Integer yearNum;
    private EditText countryEt;
    private CheckBox wearEyeglassYesCheckBox;
    private CheckBox wearEyeglassNoCheckBox;
    private boolean wearEyeglassYesChecked = false;
    private boolean wearEyeglassNoChecked = false;
    private CheckBox readingGlassesYesCheckBox;
    private CheckBox readingGlassesNoCheckBox;
    private boolean readingGlassesYesChecked = false;
    private boolean readingGlassesNoChecked = false;
    private EditText readingGlassesValueEt;

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
        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_account);

        // Set up the account form.
        mEmailView = (EditText) findViewById(R.id.email);
        if (!SingletonDataHolder.email.matches(""))
            mEmailView.setText(SingletonDataHolder.email);
        else
            mEmailView.setText("Your registered email");
        mEmailView.setEnabled(false);

        firstnameEt = (EditText) findViewById(R.id.firstName);
        firstnameEt.requestFocus();
        if (!SingletonDataHolder.firstName.equals(""))
            firstnameEt.setText(SingletonDataHolder.firstName);
        lastnameEt = (EditText) findViewById(R.id.lastName);
        if (!SingletonDataHolder.lastName.equals(""))
            lastnameEt.setText(SingletonDataHolder.lastName);
        birthYearEt = (EditText) findViewById(R.id.brithYear);
        if (SingletonDataHolder.birthYear != 0)
            birthYearEt.setText(Integer.toString(SingletonDataHolder.birthYear));

        maleCheckBox = (CheckBox) findViewById(R.id.maleCheckbox);
        femaleCheckBox = (CheckBox) findViewById(R.id.femaleCheckbox);
        noPrefCheckBox = (CheckBox) findViewById(R.id.noPrefCheckbox);

        if (SingletonDataHolder.gender == 1) {
            maleCheckBox.setChecked(true);
            maleChecked = true;
            femaleChecked = false;
            noPrefChecked = false;
        }  else if (SingletonDataHolder.gender == 2) {
            femaleCheckBox.setChecked(true);
            femaleChecked = true;
            maleChecked = false;
            noPrefChecked = false;
        } else {
            noPrefCheckBox.setChecked(true);
            noPrefChecked = true;
            maleChecked = false;
            femaleChecked = false;
        }

        birthYearEt = (EditText) findViewById(R.id.brithYear);
        countryEt = (EditText) findViewById(R.id.country);
        if (!SingletonDataHolder.country.equals(""))
            countryEt.setText(SingletonDataHolder.country);
        countryEt.setKeyListener(null);
        countryEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    // Toast.makeText(getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
                    GetCountryList();
                }
            }
        });

        wearEyeglassYesCheckBox = (CheckBox) findViewById(wearEyeglassYesCheckbox);
        wearEyeglassNoCheckBox = (CheckBox) findViewById(wearEyeglassNoCheckbox);
        readingGlassesYesCheckBox = (CheckBox) findViewById(readingGlassYesCheckbox);
        readingGlassesNoCheckBox = (CheckBox) findViewById(readingGlassNoCheckbox);
        if (SingletonDataHolder.profileWearEyeglass) {
            wearEyeglassYesCheckBox.setChecked(true);
            wearEyeglassYesChecked = true;
            wearEyeglassNoChecked = false;
        } else {
            wearEyeglassNoCheckBox.setChecked(true);
            wearEyeglassYesChecked = false;
            wearEyeglassNoChecked = true;
        }
        if (SingletonDataHolder.profileWearReadingGlasses) {
            readingGlassesYesCheckBox.setChecked(true);
            readingGlassesYesChecked = true;
            readingGlassesNoChecked = false;
        } else {
            readingGlassesNoCheckBox.setChecked(true);
            readingGlassesYesChecked = false;
            readingGlassesNoChecked = true;
        }
        if ((!wearEyeglassYesChecked && !wearEyeglassNoChecked)
                || (!readingGlassesYesChecked && !readingGlassesNoChecked))
            Toast.makeText(AccountActivity.this, "Please provide the answer", Toast.LENGTH_SHORT).show();
        else {
            if (wearEyeglassYesChecked)
                SingletonDataHolder.profileWearEyeglass = true;
            else
                SingletonDataHolder.profileWearEyeglass = false;
            if (readingGlassesYesChecked)
                SingletonDataHolder.profileWearReadingGlasses = true;
            else
                SingletonDataHolder.profileWearReadingGlasses = false;
        }

        SingletonDataHolder.profileReadingGlassesValue_selected = "";
        readingGlassesValueEt = (EditText) findViewById(R.id.readingGlassValueEditText);
        readingGlassesValueEt.setText(SingletonDataHolder.profileReadingGlassesValue);
        readingGlassesValueEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nvaddListIntent = new Intent(getBaseContext(), NvaddListActivity.class);
                startActivity(nvaddListIntent);
            }
        });

        SingletonDataHolder.country_selected = "";

        Button saveButton = (Button) findViewById(R.id.account_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = firstnameEt.getText().toString();
                lastName = lastnameEt.getText().toString();
                if (firstName.matches("") || lastName.matches("")) {
                    Toast.makeText(AccountActivity.this, "Please enter your firstname and last name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!maleChecked && !femaleChecked && !noPrefChecked) {
                    Toast.makeText(AccountActivity.this, "Please choose the gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    String dob = birthYearEt.getText().toString();
                    try {
                        yearNum = Integer.parseInt(dob);
                        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                        if (yearNum >= (currentYear - 100) && yearNum <= (currentYear - 10)) {
                            UpdateProfile();
                        } else {
                            Toast.makeText(AccountActivity.this, "The birth year is out of range", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(AccountActivity.this, "The birth year is out of range", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if ((!wearEyeglassYesChecked && !wearEyeglassNoChecked)
                        || (!readingGlassesYesChecked && !readingGlassesNoChecked))
                    Toast.makeText(AccountActivity.this, "Please provide the answer on wearing distance or reading eyeglasses", Toast.LENGTH_SHORT).show();
                else {
                    if (wearEyeglassYesChecked)
                        SingletonDataHolder.profileWearEyeglass = true;
                    else
                        SingletonDataHolder.profileWearEyeglass = false;
                    if (readingGlassesYesChecked)
                        SingletonDataHolder.profileWearReadingGlasses = true;
                    else
                        SingletonDataHolder.profileWearReadingGlasses = false;
                }
                if (!SingletonDataHolder.country_selected.equals(""))
                    SingletonDataHolder.country = SingletonDataHolder.country_selected;
                if (!SingletonDataHolder.profileReadingGlassesValue_selected.equals(""))
                    SingletonDataHolder.profileReadingGlassesValue = SingletonDataHolder.profileReadingGlassesValue_selected;
                UpdateProfile();
            }
        });
    }

    public void maleClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            maleChecked = true;
            femaleChecked = false;
            noPrefChecked = false;
            femaleCheckBox.setChecked(false);
            femaleCheckBox.setSelected(false);
            noPrefCheckBox.setChecked(false);
            noPrefCheckBox.setSelected(false);
        }
        else
            maleChecked = false;
    }

    public void femaleClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            femaleChecked = true;
            maleChecked = false;
            noPrefChecked = false;
            maleCheckBox.setChecked(false);
            maleCheckBox.setSelected(false);
            noPrefCheckBox.setChecked(false);
            noPrefCheckBox.setSelected(false);
        }
        else
            femaleChecked = false;
    }


    public void noPrefClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            femaleChecked = false;
            maleChecked = false;
            noPrefChecked = true;
            maleCheckBox.setChecked(false);
            maleCheckBox.setSelected(false);
            femaleCheckBox.setChecked(false);
            femaleCheckBox.setSelected(false);
        }
        else
            noPrefChecked = false;
    }

    public void wearEyeglassYesClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            wearEyeglassYesChecked = true;
            wearEyeglassNoChecked = false;
            wearEyeglassNoCheckBox.setChecked(false);
            wearEyeglassNoCheckBox.setSelected(false);
        } else
            wearEyeglassYesChecked = false;
    }

    public void wearEyeglassNoClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            wearEyeglassYesChecked = false;
            wearEyeglassNoChecked = true;
            wearEyeglassYesCheckBox.setChecked(false);
            wearEyeglassYesCheckBox.setSelected(false);
        } else
            wearEyeglassNoChecked = false;
    }

    public void readingGlassesYesClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            readingGlassesYesChecked = true;
            readingGlassesNoChecked = false;
            readingGlassesNoCheckBox.setChecked(false);
            readingGlassesNoCheckBox.setSelected(false);
        } else
            readingGlassesYesChecked = false;
    }

    public void readingGlassesNoClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            readingGlassesYesChecked = false;
            readingGlassesNoChecked = true;
            readingGlassesYesCheckBox.setChecked(false);
            readingGlassesYesCheckBox.setSelected(false);
        } else
            readingGlassesNoChecked = false;
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!SingletonDataHolder.country_selected.equals("")) {
            countryEt.setText(SingletonDataHolder.country_selected);
            countryEt.clearFocus();
        }
        if (!SingletonDataHolder.profileReadingGlassesValue_selected.equals("")) {
            readingGlassesValueEt.setText(SingletonDataHolder.profileReadingGlassesValue_selected);
        }
    }

    private void UpdateProfile() {

        String url = Constants.UrlUserProfile;
        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            final JSONObject params = new JSONObject();
            final JSONObject tmpParams = new JSONObject();
            final JSONObject tmpParams1 = new JSONObject();
            final JSONObject tmpParams2 = new JSONObject();
            final JSONObject tmpParams3 = new JSONObject();
            final JSONObject tmpParams4 = new JSONObject();
            final JSONArray attrArray = new JSONArray();
            final JSONObject finalParams = new JSONObject();

            try {
                params.put("email", SingletonDataHolder.email);
                params.put("firstname", firstName);
                params.put("lastname", lastName);
                params.put("website_id", 1);
                params.put("store_id", 1);
                params.put("group_id", SingletonDataHolder.groupId);
                if (maleChecked)
                    params.put("gender", 1);
                else if (femaleChecked)
                    params.put("gender", 2);
                else
                    params.put("gender", 0);
                params.put("dob", birthYearEt.getText().toString() + "-01-01");
                if (SingletonDataHolder.country_selected != "") {
                    tmpParams.put("attribute_code", "country_preference");
                    tmpParams.put("value", SingletonDataHolder.country_selected);
                    attrArray.put(tmpParams);
                }
                tmpParams1.put("attribute_code", "wear_glasses_or_contacts");
                tmpParams1.put("value", SingletonDataHolder.profileWearEyeglass? "yes":"no");
                attrArray.put(tmpParams1);
                tmpParams2.put("attribute_code", "wear_reading_glasses");
                tmpParams2.put("value", SingletonDataHolder.profileWearReadingGlasses? "yes":"no");
                attrArray.put(tmpParams2);
                if (SingletonDataHolder.profileReadingGlassesValue != "") {
                    tmpParams3.put("attribute_code", "user_nvadd_input");
                    tmpParams3.put("value", SingletonDataHolder.profileReadingGlassesValue);
                    attrArray.put(tmpParams3);
                }
                params.put("custom_attributes", attrArray);
                finalParams.put("customer", params);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringRequest postRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG, response);
                    SingletonDataHolder.userApiRespData = response;
                    SingletonDataHolder.firstName = firstName;
                    SingletonDataHolder.lastName = lastName;
                    SingletonDataHolder.birthYear = yearNum;
                    if (maleChecked)
                        SingletonDataHolder.gender = 1;
                    else
                        SingletonDataHolder.gender = 2;
                    Toast.makeText(AccountActivity.this, "Profile Saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // showProgress(false);
                    Log.d("Error.Response", error.toString());
                    SingletonDataHolder.userApiRespData = error.toString();
                    Toast.makeText(AccountActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    Log.i("$$$---AJSON---$$$", finalParams.toString());
                    return finalParams.toString().getBytes();
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
            Toast.makeText(AccountActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    private void GetCountryList() {

        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = Constants.UrlCountryList;
            final JSONObject params = new JSONObject();

            StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("*** Country List ***", response);

                    //Parse the JSON response
                    try {
                        JSONArray jsonResponse = new JSONArray(response);
                        SingletonDataHolder.countryListItems.clear();
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            Log.i("*** Country ***", jsonResponse.getString(i));
                            SingletonDataHolder.countryListItems.add(jsonResponse.getString(i));
                        }
                        Intent countryListIntent = new Intent(getBaseContext(), CountryListActivity.class);
                        startActivity(countryListIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
                    String authStr = "Bearer " + SingletonDataHolder.token;
                    headers.put("Content-Type", "application/json;charset=UTF-8");
                    headers.put("Authorization", authStr);
                    return headers;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(Constants.NETCONN_TIMEOUT_VALUE, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            queue.add(postRequest);
        } else
            Toast.makeText(AccountActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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
