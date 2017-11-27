package com.eyeque.eyeque;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * File:            DashboardFragment.java
 * Description:     Dashboard fragment to display user's test progress, lastest EyeGlass Number,
 *                  vision snapshot and historical tracking chart
 * Created:         2016/07/17
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class Dashboard1Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Context thisContext;
    private static final String TAG = Dashboard1Fragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private WebView webview;
    private String mParam1;
    private String mParam2;
    private LinearLayout scoreLayout;
    private TextView scoreTv;
    private TextView eyeglassNumDescTv;
    private ProgressBar progressBar;
    private Button newEyeglassNumberBtn;
    private ImageButton eyeglassNumListExpandIv;
    private TableLayout eyeglassTableLayout;
    private WebView visionSummarydWebView;
    private WebView trackingDataOdWebView;
    private WebView trackingDataOsWebView;
    private TableRow pdTblRow;
    private TableRow dateTblRow;
    private TableRow herderTableRow;
    private TableRow odTableRow;
    private TableRow osTableRow;
    private Boolean eyeglassNumListToggle = false;
    private OnFragmentInteractionListener mListener;

    public Dashboard1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Dashboard1Fragment newInstance(String param1, String param2) {
        Dashboard1Fragment fragment = new Dashboard1Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard1, container, false);
        SingletonDataHolder.lang = Locale.getDefault().getLanguage();

        thisContext = getActivity().getApplicationContext();

        this.webview = (WebView) rootView.findViewById(R.id.shopWebView);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        // webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        // webview.setVerticalScrollBarEnabled(false);
        // webview.getSettings().setLoadWithOverviewMode(true);
        // webview.getSettings().setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        //To disabled the horizontal and vertical scrolling:
        // webview.setOnTouchListener(new View.OnTouchListener() {
           // public boolean onTouch(View v, MotionEvent event) {
           //     return (event.getAction() == MotionEvent.ACTION_MOVE);
           // }
        // });
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "Loading...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                /**
                 Log.i(TAG, "Finished loading URL: " + url);
                 if (progressBar.isShowing()) {
                 progressBar.dismiss();
                 }
                 **/
            }
        });
        // webview.loadUrl(Constants.UrlShop);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDashboardFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDashboardFragmentInteraction(Uri uri);
    }

    private void GetDahsboardInfo() {

        String url = Constants.UrlDashboard;
        NetConnection conn = new NetConnection();
        if (conn.isConnected(thisContext)) {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);
            RequestQueue queue = Volley.newRequestQueue(thisContext);

            final JSONObject params = new JSONObject();
            final JSONArray eyeglassNumArray = new JSONArray();

            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("*** Dashboard Info ***", response);
                    SingletonDataHolder.dahsboardApiRespData = response;
                    try {
                        final JSONObject result = new JSONObject(response);
                        final JSONObject eyeglassResult = result.getJSONObject("eyeglassnumbers");
                        final JSONArray eyeglassNumList = eyeglassResult.getJSONArray("purchasednumbers");

                        SingletonDataHolder.urlOdTracking = result.getString("url_spheod");
                        SingletonDataHolder.urlOSTracking = result.getString("url_spheos");
                        SingletonDataHolder.urlVisionSummary = result.getString("url_vision_summary");
                        SingletonDataHolder.urlVisionSummary = SingletonDataHolder.urlVisionSummary.replace("diagram", "piechart");
                        SingletonDataHolder.pupillaryDistance = eyeglassResult.getInt("pd");
                        SingletonDataHolder.nvadd = eyeglassResult.getDouble("nvadd");
                        SingletonDataHolder.currentTestScore = eyeglassResult.getInt("score");
                        SingletonDataHolder.freetrial = eyeglassResult.getInt("freetrial");
                        SingletonDataHolder.eyeglassNumPurchasable = eyeglassResult.getBoolean("purchasable");
                        SingletonDataHolder.eyeglassNumCount = eyeglassNumList.length();

                        Log.i("*****PD******", Integer.toString(SingletonDataHolder.pupillaryDistance));
                        Log.i("*** Purchasable ***", Boolean.toString(SingletonDataHolder.eyeglassNumPurchasable));

                        SingletonDataHolder.eyeglassNumberList = new SingletonDataHolder.EyeglassNumber[SingletonDataHolder.eyeglassNumCount];
                        for (int i = 0; i < SingletonDataHolder.eyeglassNumCount; i++) {
                            JSONObject eyeglassNumber = eyeglassNumList.getJSONObject(i);
                            SingletonDataHolder.eyeglassNumberList[i] = new SingletonDataHolder.EyeglassNumber(
                                    eyeglassNumber.getDouble("sphOD"),
                                    eyeglassNumber.getDouble("cylOD"),
                                    eyeglassNumber.getInt("axisOD"),
                                    eyeglassNumber.getDouble("sphOS"),
                                    eyeglassNumber.getDouble("cylOS"),
                                    eyeglassNumber.getInt("axisOS"),
                                    eyeglassNumber.getString("createdAt"));
                            Log.i("***--- AXIS-0 ---***", Integer.toString(SingletonDataHolder.eyeglassNumberList[i].odAxis));
                        }
                        for (int i = 0; i < SingletonDataHolder.eyeglassNumCount; i++) {
                            Log.i("***--- AXIS-1 ---***", Integer.toString(SingletonDataHolder.eyeglassNumberList[i].odAxis));
                        }
                        loadData();
                        loadEyeglassNumber();
                        // Log.i("*** Tracking OD ***", result.getString("url_spheod"));
                        // Log.i("*** Tracking OS ***", result.getString("url_spheos"));
                        // Log.i("*** Score ***", Integer.toString(SingletonDataHolder.currentTestScore));
                        // Log.i("*** Purchasable ***", Boolean.toString(SingletonDataHolder.eyeglassNumPurchasable));
                        Log.i("*** VisionSum ***", SingletonDataHolder.urlVisionSummary);
                        visionSummarydWebView.clearCache(true);
                        visionSummarydWebView.loadUrl(SingletonDataHolder.urlVisionSummary);
                        trackingDataOdWebView.clearCache(true);
                        trackingDataOdWebView.loadUrl(SingletonDataHolder.urlOdTracking);
                        trackingDataOsWebView.loadUrl(SingletonDataHolder.urlOSTracking);
                    } catch (JSONException e) {
                        Toast.makeText(thisContext,
                                "Operation failed, please try it again", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // showProgress(false);
                    Log.d("Error.Response", error.toString());
                    SingletonDataHolder.dahsboardApiRespData = error.toString();
                    Toast.makeText(thisContext, "Server Error", Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String authString = "Bearer " + SingletonDataHolder.token;
                    headers.put("Content-Type", "application/json;charset=UTF-8");
                    headers.put("Authorization", authString);
                    return headers;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(Constants.NETCONN_TIMEOUT_VALUE, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            queue.add(postRequest);
        }
        else {
            loadData();
            loadEyeglassNumber();
            Log.i("*** VisionSum1 ***", SingletonDataHolder.urlVisionSummary);
            visionSummarydWebView.loadUrl(SingletonDataHolder.urlVisionSummary);
            trackingDataOdWebView.loadUrl(SingletonDataHolder.urlOdTracking);
            trackingDataOsWebView.loadUrl(SingletonDataHolder.urlOSTracking);
        }
    }

    private void loadData() {

        scoreLayout.removeAllViewsInLayout();

        if (SingletonDataHolder.eyeglassNumPurchasable) {

            /****
             eyeglassNumDescTv = new TextView(thisContext);
             LayoutParams eyeglassNumDescTvParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
             eyeglassNumDescTv.setGravity(Gravity.CENTER);
             eyeglassNumDescTv.setText("Your new EyeGlass Numbers is ready");
             eyeglassNumDescTv.setTextColor(Color.BLACK);
             eyeglassNumDescTv.setTextSize(16);
             scoreLayout.addView(eyeglassNumDescTv, eyeglassNumDescTvParams);
             ****/

            newEyeglassNumberBtn = new Button(thisContext);
            LinearLayout.LayoutParams newEyeglassNumberBtnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            newEyeglassNumberBtnParams.setMargins(170, 20, 170, 0);
            newEyeglassNumberBtn.setBackgroundColor(Color.parseColor("#ffa500"));
            if (SingletonDataHolder.eyeglassNumCount > 0)
                newEyeglassNumberBtn.setText("UPDATE YOUR EYEGLASS NUMBERS");
            else
                newEyeglassNumberBtn.setText("GET YOUR EYEGLASS NUMBERS");
            newEyeglassNumberBtn.setTextColor(Color.WHITE);
            newEyeglassNumberBtn.setTypeface(null, Typeface.BOLD);
            newEyeglassNumberBtn.setTextSize(16);
            newEyeglassNumberBtn.setGravity(Gravity.CENTER);
            // newEyeglassNumberBtn.setLayoutParams(newEyeglassNumberBtnParams);
            scoreLayout.addView(newEyeglassNumberBtn, newEyeglassNumberBtnParams);
            newEyeglassNumberBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetNewEyeglassNumber();
                }
            });
        } else if (SingletonDataHolder.currentTestScore == 0 && SingletonDataHolder.eyeglassNumCount == 0) {
            eyeglassNumDescTv = new TextView(thisContext);
            LinearLayout.LayoutParams eyeglassNumDescTvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            eyeglassNumDescTvParams.setMargins(30, 0, 30, 0);
            eyeglassNumDescTv.setGravity(Gravity.CENTER);
            eyeglassNumDescTv.setTypeface(null, Typeface.BOLD);
            eyeglassNumDescTv.setText("Start the full test and get your EyeGlass Numbers");
            eyeglassNumDescTv.setTextColor(Color.BLACK);
            eyeglassNumDescTv.setTextSize(16);
            scoreLayout.addView(eyeglassNumDescTv, eyeglassNumDescTvParams);

        } else if (SingletonDataHolder.currentTestScore < 100){
            scoreTv = new TextView(thisContext);
            LinearLayout.LayoutParams scoreTvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            scoreTv.setGravity(Gravity.CENTER);
            scoreTv.setText("Progress: " + Integer.toString(SingletonDataHolder.currentTestScore));
            scoreTv.setTextColor(Color.BLACK);
            scoreTv.setTextSize(17);
            scoreLayout.addView(scoreTv, scoreTvParams);

            progressBar = new ProgressBar(thisContext, null, android.R.attr.progressBarStyleHorizontal);
            LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            progressBarParams.setMargins(150, 0, 150, 0);
            progressBar.setMax(100);
            if (SingletonDataHolder.currentTestScore >= 100)
                progressBar.setProgress(100);
            else
                progressBar.setProgress(SingletonDataHolder.currentTestScore);
            scoreLayout.addView(progressBar, progressBarParams);

            eyeglassNumDescTv = new TextView(thisContext);
            LinearLayout.LayoutParams eyeglassNumDescTvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            eyeglassNumDescTvParams.setMargins(30, 0, 30, 0);
            eyeglassNumDescTv.setGravity(Gravity.CENTER);
            eyeglassNumDescTv.setText("Your EyeGlass Numbers will be available once your progress reaches 100");
            eyeglassNumDescTv.setTextColor(Color.GRAY);
            eyeglassNumDescTv.setTextSize(16);
            scoreLayout.addView(eyeglassNumDescTv, eyeglassNumDescTvParams);
        } else if (SingletonDataHolder.freetrial < 1) {
            eyeglassNumDescTv = new TextView(thisContext);
            LinearLayout.LayoutParams eyeglassNumDescTvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            eyeglassNumDescTvParams.setMargins(30, 0, 30, 0);
            eyeglassNumDescTv.setGravity(Gravity.CENTER);
            eyeglassNumDescTv.setText("The free trial has ended, please upgrade your app");
            eyeglassNumDescTv.setTextColor(Color.GRAY);
            eyeglassNumDescTv.setTextSize(16);
            scoreLayout.addView(eyeglassNumDescTv, eyeglassNumDescTvParams);
        }
        else {
            eyeglassNumDescTv = new TextView(thisContext);
            LinearLayout.LayoutParams eyeglassNumDescTvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            eyeglassNumDescTvParams.setMargins(30, 0, 30, 0);
            eyeglassNumDescTv.setGravity(Gravity.CENTER);
            if (SingletonDataHolder.lang.equals("zh"))
                eyeglassNumDescTv.setText("  你可以做更多测试，系统会产生新的验光结果");
            else
                eyeglassNumDescTv.setText("Perform more tests to get your updated EyeGlass Numbers");
            eyeglassNumDescTv.setTextColor(Color.GRAY);
            eyeglassNumDescTv.setTextSize(16);
            scoreLayout.addView(eyeglassNumDescTv, eyeglassNumDescTvParams);
        }
    }

    private void loadData1() {
        scoreTv.setText("Your progress: " + Integer.toString(SingletonDataHolder.currentTestScore));
        if (SingletonDataHolder.currentTestScore >= 100)
            progressBar.setProgress(100);
        else
            progressBar.setProgress(SingletonDataHolder.currentTestScore);

        if (SingletonDataHolder.eyeglassNumPurchasable)
            eyeglassNumDescTv.setText("Your new EyeGlass Numbers is available");
        else if (!SingletonDataHolder.eyeglassNumPurchasable && SingletonDataHolder.currentTestScore >= 100)
            eyeglassNumDescTv.setText("No updated EyeGlass Numbers available");
        else
            eyeglassNumDescTv.setText("Your EyeGlass Numbers will be avaiable once your progress reaches 100");


        if (SingletonDataHolder.eyeglassNumPurchasable)
            newEyeglassNumberBtn.setClickable(true);
        else {
            newEyeglassNumberBtn.setClickable(false);
            newEyeglassNumberBtn.setBackgroundColor(Color.LTGRAY);
            newEyeglassNumberBtn.setTextColor(Color.WHITE);
        }
    }

    private void loadEyeglassNumber() {
        int displayNumRow;

        if (pdTblRow != null || dateTblRow != null)
            eyeglassTableLayout.removeAllViewsInLayout();
        /***
        if (pdTblRow != null)
            eyeglassTableLayout.removeView(pdTblRow);
        if (dateTblRow != null)
            eyeglassTableLayout.removeView(dateTblRow);
        if (odTableRow != null)
        eyeglassTableLayout.removeView(odTableRow);
        if (osTableRow != null)
            eyeglassTableLayout.removeView(osTableRow);
        if (osTableRow != null)
            eyeglassTableLayout.removeView(herderTableRow);
         ***/

        for (int i = 0; i < SingletonDataHolder.eyeglassNumCount; i++) {
            Log.i("***--- AXIS-2 ---***", Integer.toString(SingletonDataHolder.eyeglassNumberList[i].odAxis));
        }

        /****
        pdTblRow = new TableRow(thisContext);
        pdTblRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        TextView pdTextView = new TextView(thisContext);
        if (SingletonDataHolder.pupillaryDistance > 0)
            pdTextView.setText("PD: " + Integer.toString(SingletonDataHolder.pupillaryDistance));
        else
            pdTextView.setText("");
        pdTextView.setTextColor(Color.BLACK);
        pdTextView.setTextSize(18);
        TableRow.LayoutParams pdTextViewParams = new TableRow.LayoutParams();
        pdTextViewParams.column = 0;
        pdTextViewParams.span = 4;
        pdTextViewParams.gravity = Gravity.LEFT;
        pdTextViewParams.topMargin = 10;
        pdTextViewParams.bottomMargin = 10;
        pdTblRow.addView(pdTextView, pdTextViewParams);
        eyeglassTableLayout.addView(pdTblRow);
         ****/


        // Set display row
        // if (eyeglassNumListToggle)
            // displayNumRow = SingletonDataHolder.eyeglassNumCount;
        // else
            displayNumRow = 1;

        // Set arrow button dynamically
        /****
        if (SingletonDataHolder.eyeglassNumCount <= 1)
            eyeglassNumListExpandIv.setEnabled(false);
        else if (eyeglassNumListToggle)
            eyeglassNumListExpandIv.setImageResource(R.drawable.arrow_up);
        else
            eyeglassNumListExpandIv.setImageResource(R.drawable.arrow_down);
         ****/

        if (SingletonDataHolder.eyeglassNumCount > 0 || SingletonDataHolder.pupillaryDistance > 0) {

            TextView divLine = new TextView(thisContext);
            divLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
            divLine.setBackgroundColor(Color.rgb(51, 51, 51));
            eyeglassTableLayout.addView(divLine);
            // }
            for (int i = 0; i < SingletonDataHolder.eyeglassNumCount; i++) {
                Log.i("***--- AXIS-3 ---***", Integer.toString(SingletonDataHolder.eyeglassNumberList[i].odAxis));
            }
            Log.i("***--- DROW ---***", Integer.toString(displayNumRow));
            int count = 0;
            for (int i = SingletonDataHolder.eyeglassNumCount - 1; i >= 0; i--) {

                // Add datetime of the eyeglass number
                // dateTblRow = new TableRow(thisContext);
                dateTblRow = new TableRow(thisContext);
                dateTblRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                TextView dataTextView = new TextView(thisContext);
                // dataTextView.setText(SingletonDataHolder.eyeglassNumberList[i].createdAt);


                try {
                    SimpleDateFormat simpleDateFormat;
                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date myDate = simpleDateFormat.parse(SingletonDataHolder.eyeglassNumberList[i].createdAt);

                    Log.i("*** TZTZTZ ***", myDate.toString());
                    dataTextView.setText(myDate.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //textview.getTextColors(R.color.)
                dataTextView.setTextColor(Color.BLACK);

                TableRow.LayoutParams dateTextViewParams = new TableRow.LayoutParams();
                // dateTextViewParams.width = LayoutParams.MATCH_PARENT;
                // dateTextViewParams.height = LayoutParams.WRAP_CONTENT;
                dateTextViewParams.column = 0;
                dateTextViewParams.span = 4;
                dateTextViewParams.gravity = Gravity.CENTER;
                dateTextViewParams.topMargin = 10;
                dateTblRow.addView(dataTextView, dateTextViewParams);
                eyeglassTableLayout.addView(dateTblRow);

                // Add datetime of the eyeglass number
                herderTableRow = new TableRow(thisContext);
                herderTableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                TextView sphTextView = new TextView(thisContext);
                sphTextView.setTextColor(Color.BLACK);
                sphTextView.setGravity(1);
                if (SingletonDataHolder.lang.equals("zh"))
                    sphTextView.setText("球镜");
                else
                    sphTextView.setText("SPHERICAL");
                sphTextView.setTextColor(Color.BLACK);
                sphTextView.setTypeface(null, Typeface.BOLD);
                TableRow.LayoutParams sphTextViewParams = new TableRow.LayoutParams();
                sphTextViewParams.width = LayoutParams.MATCH_PARENT;
                sphTextViewParams.height = LayoutParams.WRAP_CONTENT;
                sphTextViewParams.column = 1;
                sphTextViewParams.gravity = Gravity.CENTER;
                herderTableRow.addView(sphTextView, sphTextViewParams);

                TextView cylTextView = new TextView(thisContext);
                cylTextView.setTextColor(Color.BLACK);
                cylTextView.setGravity(1);
                if (SingletonDataHolder.lang.equals("zh"))
                    cylTextView.setText("球镜");
                else
                    cylTextView.setText("CYLINDRICAL");
                cylTextView.setLayoutParams(new TableRow.LayoutParams(2));
                cylTextView.setTextColor(Color.BLACK);
                cylTextView.setTypeface(null, Typeface.BOLD);
                herderTableRow.addView(cylTextView);

                TextView axisTextView = new TextView(thisContext);
                axisTextView.setTextColor(Color.BLACK);
                axisTextView.setGravity(1);
                if (SingletonDataHolder.lang.equals("zh"))
                    axisTextView.setText("轴位");
                else
                    axisTextView.setText("AXIS");
                axisTextView.setLayoutParams(new TableRow.LayoutParams(3));
                axisTextView.setTextColor(Color.BLACK);
                axisTextView.setTypeface(null, Typeface.BOLD);
                herderTableRow.addView(axisTextView);
                eyeglassTableLayout.addView(herderTableRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));


                //Add OD Values of eyeglass number
                odTableRow = new TableRow(thisContext);
                odTableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                TextView odHeaderTextView = new TextView(thisContext);
                odHeaderTextView.setTextColor(Color.BLACK);
                odHeaderTextView.setGravity(1);
                if (SingletonDataHolder.lang.equals("zh"))
                    odHeaderTextView.setText("右眼 (R)");
                else
                    odHeaderTextView.setText("O.D. (RIGHT)");
                odHeaderTextView.setTextColor(Color.BLACK);
                odHeaderTextView.setTypeface(null, Typeface.BOLD);
                odHeaderTextView.setPadding(60, 0, 0, 0);
                TableRow.LayoutParams odHeaderTextViewParams = new TableRow.LayoutParams();
                odHeaderTextViewParams.width = LayoutParams.MATCH_PARENT;
                odHeaderTextViewParams.height = LayoutParams.WRAP_CONTENT;
                odHeaderTextViewParams.column = 0;
                odHeaderTextViewParams.gravity = Gravity.LEFT;
                odTableRow.addView(odHeaderTextView, odHeaderTextViewParams);

                TextView odSphTextView = new TextView(thisContext);
                odSphTextView.setTextColor(Color.BLACK);
                odSphTextView.setGravity(1);
                if (SingletonDataHolder.eyeglassNumberList[i].odSph > 0)
                    odSphTextView.setText(String.format("+%.2f", SingletonDataHolder.eyeglassNumberList[i].odSph));
                else
                    odSphTextView.setText(String.format("%.2f", SingletonDataHolder.eyeglassNumberList[i].odSph));
                odSphTextView.setTextColor(Color.BLACK);
                odSphTextView.setTextSize(18);
                // odSphTextView.setTypeface(null, Typeface.BOLD);
                TableRow.LayoutParams odSphTextViewParams = new TableRow.LayoutParams();
                odSphTextViewParams.width = LayoutParams.MATCH_PARENT;
                odSphTextViewParams.height = LayoutParams.WRAP_CONTENT;
                odSphTextViewParams.column = 1;
                odSphTextViewParams.gravity = Gravity.CENTER;
                odTableRow.addView(odSphTextView, odSphTextViewParams);

                TextView odCylTextView = new TextView(thisContext);
                odCylTextView.setTextColor(Color.BLACK);
                odCylTextView.setGravity(1);
                if (SingletonDataHolder.eyeglassNumberList[i].odCyl > -0.4)
                    odCylTextView.setText("--");
                else
                    odCylTextView.setText(String.format("%.2f", SingletonDataHolder.eyeglassNumberList[i].odCyl));
                odCylTextView.setLayoutParams(new TableRow.LayoutParams(2));
                odCylTextView.setTextSize(18);
                odCylTextView.setTextColor(Color.BLACK);
                // odCylTextView.setTypeface(null, Typeface.BOLD);
                TableRow.LayoutParams odCylTextViewParams = new TableRow.LayoutParams();
                odCylTextViewParams.width = LayoutParams.MATCH_PARENT;
                odCylTextViewParams.height = LayoutParams.WRAP_CONTENT;
                odCylTextViewParams.column = 2;
                odCylTextViewParams.gravity = Gravity.CENTER;
                odTableRow.addView(odCylTextView, odCylTextViewParams);

                TextView odAxisTextView = new TextView(thisContext);
                odAxisTextView.setTextColor(Color.BLACK);
                odAxisTextView.setGravity(1);
                Log.i("***--- AXIS ---***", Integer.toString(SingletonDataHolder.eyeglassNumberList[i].odAxis));
                if (SingletonDataHolder.eyeglassNumberList[i].odCyl > -0.4)
                    odAxisTextView.setText("--");
                else
                    odAxisTextView.setText(Integer.toString(SingletonDataHolder.eyeglassNumberList[i].odAxis));
                odAxisTextView.setLayoutParams(new TableRow.LayoutParams(3));
                odAxisTextView.setTextSize(18);
                odAxisTextView.setTextColor(Color.BLACK);
                // odAxisTextView.setTypeface(null, Typeface.BOLD);
                TableRow.LayoutParams odAxisTextViewParams = new TableRow.LayoutParams();
                odAxisTextViewParams.width = LayoutParams.MATCH_PARENT;
                odAxisTextViewParams.height = LayoutParams.WRAP_CONTENT;
                odAxisTextViewParams.column = 3;
                odAxisTextViewParams.gravity = Gravity.CENTER;
                odTableRow.addView(odAxisTextView, odAxisTextViewParams);

                eyeglassTableLayout.addView(odTableRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                // Add OS Values of eyeglass number
                osTableRow = new TableRow(thisContext);
                // osTableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                TableRow.LayoutParams osTAbleRowParams = new TableRow.LayoutParams();
                osTAbleRowParams.bottomMargin = 20;

                TextView osHeaderTextView = new TextView(thisContext);
                osHeaderTextView.setTextColor(Color.BLACK);
                osHeaderTextView.setGravity(1);
                if (SingletonDataHolder.lang.equals("zh"))
                    osHeaderTextView.setText("左眼 (L)");
                else
                    osHeaderTextView.setText("O.S. (LEFT)");
                osHeaderTextView.setTextColor(Color.BLACK);
                osHeaderTextView.setTypeface(null, Typeface.BOLD);
                osHeaderTextView.setPadding(60, 0, 0, 0);
                TableRow.LayoutParams osHeaderTextViewParams = new TableRow.LayoutParams();
                osHeaderTextViewParams.width = LayoutParams.MATCH_PARENT;
                osHeaderTextViewParams.height = LayoutParams.WRAP_CONTENT;
                osHeaderTextViewParams.column = 0;
                osHeaderTextViewParams.gravity = Gravity.LEFT;
                osTableRow.addView(osHeaderTextView, osHeaderTextViewParams);

                TextView osSphTextView = new TextView(thisContext);
                osSphTextView.setTextColor(Color.BLACK);
                osSphTextView.setGravity(1);
                if (SingletonDataHolder.eyeglassNumberList[i].osSph > 0)
                    osSphTextView.setText(String.format("+%.2f", SingletonDataHolder.eyeglassNumberList[i].osSph));
                else
                    osSphTextView.setText(String.format("%.2f", SingletonDataHolder.eyeglassNumberList[i].osSph));
                osSphTextView.setTextColor(Color.BLACK);
                osSphTextView.setTextSize(18);
                // osSphTextView.setTypeface(null, Typeface.BOLD);
                TableRow.LayoutParams osSphTextViewParams = new TableRow.LayoutParams();
                osSphTextViewParams.width = LayoutParams.MATCH_PARENT;
                osSphTextViewParams.height = LayoutParams.WRAP_CONTENT;
                osSphTextViewParams.column = 1;
                osSphTextViewParams.gravity = Gravity.CENTER;
                osTableRow.addView(osSphTextView, osSphTextViewParams);


                TextView osCylTextView = new TextView(thisContext);
                osCylTextView.setTextColor(Color.BLACK);
                osCylTextView.setGravity(1);
                if (SingletonDataHolder.eyeglassNumberList[i].osCyl > -0.4)
                    osCylTextView.setText("--");
                else
                    osCylTextView.setText(String.format("%.2f", SingletonDataHolder.eyeglassNumberList[i].osCyl));
                osCylTextView.setLayoutParams(new TableRow.LayoutParams(2));
                osCylTextView.setTextColor(Color.BLACK);
                osCylTextView.setTextSize(18);
                // osCylTextView.setTypeface(null, Typeface.BOLD);
                TableRow.LayoutParams osCylTextViewParams = new TableRow.LayoutParams();
                osCylTextViewParams.width = LayoutParams.MATCH_PARENT;
                osCylTextViewParams.height = LayoutParams.WRAP_CONTENT;
                osCylTextViewParams.column = 2;
                osCylTextViewParams.gravity = Gravity.CENTER;
                osTableRow.addView(osCylTextView, osCylTextViewParams);

                TextView osAxisTextView = new TextView(thisContext);
                osAxisTextView.setTextColor(Color.BLACK);
                osAxisTextView.setGravity(1);
                if (SingletonDataHolder.eyeglassNumberList[i].osCyl > -0.4)
                    osAxisTextView.setText("--");
                else
                    osAxisTextView.setText(Integer.toString(SingletonDataHolder.eyeglassNumberList[i].osAxis));
                osAxisTextView.setLayoutParams(new TableRow.LayoutParams(3));
                osAxisTextView.setTextColor(Color.BLACK);
                osAxisTextView.setTextSize(18);
                // osAxisTextView.setTypeface(null, Typeface.BOLD);
                TableRow.LayoutParams osAxisTextViewParams = new TableRow.LayoutParams();
                osAxisTextViewParams.width = LayoutParams.MATCH_PARENT;
                osAxisTextViewParams.height = LayoutParams.WRAP_CONTENT;
                osAxisTextViewParams.column = 3;
                osAxisTextViewParams.gravity = Gravity.CENTER;
                osTableRow.addView(osAxisTextView, osAxisTextViewParams);

                eyeglassTableLayout.addView(osTableRow, osTAbleRowParams);

                /***
                if (i >= 0) {
                    TextView divLine = new TextView(thisContext);
                    divLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
                    divLine.setBackgroundColor(Color.rgb(51, 51, 51));
                    eyeglassTableLayout.addView(divLine);
                }
                 ****/

                count++;
                if (count >= displayNumRow)
                    break;
            }

            pdTblRow = new TableRow(thisContext);
            pdTblRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            TextView pdTextView = new TextView(thisContext);
            if (SingletonDataHolder.pupillaryDistance > 0)
                if (SingletonDataHolder.lang.equals("zh"))
                    pdTextView.setText("瞳距: " + Integer.toString(SingletonDataHolder.pupillaryDistance));
                else
                    pdTextView.setText("PD: " + Integer.toString(SingletonDataHolder.pupillaryDistance));
            else
                pdTextView.setText("PD: --");
            pdTextView.setTextColor(Color.BLACK);
            pdTextView.setTextSize(16);
            TableRow.LayoutParams pdTextViewParams = new TableRow.LayoutParams();
            pdTextViewParams.column = 0;
            // pdTextViewParams.span = 4;
            pdTextViewParams.gravity = Gravity.LEFT;
            pdTextViewParams.leftMargin = 60;
            pdTextViewParams.topMargin = 10;
            pdTextViewParams.bottomMargin = 10;
            pdTblRow.addView(pdTextView, pdTextViewParams);

            TextView nvAddTextView = new TextView(thisContext);
            if (SingletonDataHolder.nvadd <= 0)
                if (SingletonDataHolder.lang.equals("zh"))
                    nvAddTextView.setText("下加光: 0");
                else
                    nvAddTextView.setText("NVADD: 0");
            else
                if (SingletonDataHolder.lang.equals("zh"))
                    nvAddTextView.setText("下加光: " + String.format("+%.2f",(SingletonDataHolder.nvadd)));
                else
                    nvAddTextView.setText("NVADD: " + String.format("+%.2f",(SingletonDataHolder.nvadd)));
            nvAddTextView.setTextColor(Color.BLACK);
            nvAddTextView.setTextSize(16);
            TableRow.LayoutParams nvAddTextViewParams = new TableRow.LayoutParams();
            nvAddTextViewParams.column = 2;
            // nvAddTextViewParams.leftMargin = 60;
            nvAddTextViewParams.topMargin = 10;
            nvAddTextViewParams.bottomMargin = 10;
            nvAddTextViewParams.gravity = Gravity.LEFT;
            pdTblRow.addView(nvAddTextView, nvAddTextViewParams);

            eyeglassTableLayout.addView(pdTblRow);
        }
    }

    private void GetNewEyeglassNumber() {

        String url = Constants.UrlPurchaseEyeglassNumber;
        NetConnection conn = new NetConnection();
        if (conn.isConnected(thisContext)) {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);
            RequestQueue queue = Volley.newRequestQueue(thisContext);
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Log.i(TAG, response);
                    Toast.makeText(thisContext, "Succeeded", Toast.LENGTH_LONG).show();
                    GetDahsboardInfo();
                    // loadData();
                    // loadEyeglassNumber();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // showProgress(false);
                    Log.d("Error.Response", error.toString());
                    Toast.makeText(thisContext, "Operation failed, please try it again", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String authString = "Bearer " + SingletonDataHolder.token;
                    headers.put("Content-Type", "application/json;charset=UTF-8");
                    headers.put("Authorization", authString);
                    Log.i("$$$---HEADER---$$$", headers.toString());
                    return headers;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(Constants.NETCONN_TIMEOUT_VALUE, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            queue.add(postRequest);
        } else
            Toast.makeText(thisContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

}
