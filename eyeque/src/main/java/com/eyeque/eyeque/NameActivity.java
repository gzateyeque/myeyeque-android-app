package com.eyeque.eyeque;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.eyeque.eyeque.R;

/**
 *
 * File:            NameActivity.java
 * Description:     The page let user to enter their lastname and firstname year as part of
 *                  account onboarding process
 * Created:         2016/07/12
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class NameActivity extends AppCompatActivity {
    private WebView webview;
    private EditText firstnameEt;
    private EditText lastnameEt;

    // Tag for log message
    private static final String TAG = NameActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_name);

        firstnameEt = (EditText) findViewById(R.id.firstNameEditText);
        firstnameEt.setHint(Html.fromHtml("<small>" + "First Name" + "</small>" ));
        lastnameEt = (EditText) findViewById(R.id.lastNameEditText);
        lastnameEt.setHint(Html.fromHtml("<small>" + "Last Name" + "</small>" ));

        Button nextButton = (Button) findViewById(R.id.nameNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = firstnameEt.getText().toString();
                String lastname = lastnameEt.getText().toString();
                if (firstname.matches(""))
                    Toast.makeText(NameActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                else if (lastname.matches(""))
                    Toast.makeText(NameActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                else {
                    SingletonDataHolder.firstName = firstname;
                    SingletonDataHolder.lastName = lastname;
                    Intent genderIntent = new Intent(getBaseContext(), GenderActivity.class);
                    startActivity(genderIntent);
                }
            }
        });
    }

    /**
     * Is empty string boolean.
     *
     * @param text the text
     * @return the boolean
     */
    public static boolean isEmptyString(String text) {
        return (text == null || text.trim().equals("null") || text.trim()
                .length() <= 0);
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

