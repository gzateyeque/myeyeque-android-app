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
 * File:            ServerActivity.java
 * Description:     Server selection: Local or Network
 * Created:         2016/02/05
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class ServerActivity extends Activity {
    /**
     * The List items.
     */
// List of array string which will serve as list items
    ArrayList<String> listItems = new ArrayList<String>();

    /**
     * The Adapter.
     */
// Defining a string adapter whcih will handle the data of the ListView
    ArrayAdapter<String> adapter;

    /**
     * The Click counter.
     */
    int clickCounter = 0;  // record how many times the button has been clicked
    /**
     * The Items.
     */
    String[] items;


    private static final String TAG = ServerActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_server);

        final ListView serverListView = (ListView) this.findViewById(R.id.serverListView);
        listItems.add("Internal");
        listItems.add("Amazon Cloud");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        serverListView.setAdapter(adapter);

        serverListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView) view).getText().toString();
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
// Method to handle dynamic insertion
    public void addItems(View v) {
        listItems.add("Clicked : " + clickCounter++);
        adapter.notifyDataSetChanged();
    }
}
