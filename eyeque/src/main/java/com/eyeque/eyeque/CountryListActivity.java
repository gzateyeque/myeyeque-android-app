package com.eyeque.eyeque;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 *
 * File:            CountryListActivity.java
 * Description:     This class gives a list of countries so user can select the country
 *                  and region manuualy
 *                  by the app
 * Created:         2016/07/10
 * Author:          George Zhao
 *
 * Copyright (c) 2017 EyeQue Corp
 */
public class CountryListActivity extends Activity {
    // Defining a string adapter which will handle the data of the ListView
    ArrayAdapter<String> adapter;

    // Record the number of clicks
    int clickCounter = 0;
    String[] items;

    // Tag for log message
    private static final String TAG = CountryListActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_device);

        final ListView deviceListView = (ListView) this.findViewById(R.id.deviceListView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                SingletonDataHolder.countryListItems);
        deviceListView.setAdapter(adapter);

        SingletonDataHolder.country_selected = "";
        deviceListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SingletonDataHolder.country_selected = ((TextView) view).getText().toString();
            finish();
            }
        });
    }
}
