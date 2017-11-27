package com.eyeque.eyeque;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.eyeque.eyeque.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
/**
 *
 * File:            SubjectActivity.java
 * Description:     Prototype - Subject selection
 * Created:         2016/03/19
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class SubjectActivity extends Activity {
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    //RECORDING HOW MANY TIMES THE BUTTON HAS BEEN CLICKED
    int clickCounter = 0;
    String[] items;
    int[] iDIndex;

    private int subjectId = 0;
    private String subjectFirstName = null;
    private String subjectLastName = null;
    private String subjectEmail = null;

    private static SQLiteDatabase myDb = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    // Tag for log message
    private static final String TAG = SubjectActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstance) {

        super.onCreate(savedInstance);
        setContentView(R.layout.activity_subject);

        final ListView subjectListView = (ListView) this.findViewById(R.id.subjectListView);

        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            myDb = dbHelper.getWritableDatabase();
            Log.d("TAG", "open database successfully");
        } catch (IOException e) {
            Log.d("TAG", "open database failed");
        }

        Intent intent = getIntent();
        String objString = intent.getStringExtra("jsonArray");
        ContentValues values = new ContentValues();

        try {
            JSONArray objArray = new JSONArray(objString);
            Log.i(TAG, objArray.toString());
            int len = objArray.length();
            iDIndex = new int[len];
            for(int i = 0; i < len; ++i) {
                try {
                    JSONObject tmpobj = objArray.getJSONObject(i);
                    iDIndex[i] = tmpobj.getInt("id");
                    subjectFirstName = tmpobj.getString("firstName");
                    subjectLastName = tmpobj.getString("lastName");
                    subjectEmail = tmpobj.getString("email");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                listItems.add(subjectFirstName + " " + subjectLastName);

                // Store network data to local persistence
                values.clear();
                values.put(Constants.TEST_SUBJECT_ID_COLUMN, iDIndex[i]);
                values.put(Constants.TEST_SUBJECT_VERSION_COLUMN, 1);
                values.put(Constants.TEST_SUBJECT_FIRST_NAME_COLUMN, subjectFirstName);
                values.put(Constants.TEST_SUBJECT_LAST_NAME_COLUMN, subjectLastName);
                values.put(Constants.TEST_SUBJECT_EMAIL_COLUMN, subjectEmail);
                values.put(Constants.TEST_SUBJECT_CREATED_AT_COLUMN, 0);
                values.put(Constants.TEST_SUBJECT_LAST_SYNCED_AT_COLUMN, 0);
                values.put(Constants.TEST_SUBJECT_STATUS, 0);

                // values.put(Constants.TEST_SUBJECT_LAST_NAME_COLUMN, subjectLastName);
                // values.put(Constants.TEST_SUBJECT_EMAIL_COLUMN, subjectEmail);

                // values.put(Constants.TEST_SUBJECT_STATUS, 1);

                myDb.insertWithOnConflict(Constants.TEST_SUBJECT_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

                // String sql = "insert into foo values (" + iDIndex[i] + ", \"" + subjectFirstName + " " + subjectLastName + "\")";;
                // Log.d(TAG, sql);
                // myDb.execSQL(sql);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        subjectListView.setAdapter(adapter);

        subjectListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView) view).getText().toString();
                // Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                Intent result = new Intent();
                result.putExtra("index", iDIndex[position]);
                result.putExtra("result", item);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addItems(View v) {
        listItems.add("Clicked : " + clickCounter++);
        adapter.notifyDataSetChanged();
    }
}
