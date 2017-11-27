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

import java.util.ArrayList;

/**
 *
 * File:            NvaddListActivity.java
 * Description:     List of NVADD value
 * Created:         2016/03/26
 * Author:          George Zhao
 *
 * Copyright (c) 2017 EyeQue Corp
 */

public class NvaddListActivity extends Activity {
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
    private static final String TAG = NvaddListActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_nvadd_list);

        final ListView nvaddValueListView = (ListView) this.findViewById(R.id.nvaddValueListView);
        listItems.add("Don't know");
        listItems.add("+0.50");
        listItems.add("+1.00");
        listItems.add("+1.50");
        listItems.add("+2.00");
        listItems.add("+2.50");
        listItems.add("+3.00");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        nvaddValueListView.setAdapter(adapter);

        nvaddValueListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView) view).getText().toString();
                // Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                SingletonDataHolder.profileReadingGlassesValue_selected = item;
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
