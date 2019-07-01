package com.eyeque.eyeque;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.eyeque.eyeque.R;
import static com.eyeque.eyeque.R.id.glassRemovedNoCheckbox;
import static com.eyeque.eyeque.R.id.glassRemovedYesCheckbox;
// import static com.eyeque.eyeque.R.id.regularLensNoCheckbox;
// import static com.eyeque.eyeque.R.id.regularLensYesCheckbox;
import static com.eyeque.eyeque.R.id.screenProtectorOnNoCheckbox;
import static com.eyeque.eyeque.R.id.screenProtectorOnYesCheckbox;

/**
 *
 * File:            TestConidtionActivity.java
 * Description:     The screen let user check the test condition so the calculation can take
 *                  this into account for test result
 * Created:         2016/07/10
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class TestConditionActivity extends AppCompatActivity {

    private PatternView patternView;
    private static int deviceId;
    private boolean glassRemovedYesChecked = false;
    private boolean glassRemovedNoChecked = false;
    private boolean screenProtectorOnYesChecked = false;
    private boolean screenProtectorOnNoChecked = false;
    private CheckBox glassRemovedYesCheckBox;
    private CheckBox glassRemovedNoCheckBox;
    private CheckBox screenProtectorOnYesCheckBox;
    private CheckBox screenProtectorOnNoCheckBox;

    private boolean regularLensYesChecked = false;
    private boolean regularLensNoChecked = false;
    private CheckBox regularLensYesCheckBox;
    private CheckBox regularLensNoCheckBox;

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
        setContentView(R.layout.activity_test_condition);

        // Dynamically set the box center position based om the screen size
        SingletonDataHolder.phoneDisplay = android.os.Build.DISPLAY;
        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int width = size. x;
        int height = size. y;
        SingletonDataHolder.centerX = width / 2;
        // SingletonDataHolder.centerY = Math.round((float) SingletonDataHolder.centerY * (float) SingletonDataHolder.phonePpi / 520.0f);

        patternView = (PatternView) findViewById(R.id.drawView);
        // Draw the device mounting line. Hard code for now.
        // Need to dynamically populate when supporting multiple devices
        deviceId = 3;
        patternView.setDeviceId((int) deviceId);
        patternView.setdrawDeviceOnly(true);
        patternView.start();

        Button testConditionContinueButton = (Button) findViewById(R.id.testConditionContinueButton);
        assert testConditionContinueButton != null;
        testConditionContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!glassRemovedYesChecked && !glassRemovedNoChecked
                        || !screenProtectorOnYesChecked && !screenProtectorOnNoChecked)
                    Toast.makeText(TestConditionActivity.this, "Please confirm the test condition", Toast.LENGTH_SHORT).show();
                else {
                    if (glassRemovedYesChecked)
                        SingletonDataHolder.wearGlasses = 0;
                    else
                        SingletonDataHolder.wearGlasses = 1;
                    if (screenProtectorOnYesChecked)
                        SingletonDataHolder.screenProtect = 1;
                    else
                        SingletonDataHolder.screenProtect = 0;
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    i.putExtra("subjectId", 21);
                    i.putExtra("deviceId", 3);
                    i.putExtra("serverId", 1);
                    startActivity(i);
                    finish();
                }
            }
        });

        glassRemovedYesCheckBox = (CheckBox) findViewById(glassRemovedYesCheckbox);
        glassRemovedNoCheckBox = (CheckBox) findViewById(glassRemovedNoCheckbox);
        screenProtectorOnYesCheckBox = (CheckBox) findViewById(screenProtectorOnYesCheckbox);
        screenProtectorOnNoCheckBox = (CheckBox) findViewById(screenProtectorOnNoCheckbox);
        // regularLensYesCheckBox = (CheckBox) findViewById(regularLensYesCheckbox);
        // regularLensNoCheckBox = (CheckBox) findViewById(regularLensNoCheckbox);

        /****
        if (SingletonDataHolder.deviceName.equals("EQ101")) {
            regularLensYesCheckBox.setChecked(true);
            regularLensYesChecked = true;
            regularLensNoCheckBox.setChecked(false);
            regularLensNoChecked = false;
        } else {
            regularLensYesCheckBox.setChecked(false);
            regularLensYesChecked = false;
            regularLensNoCheckBox.setChecked(true);
            regularLensNoChecked = true;
        }
         ****/
     }

    public void glassRemovedYesClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            glassRemovedYesChecked = true;
            glassRemovedNoChecked = false;
            glassRemovedNoCheckBox.setChecked(false);
            glassRemovedNoCheckBox.setSelected(false);
        }
        else
            glassRemovedYesChecked = false;
    }

    public void glassRemovedNoClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            glassRemovedYesChecked = false;
            glassRemovedNoChecked = true;
            glassRemovedYesCheckBox.setChecked(false);
            glassRemovedYesCheckBox.setSelected(false);
        }
        else
            glassRemovedNoChecked = false;
    }

    public void screenProtectorOnYesClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            screenProtectorOnYesChecked = true;
            screenProtectorOnNoChecked = false;
            screenProtectorOnNoCheckBox.setChecked(false);
            screenProtectorOnNoCheckBox.setSelected(false);
        }
        else
            screenProtectorOnYesChecked = false;
    }

    public void screenProtectorOnNoClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            screenProtectorOnYesChecked = false;
            screenProtectorOnNoChecked = true;
            screenProtectorOnYesCheckBox.setChecked(false);
            screenProtectorOnYesCheckBox.setSelected(false);
        }
        else
            screenProtectorOnNoChecked = false;

    }

    public void regularLensYesClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            regularLensYesChecked = true;
            regularLensNoChecked = false;
            regularLensNoCheckBox.setChecked(false);
            regularLensNoCheckBox.setSelected(false);
            SingletonDataHolder.deviceName = "EQ101";
        }
        else
            regularLensNoChecked = false;
    }

    public void regularLensNoClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            regularLensYesChecked = false;
            regularLensNoChecked = true;
            regularLensYesCheckBox.setChecked(false);
            regularLensYesCheckBox.setSelected(false);
            SingletonDataHolder.deviceName = "EQ101_MARK";
        }
        else
            regularLensYesChecked = false;

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
