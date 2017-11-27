package com.eyeque.eyeque;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import android.util.Log;
import com.eyeque.eyeque.R;
/**
 *
 * File:            PlayModeResultAcvitity.java
 * Description:     The screen gives the result of quick test
 * Created:         2016/10/05
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */

public class PlayModeResultActivity extends Activity {

    private static int subjectId;
    private static int deviceId;
    private static int serverId;
    private static String odSph;
    private static String odCyl;
    private static String osSph;
    private static String osCyl;

    // Tag for log message
    private static final String TAG = PlayModeResultActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_playmode_result);

        subjectId = getIntent().getIntExtra("subjectId", 0);
        deviceId = getIntent().getIntExtra("deviceId", 0);
        serverId = getIntent().getIntExtra("serverId", 0);

        // odSe = Math.round(Double.parseDouble(getIntent().getStringExtra("ODSPH"))*4/4.00);
        // osSe = Math.round(Double.parseDouble(getIntent().getStringExtra("OSSPH"))*4/4.00);
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

        testCompleteHeaderTextView.setText("Quick Test Completion for " + SingletonDataHolder.firstName);
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
                popupWindow.showAsDropDown(btnCylPopup, -300, 10, Gravity.CENTER);
            }});

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(PlayModeResultActivity.this, "Record Discarded", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getBaseContext(), TopActivity.class);
                startActivity(i);
                finish();
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
