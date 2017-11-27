package com.eyeque.eyeque;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;
import com.eyeque.eyeque.R;

/**
 *
 * File:            DGenderActivity.java
 * Description:     The page let user to select their gender as part of account onboarding
 *                  process
 * Created:         2016/07/12
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class GenderActivity extends AppCompatActivity {

    boolean maleYesChecked = false;
    boolean femaleYesChecked = false;
    boolean notSetYesChecked = false;
    CheckBox maleCheckBox;
    CheckBox femaleCheckBox;
    CheckBox notSetCheckBox;

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
        setContentView(R.layout.activity_gender);

        maleCheckBox = (CheckBox) findViewById(R.id.maleCheckbox);
        femaleCheckBox = (CheckBox) findViewById(R.id.femaleCheckbox);
        notSetCheckBox = (CheckBox) findViewById(R.id.notSetCheckbox);

        Button nextButton = (Button) findViewById(R.id.genderNextButton);
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!maleYesChecked && !femaleYesChecked && !notSetYesChecked)
                    Toast.makeText(GenderActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                else {
                    if (maleYesChecked)
                        SingletonDataHolder.gender = 1;
                    else if (femaleYesChecked)
                        SingletonDataHolder.gender = 2;
                    else
                        SingletonDataHolder.gender = 0;
                    Intent dobIntent = new Intent(getBaseContext(), DobActivity.class);
                    startActivity(dobIntent);
                }
            }
        });
    }

    public void maleYesClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            maleYesChecked = true;
            femaleYesChecked = false;
            notSetYesChecked = false;
            femaleCheckBox.setChecked(false);
            femaleCheckBox.setSelected(false);
            notSetCheckBox.setChecked(false);
            notSetCheckBox.setSelected(false);
        }
        else
            maleYesChecked = false;
    }

    public void femaleYesClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            femaleYesChecked = true;
            maleYesChecked = false;
            notSetYesChecked = false;
            maleCheckBox.setChecked(false);
            maleCheckBox.setSelected(false);
            notSetCheckBox.setChecked(false);
            notSetCheckBox.setSelected(false);
        }
        else
            femaleYesChecked = false;
    }

    public void notSetYesClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            maleYesChecked = false;
            maleYesChecked = false;
            notSetYesChecked = true;
            maleCheckBox.setChecked(false);
            maleCheckBox.setSelected(false);
            femaleCheckBox.setChecked(false);
            femaleCheckBox.setSelected(false);
        }
        else
            notSetYesChecked = false;
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
