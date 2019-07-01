package com.eyeque.eyeque;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.eyeque.eyeque.R;

import java.io.IOException;

/**
 *
 * File:            AttachDeviceActivity.java
 * Description:     This page provides the guidance for user to attach the optical device to
 *                  the screen. It also let user to choose which test they want to perform:
 *                  1 - Quick Test   2 - Full Test
 * Created:         2016/07/10
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class AttachDeviceActivity extends AppCompatActivity {
    // TODO: Rename parameter arguments, choose names that match
    // private PatternView patternView;
    private static int deviceId;
    private PatternView patternView;
    Button exitButton;
    MediaPlayer mp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        setContentView(R.layout.activity_attach_device);

        // Dynamically set the box center position based om the screen size
        SingletonDataHolder.phoneDisplay = android.os.Build.DISPLAY;
        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int width = size. x;
        int height = size. y;
        SingletonDataHolder.centerX = width / 2;
        // SingletonDataHolder.centerY = Math.round((float) SingletonDataHolder.centerY * (float) SingletonDataHolder.phonePpi / 520.0f);

        deviceId = 3;
        SingletonDataHolder.accommodationOn = false;
        patternView = (PatternView) findViewById(R.id.drawView);
        patternView.setDeviceId((int) deviceId);
        patternView.setdrawDeviceOnly(true);
        SingletonDataHolder.noDevice = false;
        patternView.start();

        mp = new MediaPlayer();
        if (SingletonDataHolder.testMode == 1) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            try {
                mp.reset();
                mp.setDataSource(getApplicationContext(),
                        Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.practice_attach_device));
                mp.prepare();
                mp.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Button attachDeviceContinueButton = (Button) findViewById(R.id.attachDeviceContinueButton);
        attachDeviceContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SingletonDataHolder.correctDisplaySetting) {
                    if (SingletonDataHolder.testMode == 1) {
                        SingletonDataHolder.practiceGenericPageNum = 1;
                        Intent practiceGenericIntent = new Intent(getBaseContext(), PracticeGenericActivity.class);
                        practiceGenericIntent.putExtra("deviceId", 3);
                        startActivity(practiceGenericIntent);
                    } else {
                        // SingletonDataHolder.accommodationOn = true;
                        Intent testCondIntent = new Intent(getBaseContext(), TestConditionActivity.class);
                        testCondIntent.putExtra("deviceId", 3);
                        startActivity(testCondIntent);
                    }
                    if (mp.isPlaying()) {
                        mp.stop();
                    }
                    finish();
                } else
                    showDisplaySettingAlert();
            }
        });

        exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                        AttachDeviceActivity.this);

                // Setting Dialog Title
                if (SingletonDataHolder.lang.equals("zh")) {
                    alertDialog2.setTitle("确认退出...");
                    // Setting Dialog Message
                    alertDialog2.setMessage("你真的要退出测试吗?");
                    // Setting Positive "Yes" Btn
                    alertDialog2.setPositiveButton("是",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });

                    // Setting Negative "NO" Btn
                    alertDialog2.setNegativeButton("暂时不",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to execute after dialog
                                    // Toast.makeText(getApplicationContext(),
                                    // "You clicked on NO", Toast.LENGTH_SHORT)
                                    // .show();
                                    dialog.cancel();
                                }
                            });
                }
                else {
                    alertDialog2.setTitle("Confirm Exit...");
                    // Setting Dialog Message
                    if (SingletonDataHolder.testMode == 1)
                        alertDialog2.setMessage("Are you sure you want to quit this practice session?");
                    else
                        alertDialog2.setMessage("Are you sure you want exit this test?");
                    // Setting Positive "Yes" Btn
                    alertDialog2.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mp.isPlaying()) {
                                        mp.stop();
                                    }
                                    finish();
                                }
                            });

                    // Setting Negative "NO" Btn
                    alertDialog2.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to execute after dialog
                                    // Toast.makeText(getApplicationContext(),
                                    // "You clicked on NO", Toast.LENGTH_SHORT)
                                    // .show();
                                    dialog.cancel();
                                }
                            });
                }


                // Showing Alert Dialog
                alertDialog2.show();
            }
        });
    }

    public void showDisplaySettingAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("The test requires the display is in maximum resolution. Please go to settings and change the display resolution to maximum and try it again.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
