package com.eyeque.eyeque;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.SingleLineTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.eyeque.eyeque.R;

import static com.eyeque.eyeque.R.id.readingGlassNoCheckbox;
import static com.eyeque.eyeque.R.id.readingGlassYesCheckbox;
import static com.eyeque.eyeque.R.id.uploadPrescriptionNoCheckbox;
import static com.eyeque.eyeque.R.id.uploadPrescriptionYesCheckbox;
import static com.eyeque.eyeque.R.id.wearEyeglassNoCheckbox;
import static com.eyeque.eyeque.R.id.wearEyeglassYesCheckbox;

/**
 *
 * File:            WearEyeGlassActivity.java
 * Description:     The page let user to input whether they wear glasses or contact. The input
 *                  will be saved into user's profile
 *                  process
 * Created:         2016/07/12
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class WearEyeglassActivity extends AppCompatActivity {
    private CheckBox wearEyeglassYesCheckBox;
    private CheckBox waerEyeglassNoCheckBox;
    private boolean wearEyeglassYesChecked = false;
    private boolean wearEyeglassNoChecked = false;

    private CheckBox readingGlassesYesCheckBox;
    private CheckBox readingGlassesNoCheckBox;
    private boolean readingGlassesYesChecked = false;
    private boolean readingGlassesNoChecked = false;

    private CheckBox uploadPrescriptionYesCheckBox;
    private CheckBox uploadPrescriptionNoCheckBox;
    private boolean uploadPrescriptionYesChecked = false;
    private boolean uploadPrescriptionNoChecked = false;

    private EditText readingGlassesValueEt;

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
        setContentView(R.layout.activity_wear_eyeglass);

        wearEyeglassYesCheckBox = (CheckBox) findViewById(wearEyeglassYesCheckbox);
        waerEyeglassNoCheckBox = (CheckBox) findViewById(wearEyeglassNoCheckbox);

        readingGlassesYesCheckBox = (CheckBox) findViewById(readingGlassYesCheckbox);
        readingGlassesNoCheckBox = (CheckBox) findViewById(readingGlassNoCheckbox);

        uploadPrescriptionYesCheckBox = (CheckBox) findViewById(uploadPrescriptionYesCheckbox);
        uploadPrescriptionNoCheckBox = (CheckBox) findViewById(uploadPrescriptionNoCheckbox);

        readingGlassesValueEt = (EditText) findViewById(R.id.readingGlassValueEditText);
        readingGlassesValueEt.setText(SingletonDataHolder.profileReadingGlassesValue);
        SingletonDataHolder.profileReadingGlassesValue_selected = "";
        readingGlassesValueEt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nvaddListIntent = new Intent(getBaseContext(), NvaddListActivity.class);
                startActivity(nvaddListIntent);
            }
        });
        Button nextButton = (Button) findViewById(R.id.wearEyeglassNextButton);
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!wearEyeglassYesChecked && !wearEyeglassNoChecked)
                    || (!readingGlassesYesChecked && !readingGlassesNoChecked)
                    || (!uploadPrescriptionYesChecked && !uploadPrescriptionNoChecked))
                    Toast.makeText(WearEyeglassActivity.this, "Please provide the answer", Toast.LENGTH_SHORT).show();
                else {
                    if (wearEyeglassYesChecked)
                        SingletonDataHolder.profileWearEyeglass = true;
                    else
                        SingletonDataHolder.profileWearEyeglass = false;
                    if (readingGlassesYesChecked)
                        SingletonDataHolder.profileWearReadingGlasses = true;
                    else
                        SingletonDataHolder.profileWearReadingGlasses = false;
                    if (uploadPrescriptionYesChecked)
                        SingletonDataHolder.profileUploadPrescription = true;
                    else
                        SingletonDataHolder.profileUploadPrescription = false;
                    Intent countryIntent = new Intent(getBaseContext(), CountryActivity.class);
                    startActivity(countryIntent);
                }
            }
        });
    }

    public void wearEyeglassYesClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            wearEyeglassYesChecked = true;
            wearEyeglassNoChecked = false;
            waerEyeglassNoCheckBox.setChecked(false);
            waerEyeglassNoCheckBox.setSelected(false);
        } else
            wearEyeglassYesChecked = false;
    }

    public void wearEyeglassNoClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            wearEyeglassYesChecked = false;
            wearEyeglassNoChecked = true;
            wearEyeglassYesCheckBox.setChecked(false);
            wearEyeglassYesCheckBox.setSelected(false);
        } else
            wearEyeglassNoChecked = false;
    }

    public void readingGlassesYesClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            readingGlassesYesChecked = true;
            readingGlassesNoChecked = false;
            readingGlassesNoCheckBox.setChecked(false);
            readingGlassesNoCheckBox.setSelected(false);
        } else
            readingGlassesYesChecked = false;
    }

    public void readingGlassesNoClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            readingGlassesYesChecked = false;
            readingGlassesNoChecked = true;
            readingGlassesYesCheckBox.setChecked(false);
            readingGlassesYesCheckBox.setSelected(false);
        } else
            readingGlassesNoChecked = false;
    }

    public void uploadPrescriptionYesClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            uploadPrescriptionYesChecked = true;
            uploadPrescriptionNoChecked = false;
            uploadPrescriptionNoCheckBox.setChecked(false);
            uploadPrescriptionNoCheckBox.setSelected(false);
        } else
            uploadPrescriptionYesChecked = false;
    }

    public void uploadPrescriptionNoClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            uploadPrescriptionYesChecked = false;
            uploadPrescriptionNoChecked = true;
            uploadPrescriptionYesCheckBox.setChecked(false);
            uploadPrescriptionYesCheckBox.setSelected(false);
        } else
            uploadPrescriptionNoChecked = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!SingletonDataHolder.profileReadingGlassesValue_selected.matches(""))
            SingletonDataHolder.profileReadingGlassesValue = SingletonDataHolder.profileReadingGlassesValue_selected;
        readingGlassesValueEt.setText(SingletonDataHolder.profileReadingGlassesValue);

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
