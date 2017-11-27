package com.eyeque.eyeque;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.net.Uri;
import android.widget.TextView;
import android.widget.ImageView;

/**
 *
 * File:            AboutActivity.java
 * Description:     This displays the app version number and copyright information.
 *                  User can tap the rate app to give review in the app store. The app's
 *                  term of user and private policy are also shown in this page
 * Created:         2016/05/05
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView rateAppTv = (TextView) findViewById(R.id.rateAppTextStringView);
        TextView verStringTv = (TextView) findViewById(R.id.verStringTextView);
        verStringTv.setText("Version " + Constants.BuildNumber);
        ImageView likeUsOnFacebook = (ImageView) findViewById(R.id.likeOnFacebook);

        /**
         * Connect to the app page at Google Play store
         */
        rateAppTv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri uri =  Uri.parse(Constants.UrlRateApp);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        likeUsOnFacebook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri uri =  Uri.parse(Constants.UrlLikeUsOnFacebook);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        /**
         * WebView to show the Term of Use
         */
        TextView termsOfServiceTv = (TextView) findViewById(R.id.termsOfServiceButton);
        termsOfServiceTv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri uri = Uri.parse(Constants.UrlTermsOfService);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        /**
         * WebView to show the Private Policy
         */
        TextView privatePolicyTv = (TextView) findViewById(R.id.privatePolicyButton);
        privatePolicyTv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri uri = Uri.parse(Constants.UrlPrivacyPolicy);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    /**
     *
     * @param keyCode
     * @param event
     * @return true
     *
     * Disable the volume buttons
     */
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
