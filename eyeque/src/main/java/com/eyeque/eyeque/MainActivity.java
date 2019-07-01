package com.eyeque.eyeque;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.provider.Settings;
import android.content.ContentResolver;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
 * File:            MainActivity.java
 * Description:     Main screen display image pattern, one green line and one red line
 *                  User can tap the + or - button to move the lines closer or further until
 *                  they are overlapped. The quick test and full test will go through 3 or 9
 *                  medians for each eye respectively. User can quit the test
 * Created:         2016/04/19
 * Author:          George Zhao
 *
 * Copyright (c) 2017 EyeQue Corp
 */

public class MainActivity extends Activity {

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
    private boolean onOff = true;
    private RelativeLayout background;
    private static boolean showPower = false;

    // Tag for log message
    private static final String TAG = MainActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_main);

        /***
        //Get the content resolver
        cResolver = getContentResolver();
        //Set the system brightness using the brightness variable value
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 255);
         ***/

        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        win.setAttributes(winParams);

        // Dynamically set the box center position based om the screen size
        SingletonDataHolder.phoneDisplay = android.os.Build.DISPLAY;
        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int width = size. x;
        int height = size. y;
        SingletonDataHolder.centerX = width / 2;
        // SingletonDataHolder.centerY = Math.round((float) SingletonDataHolder.centerY * (float) SingletonDataHolder.phonePpi / 520.0f);

        background = (RelativeLayout) findViewById(R.id.mainActivity);

        subjectId = getIntent().getIntExtra("subjectId", 0);
        deviceId = getIntent().getIntExtra("deviceId", 0);
        serverId = getIntent().getIntExtra("serverId", 0);

        switch (deviceId) {
            case 2:
                minVal = Constants.MINVAL_DEVICE_3;
                maxVal = Constants.MAXVAL_DEVICE_3;
                // longPressStep = 5;
                break;
            case 3:
                // minVal = Constants.MINVAL_DEVICE_5;
                // maxVal = Constants.MAXVAL_DEVICE_5;
                minVal = SingletonDataHolder.minDistance;
                maxVal = SingletonDataHolder.maxDistance - SingletonDataHolder.minDistance;
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

        final TextView eyeLeftText = (TextView) findViewById(R.id.eyeLeftText);
        final TextView eyeRightText = (TextView) findViewById(R.id.eyeRightText);
        final ImageView eyeImageView = (ImageView) findViewById(R.id.eyeImage);

        final SeekBar alignSeekBar = (SeekBar) findViewById(R.id.alignSeekBar);
        alignSeekBar.setMax(maxVal);
        prevStopValue = maxVal;
        alignSeekBar.setProgress(maxVal);
        alignSeekBar.setVisibility(View.INVISIBLE);

        accomodationSwitch = (Switch) findViewById(R.id.accormSwitch);
        accomodationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // do something when checked is selected
                    SingletonDataHolder.accommodationOn = true;
                } else {
                    SingletonDataHolder.accommodationOn = false;
                    //do something when unchecked
                }
                patternView.reDraw();
            }
        });

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
                        MainActivity.this);

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
                    if (SingletonDataHolder.testMode == 1)
                        alertDialog2.setMessage("Are you sure you want to quit this practice session?");
                    else
                        alertDialog2.setMessage("Are you sure you want exit this test?");
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

        if (pattern.getWhichEye()) {
            eyeRightText.setTextColor(brightColor);
            eyeLeftText.setTextColor(darkColor);
            eyeImageView.setImageResource(R.drawable.eye_right1);
        } else {
            eyeRightText.setTextColor(darkColor);
            eyeLeftText.setTextColor(brightColor);
            eyeImageView.setImageResource(R.drawable.eye_left1);
        }
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
        contButton.setOnClickListener(new View.OnClickListener() {
            String str;

            @Override
            public void onClick(View v) {
                Log.i(TAG, "Next Button clicked.");

                int patternIndex = patternView.getPatternInstance().getPattenIndex();
                if (pattern.isAllPatternComplete() && patternIndex == 0) {
                    if (SingletonDataHolder.testMode == 1) {
                        // calcResultPlayMode();
                        SingletonDataHolder.practiceGenericPageNum = 2;
                        Intent practiceComplete = new Intent(getBaseContext(), PracticeGenericActivity.class);
                        startActivity(practiceComplete);
                        finish();
                    }
                    else
                        calcResult();
                    return;
                }

                if (prevStopValue == maxVal)
                    return;

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
                                        Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.practice_finish));
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
                            if (pattern.getWhichEye()) {
                                mp.setDataSource(getApplicationContext(),
                                        Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.fr_last));

                                // Flash the screen
                                new CountDownTimer(5000, 500) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        if (onOff) {
                                            background.setBackgroundColor(Color.GRAY);
                                            onOff = false;
                                        } else {
                                            background.setBackgroundColor(Color.BLACK);
                                            onOff = true;
                                            // do something after 1s
                                        }
                                    }
                                    @Override
                                    public void onFinish () {
                                        background.setBackgroundColor(Color.BLACK);
                                    }
                                }.start();

                            }
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

                    /***
                    if ((deviceId < 2 && patternIndex == 5)
                            || (deviceId >= 2 && patternIndex == 8 && SingletonDataHolder.testMode == 0)
                            || (deviceId >= 2 && patternIndex == 2 && SingletonDataHolder.testMode == 1)) {
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
                     ***/

                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                patternView.nextPattern();
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

                if (pattern.isAllPatternComplete()
                        && ((deviceId < 2 && patternIndex == 5)
                        || (deviceId >= 2 && SingletonDataHolder.testMode == 0 && patternIndex == 8)
                        || (deviceId >= 2 && SingletonDataHolder.testMode == 1 && patternIndex == 2)))
                    if (SingletonDataHolder.testMode == 1) {
                        SingletonDataHolder.practiceGenericPageNum = 2;
                        Intent practiceComplete = new Intent(getBaseContext(), PracticeGenericActivity.class);
                        startActivity(practiceComplete);
                        finish();
                        // calcResultPlayMode();
                    }
                    else
                        calcResult();
                else {
                    if (pattern.getWhichEye()) {
                        eyeRightText.setTextColor(brightColor);
                        eyeLeftText.setTextColor(darkColor);
                        eyeImageView.setImageResource(R.drawable.eye_right1);
                    } else {
                        eyeRightText.setTextColor(darkColor);
                        eyeLeftText.setTextColor(brightColor);
                        eyeImageView.setImageResource(R.drawable.eye_left1);
                    }
                    if (SingletonDataHolder.testMode == 0)
                        if (patternIndex == 8)
                            if (SingletonDataHolder.lang.equals("zh"))
                                str = "测试" + Integer.toString((patternIndex + 2) % 9) + "/9";
                            else
                                str = "Test " + Integer.toString((patternIndex + 2) % 9) + "/9";
                        else
                            if (SingletonDataHolder.lang.equals("zh"))
                                str = "测试 " + Integer.toString(patternIndex + 2) + "/9";
                            else
                                str = "Test " + Integer.toString(patternIndex + 2) + "/9";
                    if (SingletonDataHolder.testMode == 1)
                        if (patternIndex == 2)
                            if (SingletonDataHolder.lang.equals("zh"))
                                str = "快速测试 " + Integer.toString((patternIndex + 2) % 3) + "/3";
                            else
                                str = "Practice " + Integer.toString((patternIndex + 2) % 3) + "/3";
                        else
                            if (SingletonDataHolder.lang.equals("zh"))
                                str = "快速测试 " + Integer.toString(patternIndex + 2) + "/3";
                            else
                                str = "Practice " + Integer.toString(patternIndex + 2) + "/3";
                    inTestTv.setText(str);
                }
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
                if (lineSpace <= minVal + maxVal - 1) {
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
     * Calc result.
     */
    public void calcResult() {

        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {
            Toast.makeText(MainActivity.this,
                    "Processing measurement data", Toast.LENGTH_LONG).show();
            contButton.setEnabled(false);
            exitButton.setEnabled(false);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            final String url = Constants.UrlUploadTest;

            final JSONObject params = new JSONObject();
            final JSONObject finalParam = new JSONObject();
            try {
                params.put("binoOp", "No binocular");
                params.put("testType", "Full Refraction");
                if (deviceId == 2)
                    params.put("deviceName", "Device 3");
                else if (deviceId == 3)
                    // params.put("deviceName", "Device5");
                    params.put("deviceName", SingletonDataHolder.deviceName);
                else
                    params.put("deviceName", "Device 1");
                params.put("phoneType", SingletonDataHolder.phoneType);
                params.put("accomPattern", "AP5G");
                params.put("screenProtect", SingletonDataHolder.screenProtect);
                params.put("wearGlasses", SingletonDataHolder.wearGlasses);
                params.put("subjectID", SingletonDataHolder.userId);
                params.put("lineLength", SingletonDataHolder.lineLength);
                params.put("lineWidth", SingletonDataHolder.lineWidth);

                // if (serverId > 0) {
                JSONArray mDataArr = new JSONArray();
                for (int i = 0; i < 9; i++) {
                    JSONObject mDataObj = new JSONObject();
                    mDataObj.put("subjectID", SingletonDataHolder.userId);
                    mDataObj.put("angle", pattern.getPatternCalcAngleList()[i]);
                    mDataObj.put("distance", pattern.getRightDistValueList()[i]);
                    mDataObj.put("rightEye", 1);
                    mDataObj.put("power", 0);
                    mDataObj.put("duration", pattern.rightDurationList[i]);
                    mDataArr.put(mDataObj);
                }
                for (int i = 0; i < 9; i++) {
                    JSONObject mDataObj = new JSONObject();
                    mDataObj.put("subjectID", SingletonDataHolder.userId);
                    mDataObj.put("angle", pattern.getPatternCalcAngleList()[i]);
                    mDataObj.put("distance", pattern.getLeftDistValueList()[i]);
                    mDataObj.put("rightEye", 0);
                    mDataObj.put("power", 0);
                    mDataObj.put("duration", pattern.leftDurationList[i]);
                    mDataArr.put(mDataObj);
                }
                params.put("measures", mDataArr);
                finalParam.put("testdata", params);
                // }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.i("*** FT JSON Rep ***", response);

                    Intent resultIntent = new Intent(getBaseContext(), ResultActivity.class);
                    try {
                        final JSONObject result = new JSONObject(response);
                        resultIntent.putExtra("TestId", result.getInt("test_condition_id"));
                        resultIntent.putExtra("Score", result.getInt("score"));
                        if (Double.parseDouble(result.getString("sphe_od")) > 0)
                            resultIntent.putExtra("ODSPH", String.format("+%.2f", Double.parseDouble(result.getString("sph_od"))));
                        else
                            resultIntent.putExtra("ODSPH", String.format("%.2f", Double.parseDouble(result.getString("sph_od"))));
                        resultIntent.putExtra("ODCYL", result.getString("level_od"));
                        if (Double.parseDouble(result.getString("sphe_os")) > 0)
                            resultIntent.putExtra("OSSPH", String.format("+%.2f", Double.parseDouble(result.getString("sph_os"))));
                        else
                            resultIntent.putExtra("OSSPH", String.format("%.2f", Double.parseDouble(result.getString("sph_os"))));
                        resultIntent.putExtra("OSCYL", result.getString("level_os"));

                        /**** For video shooting
                         resultIntent.putExtra("ODE", "-2.25");
                         resultIntent.putExtra("OSE", "-2.75");
                         ****/
                        startActivity(resultIntent);
                        finish();
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this,
                                "Operation failed, please try it again", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    contButton.setEnabled(true);
                    exitButton.setEnabled(true);
                    Toast.makeText(MainActivity.this,
                            "Can't connect to the server", Toast.LENGTH_SHORT).show();
                    Log.d("Error.Response", error.toString());
                }
            }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    Log.i("$$$--UPLOAD JSON---$$$", finalParam.toString());
                    return finalParam.toString().getBytes();
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
            Toast.makeText(MainActivity.this,
                    "Please connect to the Internet to continue", Toast.LENGTH_SHORT).show();
    }

    /**
     * Erf double.
     *
     * @param x the x
     * @return the double
     */
    public double erf(double x) {
        double y = x * x;
        double z = x*(21.3853322378+1.72227577039*y+0.316652890658*y*y)/(18.9522572415 + 7.8437457083*y + y*y);
        Log.i("***** XXX X*****", Double.toString(x));
        Log.i("***** XXX ERF*****", Double.toString(z));
        return z;
    }

    /**
     * Erf 2 double.
     *
     * @param z the z
     * @return the double
     */
    public double erf2(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

        // use Horner's method
        double ans = 1 - t * Math.exp( -z*z   -   1.26551223 +
                t * ( 1.00002368 +
                t * ( 0.37409196 +
                t * ( 0.09678418 +
                t * (-0.18628806 +
                t * ( 0.27886807 +
                t * (-1.13520398 +
                t * ( 1.48851587 +
                t * (-0.82215223 +
                t * ( 0.17087277))))))))));
        Log.i("***** XXX X*****", Double.toString(z));

        if (z >= 0) {
            Log.i("***** XXX ERF*****", Double.toString(ans));
            return  ans;
        }
        else  {
            Log.i("***** XXX ERF*****", Double.toString(-ans));
            return -ans;
        }
    }


    /**
     * Calc result play mode.
     */
    public void calcResultPlayMode() {

        double sphOd, sphOs;
        double ageOffsetSphOd, ageOffsetSphOs;
        double offsetSpheOd, offsetSpheOs;
        double a, b, c, A, B, C, p1, p2, p3;

        A = Math.tan(2*SingletonDataHolder.calcAngleList[0]*3.14/180);
        B = Math.tan(2*SingletonDataHolder.calcAngleList[1]*3.14/180);
        C = Math.tan(2*SingletonDataHolder.calcAngleList[2]*3.14/180);
        a = Math.cos(2*SingletonDataHolder.calcAngleList[0]*3.14/180);
        b = Math.cos(2*SingletonDataHolder.calcAngleList[1]*3.14/180);
        c = Math.cos(2*SingletonDataHolder.calcAngleList[2]*3.14/180);
        p1 = pattern.getRightPowerValueList()[0]/a;
        p2 = pattern.getRightPowerValueList()[1]/b;
        p3 = pattern.getRightPowerValueList()[2]/c;
        Log.i("Training SPHE-angle1:  ", Double.toString(SingletonDataHolder.calcAngleList[0]));
        Log.i("Training SPHE-angle2:  ", Double.toString(SingletonDataHolder.calcAngleList[1]));
        Log.i("Training SPHE-angle3:  ", Double.toString(SingletonDataHolder.calcAngleList[2]));
        Log.i("Training SPHE-measur:  ", Double.toString(pattern.getRightPowerValueList()[0]));
        Log.i("Training SPHE-measur:  ", Double.toString(pattern.getRightPowerValueList()[1]));
        Log.i("Training SPHE-measur:  ", Double.toString(pattern.getRightPowerValueList()[2]));
        Log.i("Training SPHE-p1:  ", Double.toString(p1));
        Log.i("Training SPHE-p2:  ", Double.toString(p2));
        Log.i("Training SPHE-p3:  ", Double.toString(p3));
        Log.i("Training SPHE-A:  ", Double.toString(A));
        Log.i("Training SPHE-B:  ", Double.toString(B));
        Log.i("Training SPHE-C:  ", Double.toString(C));
        Log.i("Training SPHE-a:  ", Double.toString(a));
        Log.i("Training SPHE-b:  ", Double.toString(b));
        Log.i("Training SPHE-c:  ", Double.toString(c));

        // Age Diff
        double ageDiff = (double) (Calendar.getInstance().get(Calendar.YEAR) - SingletonDataHolder.birthYear) - Constants.AGE0;

        Log.i("***** XXX ageDiff *****", Double.toString(ageDiff));

        // Spherical Equilalence Calculation
        sphOd = ((p1-p2)/(A-B)-(p1-p3)/(A-C))/((1/a-1/b)/(A-B)-(1/a-1/c)/(A-C));

        offsetSpheOd = -Constants.OFFSET1 * (1.0 + erf2((sphOd + 5.5) / 2.0)) * (erf2(ageDiff / Constants.SPAN) - 1.0) / 4.0
                     + Constants.OFFSET2;
        Log.i("**** AOOD ****", Double.toString(offsetSpheOd));
        /*** Old Algorithm Obosolete in version 1.1.5
        if (sphOd >= -4.0)
            offsetSphe = -Constants.OFFSET1 * (erf2(ageDiff/ Constants.SPAN) - 1.0) / 2.0;
        else
            offsetSphe = Constants.OFFSET2;
        Log.i("***** XXX ageDiff2*****", Double.toString(offsetSphe));
         ***/
        // Apply screen protect offset
        // Apply age and screen protect offset
        if (SingletonDataHolder.screenProtect == 1)
            ageOffsetSphOd = sphOd + offsetSpheOd - 0.25;
        else
            ageOffsetSphOd = sphOd + offsetSpheOd;

        p1 = pattern.getLeftPowerValueList()[0]/a;
        p2 = pattern.getLeftPowerValueList()[1]/b;
        p3 = pattern.getLeftPowerValueList()[2]/c;

        // Spherical Equilalence Calculation
        sphOs = ((p1-p2)/(A-B)-(p1-p3)/(A-C))/((1/a-1/b)/(A-B)-(1/a-1/c)/(A-C));
        /*** Old Algorithm Obosolete in version 1.1.5
        if (sphOs >= -4.0)
            offsetSphe = -Constants.OFFSET1 * (erf2(ageDiff/ Constants.SPAN) - 1.0) / 2.0;
        else
            offsetSphe = Constants.OFFSET2;
         ***/
        offsetSpheOs = -Constants.OFFSET1 * (1.0 + erf2((sphOs + 5.5) / 2.0)) * (erf2(ageDiff / Constants.SPAN) - 1.0) / 4.0
                + Constants.OFFSET2;
        Log.i("**** AOOD ****", Double.toString(offsetSpheOs));
        // Apply age and screen protect offset
        if (SingletonDataHolder.screenProtect == 1)
            ageOffsetSphOs = sphOs + offsetSpheOs - 0.25;
        else
            ageOffsetSphOs = sphOs + offsetSpheOs;

        Log.i("Training SPHE-OD:  ", Double.toString(sphOd));
        Log.i("Training SPHE-OS:  ", Double.toString(sphOs));

        // Upload the data
        NetConnection conn = new NetConnection();
        if (conn.isConnected(getApplicationContext())) {
            Toast.makeText(MainActivity.this,
                    "Upload data...", Toast.LENGTH_LONG).show();
            contButton.setEnabled(false);
            exitButton.setEnabled(false);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            final String url = Constants.UrlUploadTraining;

            String rightDistanceValues = Integer.toString(pattern.getRightDistValueList()[0]) + ","
                    + Integer.toString(pattern.getRightDistValueList()[1]) + ","
                    + Integer.toString(pattern.getRightDistValueList()[2]);
            String leftDistanceValues = Integer.toString(pattern.getLeftDistValueList()[0]) + ","
                    + Integer.toString(pattern.getLeftDistValueList()[1]) + ","
                    + Integer.toString(pattern.getLeftDistValueList()[2]);
            final JSONObject params = new JSONObject();
            final JSONObject finalParam = new JSONObject();
            try {
                params.put("subjectID", SingletonDataHolder.userId);
                if (deviceId == 2)
                    params.put("deviceName", "Device 3");
                else if (deviceId == 3)
                    // params.put("deviceName", "Device5");
                    params.put("deviceName", SingletonDataHolder.deviceName);
                else
                    params.put("deviceName", "Device 1");
                params.put("phoneType", SingletonDataHolder.phoneType);
                if (SingletonDataHolder.accommodationOn)
                    params.put("background", 1);
                else
                    params.put("background", 0);
                params.put("sphOD", Double.toString(sphOd));
                params.put("sphOS", Double.toString(sphOs));
                params.put("measuresOD", rightDistanceValues);
                params.put("measuresOS", leftDistanceValues);
                // params.put("ageOffsetOD", offsetSpheOd);
                // params.put("ageOffsetOS", offsetSpheOs);
                if (SingletonDataHolder.screenProtect == 1)
                    params.put("screenOffset", "0.25");
                else
                    params.put("screenOffset", "0");
                finalParam.put("training", params);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                Intent resultIntent = new Intent(getBaseContext(), ResultActivity.class);
                @Override
                public void onResponse(String response) {
                    Log.i("*** QT JSON Rep ***", response);
                    Intent resultIntent = new Intent(getBaseContext(), PlayModeResultActivity.class);
                    try {
                        final JSONObject result = new JSONObject(response);
                        resultIntent.putExtra("Score", result.getInt("score"));
                        if (Double.parseDouble(result.getString("sph_od")) > 0)
                            resultIntent.putExtra("ODSPH", String.format("+%.2f", Double.parseDouble(result.getString("sph_od"))));
                        else
                            resultIntent.putExtra("ODSPH", String.format("%.2f", Double.parseDouble(result.getString("sph_od"))));
                        resultIntent.putExtra("ODCYL", result.getString("level_od"));
                        if (Double.parseDouble(result.getString("sph_os")) > 0)
                            resultIntent.putExtra("OSSPH", String.format("+%.2f", Double.parseDouble(result.getString("sph_os"))));
                        else
                            resultIntent.putExtra("OSSPH", String.format("%.2f", Double.parseDouble(result.getString("sph_os"))));
                        resultIntent.putExtra("OSCYL", result.getString("level_os"));
                        resultIntent.putExtra("ODCYL_LEVEL", result.getString("level_od"));
                        resultIntent.putExtra("OSCYL_LEVEL", result.getString("level_os"));
                        resultIntent.putExtra("Message", result.getString("msg"));
                        startActivity(resultIntent);
                        finish();
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this,
                                "Operation failed, please try it again", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    contButton.setEnabled(true);
                    exitButton.setEnabled(true);
                    Toast.makeText(MainActivity.this,
                            "Can't connect to the server", Toast.LENGTH_SHORT).show();
                    Log.d("Error.Response", error.toString());
                }
            }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    Log.i("$$$-UPLOAD TRAINING-$$$", finalParam.toString());
                    return finalParam.toString().getBytes();
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
            Toast.makeText(MainActivity.this,
                    "Upload failed", Toast.LENGTH_SHORT).show();

        /*****
        Intent resultIntent = new Intent(getBaseContext(), PlayModeResultActivity.class);
        resultIntent.putExtra("ODE", String.format("%.2f", Math.round(ageOffsetSphOd*4)/4f));
        resultIntent.putExtra("OSE", String.format("%.2f", Math.round(ageOffsetSphOs*4)/4f));
        startActivity(resultIntent);
        finish();
         *****/

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
        if (pattern.getDistance() >=minVal+6) {
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
        if (pattern.getDistance() <=minVal+maxVal-2) {
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


    /****
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // final PatternView patternView = (PatternView) findViewById(R.id.drawView);
        final TextView tv = (TextView) findViewById(R.id.powerText);
        // pattern = patternView.getPatternInstance();
        final TextView dtv = (TextView) findViewById(R.id.distText);
        final SeekBar alignSeekBar = (SeekBar) findViewById(R.id.alignSeekBar);


         switch (keyCode) {
         case KeyEvent.KEYCODE_VOLUME_DOWN:
         Log.d(TAG, "Volume Down Button is pressed");
         if (pattern.getDistance() >=minVal+2) {
         patternView.closerDraw(longPressStep);
         dtv.setText("Distance: " + String.valueOf(pattern.getDistance()));
         DecimalFormat precision = new DecimalFormat("#.##");
         Double i2 = Double.valueOf(precision.format(pattern.getPowerValue()));
         tv.setText("Power: " + String.valueOf(i2));
         prevStopValue = pattern.getDistance() - minVal;
         alignSeekBar.setProgress(pattern.getDistance() - minVal);
         }
         break;
         case KeyEvent.KEYCODE_VOLUME_UP:
         Log.d(TAG, "Volume Up Button is pressed");
         if (pattern.getDistance() <=minVal+maxVal-2) {
         patternView.furtherDraw(longPressStep);
         dtv.setText("Distance: " + String.valueOf(pattern.getDistance()));
         DecimalFormat precision = new DecimalFormat("#.##");
         Double i2 = Double.valueOf(precision.format(pattern.getPowerValue()));
         tv.setText("Power: " + String.valueOf(i2));
         prevStopValue = pattern.getDistance() - minVal;
         alignSeekBar.setProgress(pattern.getDistance() - minVal);
         }
         break;
         default:
         break;
         }

        return true;
    }
    ****/


    @Override
    public void onBackPressed() {}


    /*****************************************
     * DEFAULT Stub function with Menubar
     * ==================================
     *
     @Override
     protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_main);
     Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
     setSupportActionBar(toolbar);

     FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
     fab.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
     Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
     .setAction("Action", null).show();
     }
     });
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
     // Inflate the menu; this adds items to the action bar if it is present.
     getMenuInflater().inflate(R.menu.menu_main, menu);
     return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
     // Handle action bar item clicks here. The action bar will
     // automatically handle clicks on the Home/Up button, so long
     // as you specify a parent activity in AndroidManifest.xml.
     int id = item.getItemId();

     //noinspection SimplifiableIfStatement
     if (id == R.id.action_settings) {
     return true;
     }

     return super.onOptionsItemSelected(item);
     }
     ********/
}
