package com.eyeque.eyeque;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * File:            PracticeActivity.java
 * Description:     Main screen display image pattern, one green line and one red line
 *                  User can tap the + or - button to move the lines closer or further until
 *                  they are overlapped. The quick test and full test will go through 3 or 9
 *                  medians for each eye respectively. User can quit the test
 * Created:         2016/04/19
 * Author:          George Zhao
 *
 * Copyright (c) 2017 EyeQue Corp
 */

public class PracticeActivity extends Activity {

    private int totalNumOfTest;
    private PatternView patternView;
    private Pattern pattern;

    private static int minVal;
    private static int maxVal;
    private int prevStopValue = maxVal;
    private static int longPressStep = 1;
    private boolean closerOrFurther = true;
    private boolean toggleEye = true;

    // Long press event
    private boolean inLongPressMode = false;
    private final long REPEAT_DELAY = 50;
    private Handler repeatUpdateHandler = new Handler();

    // Inter Activity Parameters
    private static int subjectId;
    private static int deviceId;
    private static int serverId;
    private static long seconds;
    // private static int brightColor = Color.rgb(96, 96, 96);
    // private static int darkColor = Color.rgb(21, 21, 21);
    private static int brightColor = Color.rgb(255, 255, 255);
    private static int darkColor = Color.rgb(96, 96, 96);
    private static Button contButton;
    private static Button exitButton;
    private static Switch accomodationSwitch;
    private static int allowedOffset = 1;       // line overlapping offset with no device mode
    private static boolean contEnabled = false;
    private static boolean showPower = false;

    // Tag for log message
    private static final String TAG = PracticeActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
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

        setContentView(R.layout.activity_practice);

        subjectId = getIntent().getIntExtra("subjectId", 0);
        deviceId = getIntent().getIntExtra("deviceId", 0);
        serverId = getIntent().getIntExtra("serverId", 0);

        SingletonDataHolder.testMode = 1;

        switch (deviceId) {
            case 2:
                minVal = Constants.MINVAL_DEVICE_3;
                maxVal = Constants.MAXVAL_DEVICE_3;
                // longPressStep = 5;
                break;
            case 3:
                // minVal = Constants.MINVAL_DEVICE_5;
                // maxVal = Constants.MAXVAL_DEVICE_5;
                if (SingletonDataHolder.noDevice) {
                    minVal = -2*SingletonDataHolder.lineWidth - 25;
                    maxVal = 2*SingletonDataHolder.lineWidth + 25;
                } else {
                    minVal = SingletonDataHolder.minDistance;
                    maxVal = SingletonDataHolder.maxDistance - SingletonDataHolder.minDistance;
                }
                // longPressStep = 1;
                break;
            case 4:
                minVal = Constants.MINVAL_DEVICE_6;
                maxVal = Constants.MAXVAL_DEVICE_6;
                // longPressStep = 5;
                break;
            default:
                minVal = Constants.MINVAL_DEVICE_1;
                maxVal = Constants.MAXVAL_DEVICE_1;
                // longPressStep = 5;
                break;
        }

        class RepetitiveUpdater implements Runnable {
            @Override
            public void run() {
                if (inLongPressMode) {
                    if (closerOrFurther)
                        closerLongPressedAction();
                    else
                        furtherLongPressedAction();
                    repeatUpdateHandler.postDelayed(new RepetitiveUpdater(), REPEAT_DELAY);
                }
            }
        }

        /**
         * Widgets requires modification during interaction
         */
        final TextView inTestTv = (TextView) findViewById(R.id.testHeaderTextView);
        final TextView tv = (TextView) findViewById(R.id.powerText);
        final TextView dtv = (TextView) findViewById(R.id.distText);
        final TextView atv = (TextView) findViewById(R.id.angleText);

        // final PatternView patternView = (PatternView) findViewById(R.id.drawView);
        // final Pattern pattern = patternView.getPatternInstance();
        patternView = (PatternView) findViewById(R.id.drawView);
        pattern = patternView.getPatternInstance();
        patternView.setDeviceId((int) deviceId);
        patternView.start();
        // if (deviceId != 4) {
        AccormAnimation animation = new AccormAnimation(patternView);
        seconds = System.currentTimeMillis();
        animation.setDuration(seconds);
        animation.setSeconds(seconds);
        animation.setRepeatCount(Animation.INFINITE);
        patternView.startAnimation(animation);
        // }

