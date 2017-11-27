package com.eyeque.eyeque;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.eyeque.eyeque.R;

import java.util.ArrayList;
/**
 *
 * File:            DeviceActivity.java
 * Description:     This classs gives a list of supported device
 * Created:         2016/03/26
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */

public class DeviceActivity extends Activity {
    /**
     * The List items.
     */

    ArrayList<String> listItems = new ArrayList<String>();

    /**
     * The Adapter.
     */

    ArrayAdapter<String> adapter;

    /**
     * The Click counter.
     */
    int clickCounter = 0;
    /**
     * The Items.
     */
    String[] items;

    // Tag for log message
    private static final String TAG = DeviceActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_device);

        final ListView deviceListView = (ListView) this.findViewById(R.id.deviceListView);
        listItems.add("#1");
        listItems.add("#2");
        listItems.add("#3");
        listItems.add("#5");
        listItems.add("#6");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        deviceListView.setAdapter(adapter);

        deviceListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView) view).getText().toString();
                // Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                Intent result = new Intent();
                result.putExtra("index", new Integer(position).toString());
                result.putExtra("result", item);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    /**
     * Add items.
     *
     * @param v the v
     */
// Method to handle the dynamic insertion
    public void addItems(View v) {
        listItems.add("Clicked : " + clickCounter++);
        adapter.notifyDataSetChanged();
    }
}
