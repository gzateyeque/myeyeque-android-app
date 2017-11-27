package com.eyeque.eyeque;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

/**
 *
 * File:            PracticeGenericAcvitity.java
 * Description:     The practice generic page
 * Created:         2017/10/07
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */

public class PracticeGenericActivity extends Activity {

    private PatternView patternView;
    private Pattern pattern;
    private Button exitButton;
    private static int subjectId;
    private static int deviceId;
    private static int serverId;
    private static MediaPlayer mp;

    // Tag for log message
    private static final String TAG = PracticeGenericActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_practice_generic);

        subjectId = getIntent().getIntExtra("subjectId", 0);
        deviceId = getIntent().getIntExtra("deviceId", 0);
        serverId = getIntent().getIntExtra("serverId", 0);

        final SeekBar alignSeekBar = (SeekBar) findViewById(R.id.alignSeekBar);
        alignSeekBar.setVisibility(View.INVISIBLE);

        final TextView practiceHeaderTextView = (TextView) findViewById(R.id.practiceHeaderTextView);
        if (SingletonDataHolder.practiceGenericPageNum == 1)
            practiceHeaderTextView.setText("Practice");
        else {
            practiceHeaderTextView.setText("Practice Complete!");
        }

        Button closeButton = (Button) findViewById(R.id.closerButton);
        Button furtherButton = (Button) findViewById(R.id.furtherButton);
        exitButton = (Button) findViewById(R.id.exitButton);
        closeButton.setVisibility(View.INVISIBLE);
        furtherButton.setVisibility(View.INVISIBLE);

        TextView practiceDescTv = (TextView) findViewById(R.id.practiceDescText);
        if (SingletonDataHolder.practiceGenericPageNum == 1) {
            SingletonDataHolder.accommodationOn = true;
            patternView = (PatternView) findViewById(R.id.drawView);
            pattern = patternView.getPatternInstance();
            patternView.setDeviceId((int) deviceId);
            patternView.start();
            practiceDescTv.setText(R.string.practiceCheckLineText);
        }

        mp = new MediaPlayer();
        if (mp.isPlaying()) {
            mp.stop();
        }
        try {

            if (SingletonDataHolder.practiceGenericPageNum == 1) {
                mp.reset();
                mp.setDataSource(getApplicationContext(),
                        Uri.parse("android.resource://com.eyeque.eyeque/" + R.raw.practice_see_lines_1));
                mp.prepare();
                mp.start();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button continueButton = (Button) findViewById(R.id.practiceContinueButton);
        if (SingletonDataHolder.practiceGenericPageNum == 1)
            continueButton.setText("Next");
        else
            continueButton.setText("Continue");
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (SingletonDataHolder.practiceGenericPageNum) {
                    case 1:
                        SingletonDataHolder.testMode = 1;
                        SingletonDataHolder.noDevice = false;
                        SingletonDataHolder.accommodationOn = true;
                        Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
                        mainIntent.putExtra("deviceId", 3);
                        startActivity(mainIntent);
                        if (mp.isPlaying()) {
                            mp.stop();
                        }
                        finish();
                        break;
                    case 2:
                        // Toast.makeText(PlayModeResultActivity.this, "Record Discarded", Toast.LENGTH_SHORT).show();
                        // Intent topIntent = new Intent(getBaseContext(), TopActivity.class);
                        // startActivity(topIntent);
                        if (mp.isPlaying()) {
                            mp.stop();
                        }
                        finish();
                        break;
                    default:
                        break;
                }
                finish();
            }
        });

        if (SingletonDataHolder.practiceGenericPageNum == 1) {
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                            PracticeGenericActivity.this);

                    // Setting Dialog Title
                    if (SingletonDataHolder.lang.equals("zh")) {
                        alertDialog2.setTitle("确认退出...");
                        // Setting Dialog Message
                        alertDialog2.setMessage("你真的要退出测试吗?");
                        // Setting Positive "Yes" Btn
                        alertDialog2.setPositiveButton("是",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Intent topIntent = new Intent(getBaseContext(), TopActivity.class);
                                        // startActivity(topIntent);
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
                    } else {
                        alertDialog2.setTitle("Confirm Exit...");
                        // Setting Dialog Message
                        alertDialog2.setMessage("Are you sure you want to quit this practice session?");
                        // Setting Positive "Yes" Btn
                        alertDialog2.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Intent topIntent = new Intent(getBaseContext(), TopActivity.class);
                                        // startActivity(topIntent);
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
        } else
            exitButton.setVisibility(View.INVISIBLE);
    }

}
