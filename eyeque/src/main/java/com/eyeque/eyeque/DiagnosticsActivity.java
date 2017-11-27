package com.eyeque.eyeque;

import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.DisplayMetrics;
/**
 *
 * File:            DiagnosticsActivity.java
 * Description:     Diagnostics screen to display app version, network connection status,
 *                  device's display information and other various runtime parameter values
 * Created:         2017/01/31
 * Author:          George Zhao
 *
 * Copyright (c) 2017 EyeQue Corp
 */
public class DiagnosticsActivity extends AppCompatActivity {

    // Tag for log message
    private static final String TAG = DiagnosticsActivity.class.getSimpleName();
    private static String diagText;
    private static String emailText;

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
        setContentView(R.layout.activity_diagnostics);

        TextView et = (TextView) findViewById(R.id.diagnosticTextView);
        et.setEnabled(false);

        NetConnection conn = new NetConnection();
        diagText = "OS Version = " + Build.VERSION.RELEASE + "\n";
        diagText += "App Version = " + Constants.BuildNumber + "\n";
        if (conn.isConnected(getApplicationContext()))
            diagText += "Internet Connection = " + "ON\n\n";
        else
            diagText += "Internet Connection = " + "OFF\n\n";
        diagText += "Device Parameters:" + "\n";
        diagText += "\u2022  Manufacturer = " + SingletonDataHolder.phoneManufacturer + "\n";
        diagText += "\u2022  Brand = " + SingletonDataHolder.phoneBrand + "\n";
        diagText += "\u2022  Model = " + SingletonDataHolder.phoneModel + "\n";
        diagText += "\u2022  Product = " + SingletonDataHolder.phoneProduct + "\n";
        diagText += "\u2022  Device = " + SingletonDataHolder.phoneDevice + "\n";
        diagText += "\u2022  Hardware =" + SingletonDataHolder.phoneHardware + "\n";
        diagText += "\u2022  Serial = " + SingletonDataHolder.phoneSerialNum + "\n";
        diagText += "\u2022  Display = " + SingletonDataHolder.phoneDisplay + "\n";
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        diagText += "\u2022  WidthPixels = " + metrics.widthPixels + "\n";
        diagText += "\u2022  HeightPixels = " + metrics.heightPixels + "\n";
        diagText += "\u2022  Xdpi = " + metrics.xdpi + "\n";
        diagText += "\u2022  Ydpi = " + metrics.ydpi + "\n";
        diagText += "\u2022  Density:" + metrics.density + "\n\n";


        diagText += "System Parameters:" + "\n";
        // diagText += "\u2022 Token = " + SingletonDataHolder.token + "\n";
        diagText += "\u2022   Email = " + SingletonDataHolder.email + "\n";
        // diagText += "\u2022   Ppi = " + SingletonDataHolder.phonePpi + "\n";

        /*** Omit user profile
        diagText += "\u2022 UserProfile = "
                    + SingletonDataHolder.firstName + ":"
                    + SingletonDataHolder.lastName + ":"
                    + SingletonDataHolder.birthYear + ":"
                    + SingletonDataHolder.gender + "\n";
        ***/
        // diagText += "\u2022 UserApiRespData = " + SingletonDataHolder.userApiRespData + "\n";
        diagText += "\u2022  deviceSerialNum = " + SingletonDataHolder.deviceSerialNum + "\n";
        diagText += "\u2022  DeviceData = "
                + "PT:" + SingletonDataHolder.phoneType
                + "PPI:" + SingletonDataHolder.phonePpi
                + "DH:" + SingletonDataHolder.deviceHeight
                + "DW:" + SingletonDataHolder.deviceWidth
                + "CX:" + Integer.toString(SingletonDataHolder.centerX)
                + "CY:" + Integer.toString(SingletonDataHolder.centerY)
                + "LL:" + Integer.toString(SingletonDataHolder.lineLength)
                + "LW:" + Integer.toString(SingletonDataHolder.lineWidth)
                + "ID:" + Integer.toString(SingletonDataHolder.initDistance)
                + "MinD:" + Integer.toString(SingletonDataHolder.minDistance)
                + "MaxD:" + Integer.toString(SingletonDataHolder.maxDistance) + "\n";
        if (SingletonDataHolder.dahsboardApiRespData != "")
            // diagText += "\u2022 DahsboardApiRespData = " + SingletonDataHolder.dahsboardApiRespData + "\n";
            diagText += "\u2022  DahsboardData = " + "Ok\n";
        else
            diagText += "\u2022  DahsboardData = " + "Failed\n";
        et.setText(diagText);

        Button nextButton = (Button) findViewById(R.id.sendButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@eyeque.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "App Diagnostic Data Report");
                emailText = "State your issue here: \n\n\n";
                emailText += diagText;
                i.putExtra(Intent.EXTRA_TEXT, emailText);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(DiagnosticsActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