        /*** v1.4
        final TextView eyeLeftText = (TextView) findViewById(R.id.eyeLeftText);
        final TextView eyeRightText = (TextView) findViewById(R.id.eyeRightText);
        final ImageView eyeImageView = (ImageView) findViewById(R.id.eyeImage);
         ***/

        final SeekBar alignSeekBar = (SeekBar) findViewById(R.id.alignSeekBar);
        alignSeekBar.setMax(maxVal);
        prevStopValue = maxVal;
        alignSeekBar.setProgress(maxVal);
        alignSeekBar.setVisibility(View.INVISIBLE);
        SingletonDataHolder.accommodationOn = false;
        dtv.setText("Distance: " + String.valueOf(pattern.getDistance()));
        atv.setText("Angle: " + String.valueOf(patternView.getAngle()) + (char) 0x00B0);
        DecimalFormat precision = new DecimalFormat("#.##");
        // Double i2 = Double.valueOf(precision.format(pattern.getPowerValue()));
        // tv.setText("Power: " + String.valueOf(i2));

        if (!showPower)
            dtv.setVisibility(View.INVISIBLE);
        atv.setVisibility(View.INVISIBLE);
        if (!showPower)
            tv.setVisibility(View.INVISIBLE);
        else {
            Double i2 = Double.valueOf(precision.format(pattern.getPowerValue()));
            tv.setText("Power: " + String.valueOf(i2));
        }
        final MediaPlayer mp = new MediaPlayer();

        exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                        PracticeActivity.this);

                // Setting Dialog Title
                if (SingletonDataHolder.lang.equals("zh")) {
                    alertDialog2.setTitle("确认退出...");
                    // Setting Dialog Message
                    alertDialog2.setMessage("你真的要退出测试吗?");
                    // Setting Positive "Yes" Btn
                    alertDialog2.setPositiveButton("是",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to execute after dialog
                                    // Toast.makeText(getApplicationContext(),
                                    // "You clicked on YES", Toast.LENGTH_SHORT)
                                    // .show();
                                    if (mp.isPlaying()) {
                                        mp.stop();
                                    }
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
                    alertDialog2.setMessage("Are you sure you want to quit this practice session?");
                    // Setting Positive "Yes" Btn
                    alertDialog2.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to execute after dialog
                                    // Toast.makeText(getApplicationContext(),
                                    // "You clicked on YES", Toast.LENGTH_SHORT)
                                    // .show();
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

        /*** v1.4
        if (pattern.getWhichEye()) {
            eyeRightText.setTextColor(brightColor);
            eyeLeftText.setTextColor(darkColor);
            eyeImageView.setImageResource(R.drawable.eye_right1);
        } else {
            eyeRightText.setTextColor(darkColor);
            eyeLeftText.setTextColor(brightColor);
            eyeImageView.setImageResource(R.drawable.eye_left1);
        }
         ***/

        if (SingletonDataHolder.testMode == 1)
            if (SingletonDataHolder.lang.equals("zh"))
                inTestTv.setText("快速测试 1/3");
            else
                inTestTv.setText("Practice 1/3");
        else
            if (SingletonDataHolder.lang.equals("zh"))
                inTestTv.setText("全面测试 1/9");
            else
                inTestTv.setText("Full Test 1/9");


        if (mp.isPlaying()) {
            mp.stop();
        }
        try {
            mp.reset();
            if (SingletonDataHolder.noDevice)
                mp.setDataSource(getApplicationContext(),
                        Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.practice_start));
            else
                mp.setDataSource(getApplicationContext(),
                    Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.start_test));
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        /****
         try {
         Thread.sleep(13000);
         mp.reset();
         mp.setDataSource(getApplicationContext(),
         Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.frp_1));
         mp.prepare();
         mp.start();
         } catch (IllegalStateException e) {
         e.printStackTrace();
         } catch (IOException e) {
         e.printStackTrace();
         } catch (InterruptedException e) {
         e.printStackTrace();
         }

         try {
         Thread.sleep(10000);
         mp.reset();
         mp.setDataSource(getApplicationContext(),
         Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.fri_2));
         mp.prepare();
         mp.start();
         } catch (IllegalStateException e) {
         e.printStackTrace();
         } catch (IOException e) {
         e.printStackTrace();
         } catch (InterruptedException e) {
         e.printStackTrace();
         }
         ****/

        /**
         * Add callback handler to change the pattern in pattern view
         */

        contButton = (Button) findViewById(R.id.contButton);
        contButton.setTextColor(Color.GRAY);
        contButton.setEnabled(false);
        contEnabled = false;
        contButton.setOnClickListener(new View.OnClickListener() {
            String str;

            @Override
            public void onClick(View v) {
                Log.i(TAG, "Next Button clicked.");

                if (contEnabled) {
                    int patternIndex = patternView.getPatternInstance().getPattenIndex();
                    if (patternIndex >= 2 && SingletonDataHolder.noDevice) {
                        // SingletonDataHolder.noDevice = false;
                        // SingletonDataHolder.accommodationOn = true;
                        Intent attachDevIntent = new Intent(getBaseContext(), AttachDeviceActivity.class);
                        startActivity(attachDevIntent);
                        finish();
                        return;
                    }

                    // if (prevStopValue == maxVal)
                        // return;

                    if (mp.isPlaying()) {
                        mp.stop();
                    }
                    // int patternIndex = patternView.getPatternInstance().getPattenIndex();
                    try {
                        mp.reset();
                        // int patternIndex = patternView.getPatternInstance().getPattenIndex();

                        switch (patternIndex) {
                            case 0:
                                if (deviceId == 4)
                                    mp.setDataSource(getApplicationContext(),
                                            // Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.mm1));
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.mm1));
                                else if (SingletonDataHolder.noDevice)
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.practice_2));
                                else if (pattern.getWhichEye())
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.frp_2));
                                else
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.flp_2));
                                break;

                            case 1:
                                if (deviceId == 4)
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.mm2));
                                else if (SingletonDataHolder.noDevice)
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.practice_3));
                                else if (pattern.getWhichEye())
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.frp_3));
                                else
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.flp_3));
                                break;
                            case 2:
                                if (deviceId == 4)
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.mm3));
                                else if (SingletonDataHolder.testMode == 1) {
                                    if (pattern.getWhichEye())
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.fr_last));
                                    else
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.fl_last));
                                } else {
                                    if (pattern.getWhichEye())
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.frp_4));
                                    else
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.flp_4));
                                }
                                break;
                            case 3:
                                if (deviceId == 4)
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.mm4));
                                else if (SingletonDataHolder.testMode == 0) {
                                    if (pattern.getWhichEye())
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.frp_5));
                                    else
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.flp_5));
                                }
                                break;
                            case 4:
                                if (deviceId == 4)
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.mm5));
                                else if (SingletonDataHolder.testMode == 0) {
                                    if (pattern.getWhichEye())
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.frp_6));
                                    else
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.flp_6));
                                }
                                break;
                            case 5:
                                if (deviceId == 4)
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.mm6));
                                else if (SingletonDataHolder.testMode == 0) {
                                    if (pattern.getWhichEye())
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.frp_7));
                                    else
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.flp_7));
                                }
                                break;
                            case 6:
                                if (deviceId == 4)
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.mm7));
                                else if (SingletonDataHolder.testMode == 0) {
                                    if (pattern.getWhichEye())
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.frp_8));
                                    else
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.flp_8));
                                }
                                break;
                            case 7:
                                if (deviceId == 4)
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.mm8));
                                else if (SingletonDataHolder.testMode == 0) {
                                    if (pattern.getWhichEye())
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.frp_9));
                                    else
                                        mp.setDataSource(getApplicationContext(),
                                                Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.flp_9));
                                }
                                break;
                            case 8:
                                if (deviceId == 4)
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.mm9));
                                if (pattern.getWhichEye())
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.fr_last));
                                else
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.fl_last));
                                break;
                            default:
                                if (deviceId == 4)
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.mm1));
                                else
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.m0));
                                break;
                        }

                        mp.prepare();
                        mp.start();

                        if (patternIndex == 2 && deviceId < 2) {
                            try {
                                Thread.sleep(3000);                 //1000 milliseconds is one second.
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                            if (mp.isPlaying()) {
                                mp.stop();
                            }
                            try {
                                mp.reset();
                                mp.setDataSource(getApplicationContext(),
                                        Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.turn90));
                                mp.prepare();
                                mp.start();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if ((deviceId < 2 && patternIndex == 5)
                                || (deviceId >= 2 && patternIndex == 8 && SingletonDataHolder.testMode == 0)
                                || (deviceId >= 2 && patternIndex == 2 && SingletonDataHolder.testMode == 1)) {
                            try {
                                Thread.sleep(2000);                 //1000 milliseconds is one second.
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                            if (mp.isPlaying()) {
                                mp.stop();
                            }
                            try {
                                mp.reset();
                                if (!pattern.getWhichEye() && pattern.isAllPatternComplete()) {

                                    // Announce Test Complete audio
                                    // mp.setDataSource(getApplicationContext(),
                                    // Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.mm81));
                                } else if (pattern.getWhichEye())
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.flp_1));
                                else
                                    mp.setDataSource(getApplicationContext(),
                                            Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.frp_1));
                                mp.prepare();
                                mp.start();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    patternView.nextPattern();
                    contButton.setTextColor(Color.GRAY);
                    contButton.setEnabled(false);
                    contEnabled = false;
                    dtv.setText("Distance: " + String.valueOf(pattern.getDistance()));
                    atv.setText("Angle: " + String.valueOf(pattern.getAngle()) + (char) 0x00B0);
                    DecimalFormat precision = new DecimalFormat("#.##");
                    if (showPower) {
                        Double i2 = Double.valueOf(precision.format(pattern.getPowerValue()));
                        tv.setText("Power: " + String.valueOf(i2));
                    }

                    Log.d(TAG, String.valueOf(pattern.getDistance()));
                    prevStopValue = maxVal;
                    // currStopValue = maxVal;
                    alignSeekBar.setProgress(maxVal);

                    // Set the page title
                    if (patternIndex == 2)
                        if (SingletonDataHolder.lang.equals("zh"))
                            str = "快速测试 " + Integer.toString((patternIndex + 2) % 3) + "/3";
                        else
                            str = "Practice " + Integer.toString((patternIndex + 2) % 3) + "/3";
                    else if (SingletonDataHolder.lang.equals("zh"))
                        str = "快速测试 " + Integer.toString(patternIndex + 2) + "/3";
                    else
                        str = "Practice " + Integer.toString(patternIndex + 2) + "/3";
                    inTestTv.setText(str);

                    /*** v1.4
                    if (pattern.isAllPatternComplete()
                            && ((deviceId < 2 && patternIndex == 5)
                            || (deviceId >= 2 && SingletonDataHolder.testMode == 0 && patternIndex == 8)
                            || (deviceId >= 2 && SingletonDataHolder.testMode == 1 && patternIndex == 2)))
                     ***/
                    if (deviceId >= 2 && SingletonDataHolder.testMode == 1 && patternIndex == 2)
                        if (!SingletonDataHolder.noDevice) {
                            finish();
                        }
                        /*** v1.4
                         if (SingletonDataHolder.testMode == 1)
                         calcResultPlayMode();
                         else
                         calcResult();
                         ***/
                        else {
                            /*** v1.4
                             if (pattern.getWhichEye()) {
                             eyeRightText.setTextColor(brightColor);
                             eyeLeftText.setTextColor(darkColor);
                             eyeImageView.setImageResource(R.drawable.eye_right1);
                             } else {
                             eyeRightText.setTextColor(darkColor);
                             eyeLeftText.setTextColor(brightColor);
                             eyeImageView.setImageResource(R.drawable.eye_left1);
                             }
                             ***/
                            if (SingletonDataHolder.testMode == 0)
                                if (patternIndex == 8)
                                    if (SingletonDataHolder.lang.equals("zh"))
                                        str = "测试" + Integer.toString((patternIndex + 2) % 9) + "/9";
                                    else
                                        str = "Test " + Integer.toString((patternIndex + 2) % 9) + "/9";
                                else if (SingletonDataHolder.lang.equals("zh"))
                                    str = "测试 " + Integer.toString(patternIndex + 2) + "/9";
                                else
                                    str = "Test " + Integer.toString(patternIndex + 2) + "/9";
                            if (SingletonDataHolder.testMode == 1)
                                if (patternIndex == 2)
                                    if (SingletonDataHolder.lang.equals("zh"))
                                        str = "快速测试 " + Integer.toString((patternIndex + 2) % 3) + "/3";
                                    else
                                        str = "Practice " + Integer.toString((patternIndex + 2) % 3) + "/3";
                                else if (SingletonDataHolder.lang.equals("zh"))
                                    str = "快速测试 " + Integer.toString(patternIndex + 2) + "/3";
                                else
                                    str = "Practice " + Integer.toString(patternIndex + 2) + "/3";
                            inTestTv.setText(str);
                        }
                } else
                    Toast.makeText(PracticeActivity.this, "Overlap the red and green lines completely before moving to the next practice", Toast.LENGTH_SHORT).show();
            }
        });

        final Button closerButton = (Button) findViewById(R.id.closerButton);
        closerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Closer Button clicked.");
                // int pressedColor = Color.rgb(255,255,255);
                // closerButton.setTextColor(pressedColor);
                int lineSpace = pattern.getDistance();
                if (Math.abs(lineSpace) > allowedOffset) {
                    contButton.setTextColor(Color.GRAY);
                    contButton.setEnabled(false);
                    contEnabled = false;
                } else {
                    contButton.setTextColor(Color.WHITE);
                    contButton.setEnabled(true);
                    contEnabled = true;
                }
                if (lineSpace >= minVal + 1) {
                    patternView.closerDraw(1);
                    dtv.setText("Distance: " + String.valueOf(pattern.getDistance()));
                    DecimalFormat precision = new DecimalFormat("#.##");
                    if (showPower & false) {
                        Double i2 = Double.valueOf(precision.format(pattern.getPowerValue()));
                        tv.setText("Power: " + String.valueOf(i2));
                    }
                    prevStopValue = lineSpace - minVal;
                    alignSeekBar.setProgress(lineSpace - minVal);
                }
            }
        });

        closerButton.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                inLongPressMode = true;
                closerOrFurther = true;
                repeatUpdateHandler.post(new RepetitiveUpdater());
                return true;
            }
        });

        closerButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && inLongPressMode) {
                    inLongPressMode = false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN ||
                        event.getAction() == MotionEvent.ACTION_CANCEL) {
                    int releasedColor = Color.rgb(255, 255, 255);
                    closerButton.setTextColor(releasedColor);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    int pressedColor = Color.rgb(200, 200, 200);
                    closerButton.setTextColor(pressedColor);
                }
                return false;
            }
        });


        final Button furtherButton = (Button) findViewById(R.id.furtherButton);
        furtherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MyActivity", "Further Button clicked.");
                int lineSpace = pattern.getDistance();
                if (Math.abs(lineSpace) > allowedOffset) {
                    contButton.setTextColor(Color.GRAY);
                    contButton.setEnabled(false);
                    contEnabled = false;
                } else {
                    contButton.setTextColor(Color.WHITE);
                    contButton.setEnabled(true);
                    contEnabled = true;
                }
                int range;
                if (SingletonDataHolder.noDevice)
                    range = maxVal;
                else
                    range = minVal + maxVal;
                if (lineSpace <= range - 1) {
                    patternView.furtherDraw(1);
                    dtv.setText("Distance: " + String.valueOf(pattern.getDistance()));
                    DecimalFormat precision = new DecimalFormat("#.##");
                    for (int i = 0; i < 4; i++)
                        Log.i("*** MAIN2 STEP ***", SingletonDataHolder.sphericalStep.get(i));
                    if (showPower & false) {
                        Double i2 = Double.valueOf(precision.format(pattern.getPowerValue()));
                        tv.setText("Power: " + String.valueOf(i2));
                    }
                    prevStopValue = lineSpace - minVal;
                    alignSeekBar.setProgress(lineSpace - minVal);
                }
            }
        });

        furtherButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                inLongPressMode = true;
                closerOrFurther = false;
                repeatUpdateHandler.post(new RepetitiveUpdater());
                return true;
            }
        });

        furtherButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && inLongPressMode) {
                    inLongPressMode = false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN ||
                        event.getAction() == MotionEvent.ACTION_CANCEL) {
                    int releasedColor = Color.rgb(255, 255, 255);
                    furtherButton.setTextColor(releasedColor);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    int pressedColor = Color.rgb(200, 200, 200);
                    furtherButton.setTextColor(pressedColor);
                }
                return false;
            }
        });

        // alignSeekBar.setOnSeekBarChangeListener(this);
        alignSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub;
                if (progress < prevStopValue)
                    patternView.closerDraw(prevStopValue - progress);
                else
                    patternView.furtherDraw(progress - prevStopValue);
                dtv.setText("Distance: " + String.valueOf(pattern.getDistance()));
                DecimalFormat precision = new DecimalFormat("#.##");

                if (showPower) {
                    Double i2 = Double.valueOf(precision.format(pattern.getPowerValue()));
                    tv.setText("Power: " + String.valueOf(i2));
                }
                prevStopValue = progress;
            }
        });
    }

    /**
     * Closer long pressed action.
     */
    public void closerLongPressedAction() {
        // final PatternView patternView = (PatternView) findViewById(R.id.drawView);
        final TextView tv = (TextView) findViewById(R.id.powerText);
        final Pattern pattern = patternView.getPatternInstance();
        final TextView dtv = (TextView) findViewById(R.id.distText);
        final SeekBar alignSeekBar = (SeekBar) findViewById(R.id.alignSeekBar);

        int lineSpace = pattern.getDistance();
        if (Math.abs(lineSpace) > allowedOffset) {
            contButton.setTextColor(Color.GRAY);
            contButton.setEnabled(false);
            contEnabled = false;
        } else {
            contButton.setTextColor(Color.WHITE);
            contButton.setEnabled(true);
            contEnabled = true;
        }
        if (lineSpace >=minVal+6) {
            patternView.closerDraw(longPressStep);
            dtv.setText("Distance: " + String.valueOf(pattern.getDistance()));
            DecimalFormat precision = new DecimalFormat("#.##");
            if (showPower & false) {
                Double i2 = Double.valueOf(precision.format(pattern.getPowerValue()));
                tv.setText("Power: " + String.valueOf(i2));
            }
            prevStopValue = pattern.getDistance() - minVal;
            alignSeekBar.setProgress(pattern.getDistance() - minVal);
        }
    }

    /**
     * Further long pressed action.
     */
    public void furtherLongPressedAction() {
        // final PatternView patternView = (PatternView) findViewById(R.id.drawView);
        final TextView tv = (TextView) findViewById(R.id.powerText);
        final Pattern pattern = patternView.getPatternInstance();
        final TextView dtv = (TextView) findViewById(R.id.distText);
        final SeekBar alignSeekBar = (SeekBar) findViewById(R.id.alignSeekBar);
        int range;
        if (SingletonDataHolder.noDevice)
            range = maxVal;
        else
            range = minVal + maxVal;
        int lineSpace = pattern.getDistance();
        if (Math.abs(lineSpace) > allowedOffset) {
            contButton.setTextColor(Color.GRAY);
            contButton.setEnabled(false);
            contEnabled = false;
        } else {
            contButton.setTextColor(Color.WHITE);
            contButton.setEnabled(true);
            contEnabled = true;
        }
        if (lineSpace <= range-2) {
            patternView.furtherDraw(longPressStep);
            dtv.setText("Distance: " + String.valueOf(pattern.getDistance()));
            DecimalFormat precision = new DecimalFormat("#.##");
            if (showPower & false) {
                Double i2 = Double.valueOf(precision.format(pattern.getPowerValue()));
                tv.setText("Power: " + String.valueOf(i2));
            }
            prevStopValue = pattern.getDistance() - minVal;
            alignSeekBar.setProgress(pattern.getDistance() - minVal);
        }
    }

    @Override
    public void onBackPressed() {}

}
