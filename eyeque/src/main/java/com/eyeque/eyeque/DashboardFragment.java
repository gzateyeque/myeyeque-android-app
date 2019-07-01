package com.eyeque.eyeque;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.Time;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eyeque.eyeque.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Locale;
import java.text.ParseException;

/**
 *
 * File:            DashboardFragment.java
 * Description:     Dashboard fragment to display user's test progress, lastest EyeGlass Number,
 *                  vision snapshot and historical tracking chart
 * Created:         2016/07/17
 * Author:          George Zhao
 *
 * Copyright (c) 2017 EyeQue Corp
 */
public class DashboardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Context thisContext;
    private static final String TAG = DashboardFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
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
    private TableRow pdTblRow;
    private TableRow gradeTblRow;
    private TableRow dateTblRow;
    private TableRow headerTableRow;
    private TableRow odTableRow;
    private TableRow osTableRow;
    private Boolean eyeglassNumListToggle = false;
    private OnFragmentInteractionListener mListener;
    private TextView avatarNameTv;
    private TextView confOdTv;
    private TextView confOsTv;
    private Button buyEyeglassesButton;
    private Button emailEyeglassNumberResult;
    private TextView pieTv;
    private TextView pieDescTv;
    private ImageView pieStateIv;
    private ImageView egnInfoIv;

    public DashboardFragment() {
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
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
        int color;

        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        SingletonDataHolder.lang = Locale.getDefault().getLanguage();

        thisContext = getActivity().getApplicationContext();

        /***
        final TextView eyeglassTitleTv = (TextView) rootView.findViewById(R.id.visionRecordTitle);
        eyeglassTitleTv.setText("EYEGLASS NUMBER ("
                                + SingletonDataHolder.firstName
                                + " "
                                + SingletonDataHolder.lastName
                                + ")");
         ***/

        avatarNameTv = (TextView) rootView.findViewById(R.id.avatarName);
        pieTv = (TextView) rootView.findViewById(R.id.pieTextView);
        pieDescTv = (TextView) rootView.findViewById(R.id.pieDescTextView);
        pieStateIv = (ImageView) rootView.findViewById(R.id.pieStateImageView);
        egnInfoIv = (ImageView) rootView.findViewById(R.id.egnInfoImageView);
        egnInfoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(Constants.UrlEngInfo);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Log.i("****** LANG ******", SingletonDataHolder.lang);
        if (SingletonDataHolder.lang.equals("zh"))
            avatarNameTv.setText("欢迎, "
                    + SingletonDataHolder.firstName);
        else
            avatarNameTv.setText("Welcome, "
                + SingletonDataHolder.firstName);


        // Progress bar
        scoreLayout = (LinearLayout) rootView.findViewById(R.id.egPanelLayout);
        /***
        scoreTv = (TextView) rootView.findViewById(R.id.scoreText);
        eyeglassNumDescTv = (TextView) rootView.findViewById(R.id.eyeglassNumberDescription);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progessSeekBar);
        newEyeglassNumberBtn = (Button) rootView.findViewById(R.id.newEyeglassNumber);
         eyeglassNumListExpandIv = (ImageButton) rootView.findViewById(R.id.expandButton);
         ***/
        eyeglassTableLayout=(TableLayout) rootView.findViewById(R.id.resultTableLayout);
        // confOdTv = (TextView) rootView.findViewById(R.id.confidenceLevelOd);
        // confOsTv = (TextView) rootView.findViewById(R.id.confidenceLevelOs);
        buyEyeglassesButton = (Button) rootView.findViewById(R.id.buyEyeglassesButton);
        emailEyeglassNumberResult = (Button) rootView.findViewById(R.id.emailEyeglassNumberButton);
        buyEyeglassesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DEBUG: SingletonDataHolder.confLevelOd = "Low";
                if ((SingletonDataHolder.confLevelOd.equals("High") || SingletonDataHolder.confLevelOd.equals("Good"))
                    && (SingletonDataHolder.confLevelOs.equals("High") || SingletonDataHolder.confLevelOs.equals("Good"))) {
                    // DEBUG Pupillary Distance
                    // SingletonDataHolder.pupillaryDistance = 0;
                    if (SingletonDataHolder.pupillaryDistance == 0) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                getActivity());

                        // Setting Dialog Title
                        alertDialog.setTitle("Information");
                        // Setting Dialog Message
                        alertDialog.setMessage("Make sure you have your PD (pupillary distance) measurement to order glasses.");
                        // Setting  "Yes" Btn
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Uri uri = Uri.parse(Constants.UrlBuyEyeglasses);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        });
                        alertDialog.show();
                    } else {
                        Uri uri = Uri.parse(Constants.UrlBuyEyeglasses);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            getActivity());

                    // Setting Dialog Title
                    alertDialog.setTitle("Warning");
                    // Setting Dialog Message
                    alertDialog.setMessage("Your results are not consistent enough to order glasses.\n\nConnect with Customer Support for further information.");
                    // Setting  "Yes" Btn
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    // Setting Negative "NO" Btn
                    alertDialog.setNegativeButton("Contact Customer Service",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    String emailText = "";
                                    Intent i = new Intent(Intent.ACTION_SEND);
                                    i.setType("message/rfc822");
                                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@eyeque.com"});
                                    i.putExtra(Intent.EXTRA_SUBJECT, "Customer Service EGN Review");
                                    emailText += "I'd like to be contacted about my EyeQue EyeGlass Numbers...";
                                    i.putExtra(Intent.EXTRA_TEXT, emailText);
                                    try {
                                        startActivity(Intent.createChooser(i, "Send mail..."));
                                    } catch (android.content.ActivityNotFoundException ex) {
                                        Toast.makeText(getActivity().getApplication(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                    alertDialog.show();
                }
            }
        });

        emailEyeglassNumberResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText;
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_SUBJECT, "My EyeQue EyeGlass Numbers");
                emailText = "\n\n";
                emailText = "                 " + SingletonDataHolder.egnDate + "\n\n";
                emailText += "                    SPHERICAL CYLINDRICAL AXIS\n";
                emailText += "O.D. (RIGHT)   ";
                if (SingletonDataHolder.eyeglassNumberList[0].odSph > 0)
                    emailText += String.format("+%.2f", SingletonDataHolder.eyeglassNumberList[0].odSph) + "              ";
                else
                    emailText += String.format("%.2f", SingletonDataHolder.eyeglassNumberList[0].odSph) + "               ";
                if (SingletonDataHolder.eyeglassNumberList[0].odCyl == 0.00)
                    emailText += "  --            ";
                else
                    emailText += String.format("%.2f", SingletonDataHolder.eyeglassNumberList[0].odCyl) + "             ";
                if (SingletonDataHolder.eyeglassNumberList[0].odCyl == 0.00)
                    emailText += "    --\n";
                else
                    emailText += Integer.toString(SingletonDataHolder.eyeglassNumberList[0].odAxis) + "\n";
                emailText +=  "O.S. (LEFT)     ";
                if (SingletonDataHolder.eyeglassNumberList[0].osSph > 0)
                    emailText += String.format("+%.2f", SingletonDataHolder.eyeglassNumberList[0].osSph) + "             ";
                else
                    emailText += String.format("%.2f", SingletonDataHolder.eyeglassNumberList[0].osSph) + "             ";
                if (SingletonDataHolder.eyeglassNumberList[0].osCyl == 0.00)
                    emailText += "  --               ";
                else
                    emailText += String.format("%.2f", SingletonDataHolder.eyeglassNumberList[0].osCyl) + "            ";
                if (SingletonDataHolder.eyeglassNumberList[0].osCyl == 0.00)
                    emailText += "    --\n\n";
                else
                    emailText += Integer.toString(SingletonDataHolder.eyeglassNumberList[0].osAxis) + "\n\n";
                emailText += "PD: " + Double.toString(SingletonDataHolder.pupillaryDistance);
                emailText += "            NVADD: " + String.format("+%.2f",(SingletonDataHolder.nvadd)) + "\n\n";
                emailText += "EGNs can be used to share with eye care provider and/or used to order glasses online.\n\n";
                i.putExtra(Intent.EXTRA_TEXT, emailText);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity().getApplication(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        GetDahsboardSummary();
        GetUserSubscription();

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
        if (SingletonDataHolder.showDashboardAppRating) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    context);

            // Setting Dialog Title
            alertDialog.setTitle("Rate Our App");
            // Setting Dialog Message
            alertDialog.setMessage("Enjoying EyeQue PVT app? you can rate it in the App Store.");
            // Setting  "Yes" Btn
            alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            // Setting Negative "NO" Btn
            alertDialog.setNegativeButton("Rate the App",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Uri uri =  Uri.parse(Constants.UrlRateApp);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
            alertDialog.show();
            SingletonDataHolder.showDashboardAppRating = false;
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

                        // SingletonDataHolder.urlOdTracking = result.getString("url_spheod");
                        // SingletonDataHolder.urlOSTracking = result.getString("url_spheos");
                        SingletonDataHolder.urlVisionSummary = result.getString("url_vision_summary");
                        SingletonDataHolder.urlVisionSummary = SingletonDataHolder.urlVisionSummary.replace("diagram", "piechart");
                        SingletonDataHolder.pupillaryDistance = eyeglassResult.getDouble("pd");
                        SingletonDataHolder.nvadd = eyeglassResult.getDouble("nvadd");
                        SingletonDataHolder.currentTestScore = eyeglassResult.getInt("score");
                        SingletonDataHolder.freetrial = eyeglassResult.getInt("freetrial");
                        SingletonDataHolder.eyeglassNumPurchasable = eyeglassResult.getBoolean("purchasable");
                        SingletonDataHolder.eyeglassNumCount = eyeglassNumList.length();

                        Log.i("*****PD******", Double.toString(SingletonDataHolder.pupillaryDistance));
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
                        if (SingletonDataHolder.numOfTests >= 3) {
                            confOdTv.setText("Confidence: " + SingletonDataHolder.confLevelOd);
                            confOsTv.setText("Confidence: " + SingletonDataHolder.confLevelOs);
                            visionSummarydWebView.clearCache(true);
                            visionSummarydWebView.loadUrl(SingletonDataHolder.urlVisionSummary);
                        }

                        /*** v1.4 removed tracking
                        trackingDataOdWebView.loadUrl(SingletonDataHolder.urlOdTracking);
                        trackingDataOsWebView.loadUrl(SingletonDataHolder.urlOSTracking);
                         ***/
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
            if (SingletonDataHolder.numOfTests >= 3)
                visionSummarydWebView.loadUrl(SingletonDataHolder.urlVisionSummary);
            /*** v1.4 removed tracking
            trackingDataOdWebView.loadUrl(SingletonDataHolder.urlOdTracking);
            trackingDataOsWebView.loadUrl(SingletonDataHolder.urlOSTracking);
             ***/
        }
    }

    // Version 1.4
    private void GetDahsboardSummary() {

        String url = Constants.UrlDashboardSummary;
        NetConnection conn = new NetConnection();
        if (conn.isConnected(thisContext)) {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);
            RequestQueue queue = Volley.newRequestQueue(thisContext);

            final JSONObject params = new JSONObject();
            final JSONArray eyeglassNumArray = new JSONArray();

            StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("*** DashSummary ***", response);
                    SingletonDataHolder.dahsboardApiRespData = response;
                    try {
                        final JSONObject result = new JSONObject(response);

                        // SingletonDataHolder.urlOdTracking = result.getString("url_spheod");
                        // SingletonDataHolder.urlOSTracking = result.getString("url_spheos");
                        SingletonDataHolder.urlVisionSummary = result.getString("piechart_url");

                        SingletonDataHolder.numOfTests = result.getInt("no_tests");
                        if (result.isNull("first_test_at"))
                            SingletonDataHolder.firstTestDate = "";
                        else
                            SingletonDataHolder.firstTestDate = result.getString("first_test_at");
                        if (SingletonDataHolder.numOfTests > 0) {
                            SingletonDataHolder.pupillaryDistance = result.getDouble("pd");
                            SingletonDataHolder.nvadd = result.getDouble("nvadd");
                            SingletonDataHolder.confLevelOd = result.getString("grade_od");
                            SingletonDataHolder.confLevelOs = result.getString("grade_os");
                            SingletonDataHolder.eyeglassNumCount = 1;
                            SingletonDataHolder.eyeglassNumberList = new SingletonDataHolder.EyeglassNumber[SingletonDataHolder.eyeglassNumCount];
                            SingletonDataHolder.eyeglassNumberList[0] = new SingletonDataHolder.EyeglassNumber(
                                    result.getDouble("sph_od"),
                                    result.getDouble("cyl_od"),
                                    result.getInt("axis_od"),
                                    result.getDouble("sph_os"),
                                    result.getDouble("cyl_os"),
                                    result.getInt("axis_os"),
                                    result.getString("updated_at"));
                        } else
                            SingletonDataHolder.eyeglassNumCount = 0;
                        // loadData();
                        loadEyeglassNumber();
                        /***
                        if (SingletonDataHolder.numOfTests >= 3)  {
                            confOdTv.setText("Confidence: " + SingletonDataHolder.confLevelOd);
                            confOsTv.setText("Confidence: " + SingletonDataHolder.confLevelOs);
                            visionSummarydWebView.clearCache(true);
                            visionSummarydWebView.loadUrl(SingletonDataHolder.urlVisionSummary);
                        }
                         ***/
                        /*** v1.4 removed tracking
                         trackingDataOdWebView.loadUrl(SingletonDataHolder.urlOdTracking);
                         trackingDataOsWebView.loadUrl(SingletonDataHolder.urlOSTracking);
                         ***/
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
            getRequest.setRetryPolicy(policy);
            queue.add(getRequest);
        }
        else {
            // loadData();
            loadEyeglassNumber();

            /*** Removed in v1.6
            if (SingletonDataHolder.numOfTests >= 3) {
                confOdTv.setText("Confidence: " + SingletonDataHolder.confLevelOd);
                confOsTv.setText("Confidence: " + SingletonDataHolder.confLevelOs);
                visionSummarydWebView.clearCache(true);
                visionSummarydWebView.loadUrl(SingletonDataHolder.urlVisionSummary);
            }
             ***/
            /*** v1.4 removed tracking
             trackingDataOdWebView.loadUrl(SingletonDataHolder.urlOdTracking);
             trackingDataOsWebView.loadUrl(SingletonDataHolder.urlOSTracking);
             ***/
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
            eyeglassNumDescTv.setText("Start testing your vision and get your EyeGlass Numbers");
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

    public static Calendar getDatePart(Date date){
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

        return cal;                                  // return the date part
    }

    /**
     * This method also assumes endDate >= startDate
     **/
    public static long daysBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);

        long daysBetween = 0;
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        Log.i("***NumOfDays***", Long.toString(daysBetween));
        return daysBetween;
    }

    private void loadEyeglassNumber() {
        int displayNumRow;

        String avatarString = "Welcome, " + SingletonDataHolder.firstName;
        avatarNameTv.setTextSize(18);

        buyEyeglassesButton.setVisibility(View.INVISIBLE);
        emailEyeglassNumberResult.setVisibility(View.INVISIBLE);
        pieTv.setVisibility(View.INVISIBLE);
        // DEBUG F Users
        // SingletonDataHolder.confLevelOd = "Poor";
        if ((SingletonDataHolder.confLevelOd.equals("High") || SingletonDataHolder.confLevelOd.equals("Good"))
                && (SingletonDataHolder.confLevelOs.equals("High") || SingletonDataHolder.confLevelOs.equals("Good")))
            buyEyeglassesButton.setBackgroundColor(thisContext.getResources().getColor(R.color.colorLtGreen));
        else
            buyEyeglassesButton.setBackgroundColor(Color.BLACK);

        // DEBUG : try with different numbers of tests
        if (SingletonDataHolder.numOfTests == 0) {
            if (SingletonDataHolder.firstTestDate != "") {
                try {
                    SimpleDateFormat simpleDateFormat;
                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date firstTestDate = simpleDateFormat.parse(SingletonDataHolder.firstTestDate);

                    Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
                    Date currentTime = localCalendar.getTime();
                    long days = daysBetween(firstTestDate, currentTime);
                    Log.i("*** Num of Days ***", Long.toString(days));
                    if (days > 180) {
                        // avatarNameTv.setTextSize(14);
                        SingletonDataHolder.recurringTestAfterValidPeriod = true;
                        avatarString = getResources().getString(R.string.avatarInSixMonthTest);
                        avatarString = avatarString.replace("#name", SingletonDataHolder.firstName);
                        pieDescTv.setText(R.string.pieDescNewStart);
                        pieStateIv.setImageResource(R.drawable.pie_state_none);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                avatarString = thisContext.getResources().getString(R.string.avatarNewStart);
                avatarString = avatarString.replace("#name", SingletonDataHolder.firstName);
                pieDescTv.setText(R.string.pieDescNewStart);
                pieStateIv.setImageResource(R.drawable.pie_state_none);
            }
        } else if (SingletonDataHolder.numOfTests == 1) {
            avatarString = thisContext.getResources().getString(R.string.avatarFirstTest);
            avatarString = avatarString.replace("#name", SingletonDataHolder.firstName);
            pieDescTv.setText(R.string.pieDescFirstTest);
            pieStateIv.setImageResource(R.drawable.pie_state_1st);
        } else if (SingletonDataHolder.numOfTests == 2) {
            avatarString = thisContext.getResources().getString(R.string.avatarSecondTest);
            avatarString = avatarString.replace("#name", SingletonDataHolder.firstName);
            pieDescTv.setText(R.string.pieDescSecondTest);
            pieStateIv.setImageResource(R.drawable.pie_state_2nd);
        } else if (SingletonDataHolder.numOfTests == 3) {
            buyEyeglassesButton.setVisibility(View.VISIBLE);
            emailEyeglassNumberResult.setVisibility(View.VISIBLE);
            if (SingletonDataHolder.confLevelOd.equals("Poor") || SingletonDataHolder.confLevelOs.equals("Poor")) {
                avatarString = thisContext.getResources().getString(R.string.avatarOngoingTestForFUsers);
                avatarString = avatarString.replace("#name", SingletonDataHolder.firstName);
                avatarString = avatarString.replace("#numOfTests", Integer.toString(SingletonDataHolder.numOfTests));
                pieDescTv.setText(R.string.pieDescOngoingTestForFUsers);
            }
            else {
                avatarString = thisContext.getResources().getString(R.string.avatarThirdTest);
                avatarString = avatarString.replace("#name", SingletonDataHolder.firstName);
                pieDescTv.setText(R.string.pieDescThirdTest);
            }
            pieStateIv.setImageResource(R.drawable.pie_state_3rd);
        } else if (SingletonDataHolder.numOfTests > 3) {
            if ((SingletonDataHolder.confLevelOd.equals("Poor") || SingletonDataHolder.confLevelOs.equals("Poor"))) {
                avatarString = thisContext.getResources().getString(R.string.avatarOngoingTestForFUsers);
                avatarString = avatarString.replace("#name", SingletonDataHolder.firstName);
                avatarString = avatarString.replace("#numOfTests", Integer.toString(SingletonDataHolder.numOfTests));
                pieDescTv.setText(R.string.pieDescOngoingTestForFUsers);
            } else {
                avatarString = thisContext.getResources().getString(R.string.avatarOngoingTest);
                avatarString = avatarString.replace("#name", SingletonDataHolder.firstName);
                avatarString = avatarString.replace("#numOfTests", Integer.toString(SingletonDataHolder.numOfTests));
                pieDescTv.setText(R.string.pieDescOngoingTest);
            }
            buyEyeglassesButton.setVisibility(View.VISIBLE);
            emailEyeglassNumberResult.setVisibility(View.VISIBLE);
            pieTv.setVisibility(View.VISIBLE);
            pieTv.setText(Integer.toString(SingletonDataHolder.numOfTests));
            pieStateIv.setImageResource(R.drawable.pie_ongoing);
        }
        avatarNameTv.setText(avatarString);

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
            eyeglassTableLayout.removeView(headerTableRow);
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

        if (SingletonDataHolder.numOfTests >= 3) {

            /**** version 1.4 : no divider line
            TextView divLine = new TextView(thisContext);
            divLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
            divLine.setBackgroundColor(Color.rgb(51, 51, 51));
            eyeglassTableLayout.addView(divLine);
             ***/
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
                    Log.i("*** EGNDate ***", SingletonDataHolder.eyeglassNumberList[i].createdAt);
                    /*** Old format
                    SimpleDateFormat simpleDateFormat;
                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Calendar.getInstance().getTimeZone().getID()));
                    Date myDate = simpleDateFormat.parse(SingletonDataHolder.eyeglassNumberList[i].createdAt);
                     ***/

                    // dataTextView.setText(myDate.toString());
                    String dateStr = changeDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                            "yyyy-MM-dd hh:mm a", SingletonDataHolder.eyeglassNumberList[i].createdAt);
                    Log.i("*** TZTZTZ ***", dateStr);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH);
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = df.parse(dateStr);
                    df.setTimeZone(TimeZone.getDefault());
                    SingletonDataHolder.egnDate = df.format(date);
                    if (SingletonDataHolder.confLevelOd.equals("Poor") || SingletonDataHolder.confLevelOs.equals("Poor"))
                        dataTextView.setTextColor(Color.LTGRAY);
                    else
                        dataTextView.setTextColor(Color.BLACK);
                    dataTextView.setText(SingletonDataHolder.egnDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (SingletonDataHolder.confLevelOd.equals("Poor") || SingletonDataHolder.confLevelOs.equals("Poor"))
                    dataTextView.setTextColor(Color.LTGRAY);
                else
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
                headerTableRow = new TableRow(thisContext);
                headerTableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                TextView sphTextView = new TextView(thisContext);
                sphTextView.setTextColor(Color.BLACK);
                sphTextView.setGravity(1);
                if (SingletonDataHolder.lang.equals("zh"))
                    sphTextView.setText("球镜");
                else
                    sphTextView.setText("SPHERICAL");
                if (SingletonDataHolder.confLevelOd.equals("Poor") || SingletonDataHolder.confLevelOs.equals("Poor"))
                    sphTextView.setTextColor(Color.LTGRAY);
                else
                    sphTextView.setTextColor(Color.BLACK);
                sphTextView.setTypeface(null, Typeface.BOLD);
                TableRow.LayoutParams sphTextViewParams = new TableRow.LayoutParams();
                sphTextViewParams.width = LayoutParams.MATCH_PARENT;
                sphTextViewParams.height = LayoutParams.WRAP_CONTENT;
                sphTextViewParams.column = 1;
                sphTextViewParams.gravity = Gravity.CENTER;
                headerTableRow.addView(sphTextView, sphTextViewParams);

                TextView cylTextView = new TextView(thisContext);
                cylTextView.setGravity(1);
                if (SingletonDataHolder.lang.equals("zh"))
                    cylTextView.setText("球镜");
                else
                    cylTextView.setText("CYLINDRICAL");
                cylTextView.setLayoutParams(new TableRow.LayoutParams(2));
                if (SingletonDataHolder.confLevelOd.equals("Poor") || SingletonDataHolder.confLevelOs.equals("Poor"))
                    cylTextView.setTextColor(Color.LTGRAY);
                else
                    cylTextView.setTextColor(Color.BLACK);
                cylTextView.setTypeface(null, Typeface.BOLD);
                headerTableRow.addView(cylTextView);

                TextView axisTextView = new TextView(thisContext);
                axisTextView.setGravity(1);
                axisTextView.setPadding(0, 0, 60, 0);
                if (SingletonDataHolder.lang.equals("zh"))
                    axisTextView.setText("轴位");
                else
                    axisTextView.setText("AXIS");
                axisTextView.setLayoutParams(new TableRow.LayoutParams(3));
                if (SingletonDataHolder.confLevelOd.equals("Poor") || SingletonDataHolder.confLevelOs.equals("Poor"))
                    axisTextView.setTextColor(Color.LTGRAY);
                else
                    axisTextView.setTextColor(Color.BLACK);
                axisTextView.setTypeface(null, Typeface.BOLD);
                headerTableRow.addView(axisTextView);
                eyeglassTableLayout.addView(headerTableRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

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
                if (SingletonDataHolder.confLevelOd.equals("Poor"))
                    odHeaderTextView.setTextColor(Color.LTGRAY);
                else
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
                odSphTextView.setGravity(1);
                if (SingletonDataHolder.eyeglassNumberList[i].odSph > 0)
                    odSphTextView.setText(String.format("+%.2f", SingletonDataHolder.eyeglassNumberList[i].odSph));
                else
                    odSphTextView.setText(String.format("%.2f", SingletonDataHolder.eyeglassNumberList[i].odSph));
                if (SingletonDataHolder.confLevelOd.equals("Poor"))
                    odSphTextView.setTextColor(Color.LTGRAY);
                else
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
                if (SingletonDataHolder.eyeglassNumberList[i].odCyl == 0.00)
                    odCylTextView.setText("--");
                else
                    odCylTextView.setText(String.format("%.2f", SingletonDataHolder.eyeglassNumberList[i].odCyl));
                odCylTextView.setLayoutParams(new TableRow.LayoutParams(2));
                odCylTextView.setTextSize(18);
                if (SingletonDataHolder.confLevelOd.equals("Poor"))
                    odCylTextView.setTextColor(Color.LTGRAY);
                else
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
                odAxisTextView.setPadding(0, 0, 60, 0);
                // odAxisTextView.setGravity(1);
                Log.i("***--- AXIS ---***", Integer.toString(SingletonDataHolder.eyeglassNumberList[i].odAxis));
                if (SingletonDataHolder.eyeglassNumberList[i].odCyl == 0.00)
                    odAxisTextView.setText("--");
                else
                    odAxisTextView.setText(Integer.toString(SingletonDataHolder.eyeglassNumberList[i].odAxis));
                odAxisTextView.setLayoutParams(new TableRow.LayoutParams(3));
                odAxisTextView.setTextSize(18);
                if (SingletonDataHolder.confLevelOd.equals("Poor"))
                    odAxisTextView.setTextColor(Color.LTGRAY);
                else
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
                if (SingletonDataHolder.confLevelOs.equals("Poor"))
                    osHeaderTextView.setTextColor(Color.LTGRAY);
                else
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
                if (SingletonDataHolder.confLevelOs.equals("Poor"))
                    osSphTextView.setTextColor(Color.LTGRAY);
                else
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
                if (SingletonDataHolder.eyeglassNumberList[i].osCyl == 0.00)
                    osCylTextView.setText("--");
                else
                    osCylTextView.setText(String.format("%.2f", SingletonDataHolder.eyeglassNumberList[i].osCyl));
                osCylTextView.setLayoutParams(new TableRow.LayoutParams(2));
                if (SingletonDataHolder.confLevelOs.equals("Poor"))
                    osCylTextView.setTextColor(Color.LTGRAY);
                else
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
                osAxisTextView.setPadding(0, 0, 60, 0);
                osAxisTextView.setGravity(1);
                if (SingletonDataHolder.eyeglassNumberList[i].osCyl == 0.00)
                    osAxisTextView.setText("--");
                else
                    osAxisTextView.setText(Integer.toString(SingletonDataHolder.eyeglassNumberList[i].osAxis));
                osAxisTextView.setLayoutParams(new TableRow.LayoutParams(3));
                if (SingletonDataHolder.confLevelOs.equals("Poor"))
                    osAxisTextView.setTextColor(Color.LTGRAY);
                else
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

                /****
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
                    pdTextView.setText("瞳距: " + Double.toString(SingletonDataHolder.pupillaryDistance));
                else
                    pdTextView.setText("PD: " + Double.toString(SingletonDataHolder.pupillaryDistance));
            else
                pdTextView.setText("PD: --");
            if (SingletonDataHolder.confLevelOd.equals("Poor") || SingletonDataHolder.confLevelOs.equals("Poor"))
                pdTextView.setTextColor(Color.LTGRAY);
            else
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
            if (SingletonDataHolder.confLevelOd.equals("Poor") || SingletonDataHolder.confLevelOs.equals("Poor"))
                nvAddTextView.setTextColor(Color.LTGRAY);
            else
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

            /***
            gradeTblRow = new TableRow(thisContext);
            gradeTblRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            TextView gradeOdTextView = new TextView(thisContext);
            if (SingletonDataHolder.lang.equals("zh"))
                gradeOdTextView.setText("稳定指数: " + SingletonDataHolder.gradeOd);
            else
                gradeOdTextView.setText("Conf(OD): " + SingletonDataHolder.gradeOd);
            gradeOdTextView.setTextColor(Color.BLACK);
            gradeOdTextView.setTextSize(16);
            TableRow.LayoutParams gradeOdTextViewParams = new TableRow.LayoutParams();
            gradeOdTextViewParams.column = 0;
            // pdTextViewParams.span = 4;
            gradeOdTextViewParams.gravity = Gravity.LEFT;
            gradeOdTextViewParams.leftMargin = 60;
            gradeOdTextViewParams.topMargin = 10;
            gradeOdTextViewParams.bottomMargin = 10;
            gradeTblRow.addView(gradeOdTextView, gradeOdTextViewParams);

            TextView gradeOsTextView = new TextView(thisContext);
            if (SingletonDataHolder.lang.equals("zh"))
                gradeOsTextView.setText("稳定指数: " + SingletonDataHolder.gradeOs);
            else
                gradeOsTextView.setText("Conf (OS): " + SingletonDataHolder.gradeOs);
            gradeOsTextView.setTextColor(Color.BLACK);
            gradeOsTextView.setTextSize(16);
            TableRow.LayoutParams gradeOsTextViewParams = new TableRow.LayoutParams();
            gradeOsTextViewParams.column = 2;
            // nvAddTextViewParams.leftMargin = 60;
            gradeOsTextViewParams.topMargin = 10;
            gradeOsTextViewParams.bottomMargin = 10;
            gradeOsTextViewParams.gravity = Gravity.LEFT;
            gradeTblRow.addView(gradeOsTextView, gradeOsTextViewParams);

            eyeglassTableLayout.addView(gradeTblRow);
             ***/
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
                    GetDahsboardSummary();
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

    public void GetUserSubscription() {

        NetConnection conn = new NetConnection();
        if (conn.isConnected(thisContext)) {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(thisContext);
            final String url = Constants.UrlUserSubscription;
            final JSONObject params = new JSONObject();

            StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String string) {
                    // Parse response
                    try {
                        // String sphericalStep;
                        Log.i("*** GetBuySubs ***", string);
                        JSONObject jsonObj = new JSONObject(string);
                        SingletonDataHolder.subscriptionStatus = jsonObj.getInt("status");
                        String attrValue = jsonObj.getString("expiration_date");
                        String str = attrValue;
                        String[] strgs = str.split(" ");
                        SingletonDataHolder.subscriptionExpDate = strgs[0];
                        if (SingletonDataHolder.freshLogin) {
                            if (SingletonDataHolder.subscriptionStatus <= 1) {
                                // Show user the subscription dialog
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                // Setting Dialog Message
                                alertDialog.setTitle("Subscription activated");
                                alertDialog.setMessage("Congratulations, you now have an active EyeQue "
                                        + "subscription for one year.  As an EyeQue subscriber you "
                                        + "will be able to take vision tests, generate new EyeGlass Numbers, "
                                        + "receive promotions, and other subscriber "
                                        + "benefits (see www.eyeque.com/subscription for details).");
                                // Setting  "Ok" Button
                                alertDialog.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                // Showing Alert Dialog
                                alertDialog.show();
                            } else {
                                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());
                                // Setting Dialog Message
                                alertDialog2.setTitle("Subscription required");
                                alertDialog2.setMessage("A subscription is required to access all of the "
                                        + "features of the myEyeQue app. The EyeQue annual subscription is "
                                        + "$4.99 per year. As an EyeQue subscriber you will be able to take "
                                        + "vision tests, generate new EyeGlass Numbers, receive discounts and "
                                        + "promotions, as well as other benefits (see www.eyeque.com/subscription "
                                        + "for details).\n\n"
                                        + "If you click the Buy button, you will be taken out of the myEyeQue "
                                        + "app to the EyeQue store.  After purchasing a subscription, you "
                                        + "can come back to the app to use the full functionality of "
                                        + "the myEyeQue app.");
                                // Setting Positive "Buy" Button
                                alertDialog2.setPositiveButton("Buy",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Uri uri = Uri.parse(SingletonDataHolder.subscriptionBuyLink
                                                        + "&token=" + SingletonDataHolder.token);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                            }
                                        });
                                // Setting Negative "Cancel" Button
                                alertDialog2.setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                // Showing Alert Dialog
                                alertDialog2.show();
                            }
                            SingletonDataHolder.freshLogin = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Subscription Parse Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.toString());
                    SingletonDataHolder.deviceApiRespData = error.toString();
                    // Toast.makeText(AgreementActivity.this, "Subscription Parse Error", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    Log.i("JSON data", params.toString());
                    return params.toString().getBytes();
                }

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
            getRequest.setRetryPolicy(policy);
            queue.add(getRequest);
        }
        else
            Toast.makeText(thisContext, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
    }

    private String changeDateFormat(String currentFormat,String requiredFormat,String dateString){
        String result="";
        if (dateString == null || dateString.equals("")){
            return result;
        }
        SimpleDateFormat formatterOld = new SimpleDateFormat(currentFormat, Locale.getDefault());
        SimpleDateFormat formatterNew = new SimpleDateFormat(requiredFormat, Locale.getDefault());

        Date date=null;
        try {
            date = formatterOld.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            result = formatterNew.format(date);
        }
        return result;
    }
}
