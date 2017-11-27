package com.eyeque.eyeque;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eyeque.eyeque.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * File:            LoginActivity.java
 * Description:     Login screen that let user login or sign up using email and password.
 *                  User can also login via Google or Facebook account
 * Created:         2016/08/02
 * Author:          George Zhao
 *
 * Copyright (c) 2017 EyeQue Corp
 */
public class StartActivity extends Activity {

    private String device = null;
    private String subject = null;
    private String server = null;
    private int deviceId = 0;
    private static int subjectId = 0;
    private int serverId = 0;

    private Button deviceButton;
    private Button subjectButton;
    private Button serverButton;
    private View loadingBar;
    private int loadingCnt = 0;

    private static SQLiteDatabase myDb = null;
    // Tag for log message
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    // private final static  String RestfulBaseURL = "http://54.191.245.62:8080"; // Amazon EC2
    // private final static String RestfulBaseURL = "http://192.168.110.122:8080"; // Office Server

    /**
     * The type Retrieve subject task.
     */
    class RetrieveSubjectTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected String doInBackground(Void... urls) {

            // Do some validation here
            NetConnection conn = new NetConnection();
            if (conn.isConnected(getApplicationContext())) {
                try {
                    String urlString;
                    // if (serverId == 0)
                        // urlString = Constants.RestfulBaseURL + "/eyecloud/api/subjects";
                    // else
                        urlString = Constants.RestfulBaseURL + "/eyecloud/api/subjects?access_token=" + Constants.AccessToken;
                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(Constants.NETCONN_TIMEOUT_VALUE);
                    urlConnection.setReadTimeout(Constants.NETCONN_TIMEOUT_VALUE);
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        // Log.i("***********", stringBuilder.toString());
                        return stringBuilder.toString();
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (java.net.MalformedURLException e) {
                    Log.e("URL ERROR", e.getMessage(), e);
                    return null;
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    return null;
                }
            }
            else {

                try {
                    int id, cnt = 0;
                    String firstName, lastName, email;
                    DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                    myDb = dbHelper.getReadableDatabase();
                    Log.d("TAG", "open database successfully");
                    Cursor cursor = myDb.rawQuery("SELECT _id, first_name, last_name, email FROM test_subject order by first_name", null);
                    cursor.moveToFirst();
                    JSONArray jsonArr = new JSONArray();
                    try {

                        while (cursor.isAfterLast() == false) {
                            id = cursor.getInt(cursor.getColumnIndex(Constants.TEST_SUBJECT_ID_COLUMN));
                            firstName = cursor.getString(cursor.getColumnIndex(Constants.TEST_SUBJECT_FIRST_NAME_COLUMN));
                            lastName = cursor.getString(cursor.getColumnIndex(Constants.TEST_SUBJECT_LAST_NAME_COLUMN));
                            email = cursor.getString(cursor.getColumnIndex(Constants.TEST_SUBJECT_EMAIL_COLUMN));
                            JSONObject subjectObj = new JSONObject();
                            subjectObj.put("id", id);
                            subjectObj.put("firstName", firstName);
                            subjectObj.put("lastName", lastName);
                            subjectObj.put("email", email);
                            jsonArr.put(subjectObj);
                            cursor.moveToNext();
                            cnt++;
                        }
                    } catch (JSONException e) {
                        Log.d("TAG", "Build JSON array failed");
                    }
                    return jsonArr.toString();
                } catch (IOException e) {
                    Log.d("TAG", "open database failed");
                }
                return null;
            }
        }

        protected void onPostExecute(String response) {
            loadingBar = (View) findViewById(R.id.loadingPanel);
            loadingBar.setVisibility(View.GONE);
            if(response == null) {
                response = "THERE WAS AN ERROR";
                Toast.makeText(StartActivity.this,
                        "Can't connect to the server", Toast.LENGTH_SHORT).show();
            } else {
                Intent subjectIntent = new Intent(getBaseContext(), SubjectActivity.class);
                subjectIntent.putExtra("jsonArray", response);
                startActivityForResult(subjectIntent, 200);
            }
            Log.i(TAG, response);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        TextView title = (TextView) findViewById(R.id.startTitleTextView);
        title.setText("EyeQue Vision Test v" + Constants.BuildNumber);

        Button startTestButton = (Button) findViewById(R.id.startTestButton);
        startTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (device != null && subject != null) {
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    i.putExtra("subjectId", subjectId);
                    i.putExtra("deviceId", deviceId);
                    i.putExtra("serverId", serverId);
                    startActivity(i);
                } else {
                    Toast.makeText(StartActivity.this,
                            "Please select a subject", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deviceButton = (Button) findViewById(R.id.deviceButton);
        device = "Device: #1";
        deviceButton.setText("Device: #1");
        deviceButton.setTextSize(18);
        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deviceIntent = new Intent(getBaseContext(), DeviceActivity.class);
                startActivityForResult(deviceIntent, 100);
            }
        });

        subjectButton = (Button) findViewById(R.id.subjectButton);
        loadingBar = (View) findViewById(R.id.loadingPanel);
        subjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(StartActivity.this,
                       // "Loading Subject List...", Toast.LENGTH_LONG).show();
                loadingBar.setVisibility(View.VISIBLE);
                new RetrieveSubjectTask().execute();
            }
        });

        /*
        serverButton = (Button) findViewById(R.id.serverButton);
        Constants.RestfulBaseURL = Constants.LocalRestfulBaseURL;
        server = "Internal";
        serverButton.setText("Server: Internal");
        serverButton.setTextSize(18);
        serverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent(getBaseContext(), ServerActivity.class);
                startActivityForResult(serverIntent, 300);
            }
        });
        */

        subjectButton = (Button) findViewById(R.id.subjectButton);
        loadingBar = (View) findViewById(R.id.loadingPanel);
        subjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(StartActivity.this,
                // "Loading Subject List...", Toast.LENGTH_LONG).show();
                loadingBar.setVisibility(View.VISIBLE);
                new RetrieveSubjectTask().execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    device = data.getStringExtra("result");
                    deviceId = new Integer(data.getStringExtra("index"));
                    deviceButton.setText("Device: " + device);
                    deviceButton.setTextSize(18);
                }
                break;
            case 200:
                if (resultCode == RESULT_OK) {
                    subject = data.getStringExtra("result");
                    subjectId = data.getIntExtra("index", 0);
                    Log.i("INFO", new Integer(subjectId).toString());
                    subjectButton.setText("Subject: " + subject);
                    subjectButton.setTextSize(18);
                }
                break;
            /*
            case 300:
                if (resultCode == RESULT_OK) {
                    server = data.getStringExtra("result");
                    serverId = new Integer(data.getStringExtra("index"));
                    if (serverId > 0)
                        Constants.RestfulBaseURL = Constants.AwsEc2RestfulBaseURL;
                    else
                        Constants.RestfulBaseURL = Constants.LocalRestfulBaseURL;
                    serverButton.setText("Server: " + server);
                    serverButton.setTextSize(18);
                }
                break;
              */
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myDb != null )
            myDb.close();
    }
}
