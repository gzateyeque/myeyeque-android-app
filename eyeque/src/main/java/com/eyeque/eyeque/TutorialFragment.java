package com.eyeque.eyeque;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
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
import com.eyeque.eyeque.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AttachDeviceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // private PatternView patternView;
    private static int deviceId;
    private PatternView patternView;
    private static View rootView;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TutorialFragment() {
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
    public static TutorialFragment newInstance(String param1, String param2) {
        TutorialFragment fragment = new TutorialFragment();
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
        rootView = inflater.inflate(R.layout.fragment_tutorial, container, false);

        // Draw the device mounting line. Hard code for now.
        // Need to dynamically populate when supporting multiple devices
        deviceId = 3;

        final TextView avatarNameTv = (TextView) rootView.findViewById(R.id.panelTextView);
        if (SingletonDataHolder.lang.equals("zh"))
            avatarNameTv.setText("欢迎, "
                    + SingletonDataHolder.firstName);
        else
            avatarNameTv.setText("Welcome, "
                    + SingletonDataHolder.firstName);

        Button tutorialButton = (Button) rootView.findViewById(R.id.tutorialButton);
        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Constants.UrlYoutube);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Button playModeButton = (Button) rootView.findViewById(R.id.playModeButton);
        playModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingletonDataHolder.testMode = 1;
                SingletonDataHolder.noDevice = true;
                Intent i = new Intent(getActivity(), PracticeActivity.class);
                i.putExtra("subjectId", 21);
                i.putExtra("deviceId", 3);
                i.putExtra("serverId", 1);
                SingletonDataHolder.screenProtect = 1;
                startActivity(i);
            }
        });

        Button fullTestButton = (Button) rootView.findViewById(R.id.fullTestButton);
        fullTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetUserSubscription();
                SingletonDataHolder.testMode = 0;
                SingletonDataHolder.noDevice = false;
                if (SingletonDataHolder.subscriptionStatus > 1) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    // Setting Dialog Message
                    alertDialog.setTitle("All Access Membership required");
                    alertDialog.setMessage("You can purchase the $" + SingletonDataHolder.subscriptionAnnualPrice
                            + " annual All Access Membership from the EyeQue store. "
                            + "As an EyeQue All Access Member you will be able to take vision tests, "
                            + "generate new Eyeglass Numbers, receive discounts and promotions, as well as other benefits "
                            + "(see www.eyeque.com/membership for details).\n\nIf you click the Buy button, "
                            + "you will be taken out of the EyeQue PVT 8 app to the EyeQue store.  After purchasing "
                            + "an All Access Membership, you can come back the app to start using the full functionality "
                            + "of the EyeQue PVT app.");
                    // Setting Positive "Buy" Button
                    alertDialog.setPositiveButton("Buy",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri uri =  Uri.parse(SingletonDataHolder.subscriptionBuyLink
                                            + "&token=" + SingletonDataHolder.token);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            });
                    // Setting Negative "Cancel" Button
                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    // Showing Alert Dialog
                    alertDialog.show();
                } else {
                    // Intent i = new Intent(getActivity(), AttachDeviceActivity.class);
                    // startActivity(i);
                    if (SingletonDataHolder.correctDisplaySetting) {
                        SingletonDataHolder.testMode = 0;
                        Intent i = new Intent(getActivity(), AttachDeviceActivity.class);
                        startActivity(i);
                    } else
                        showDisplaySettingAlert();
                }
            }
        });

        // Get the subscription dataset from the backend
        GetBuySubscriptionData();
        GetUserSubscription();

        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_dashboard, container, false);
        return rootView;
    }

    public void showDisplaySettingAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("The test requires the display is in maximum resolution. Please go to settings and change the display resolution to maximum and try it again.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void GetBuySubscriptionData() {

        NetConnection conn = new NetConnection();
        if (conn.isConnected(getActivity())) {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            final String url = Constants.UrlBuySubscription;
            final JSONObject params = new JSONObject();

            StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String string) {
                    // Parse response
                    try {
                        // String sphericalStep;
                        Log.i("*** GetBuySubs ***", string);
                        JSONObject jsonObj = new JSONObject(string);
                        SingletonDataHolder.subscriptionAnnualPrice = jsonObj.getString("price");
                        SingletonDataHolder.subscriptionBuyLink = jsonObj.getString("url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Toast.makeText(getActivity(), "Subscription Parse Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.toString());
                    SingletonDataHolder.deviceApiRespData = error.toString();
                    // Toast.makeText(getActivity(), "Subscription Parse Error", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
    }

    public void GetUserSubscription() {

        NetConnection conn = new NetConnection();
        if (conn.isConnected(getActivity())) {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);

            RequestQueue queue = Volley.newRequestQueue(getActivity());
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
                    Toast.makeText(getActivity(), "Subscription Parse Error", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onTutorialFragmentInteraction(uri);
        }
        Log.i("***** 1 ********", "hhehwjrwjrewj");
    }

    @Override
    public void onResume() {
        super.onResume();
        GetUserSubscription();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GetUserSubscription();
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTutorialFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.i("***** deATTACH ********", "hhehwjrwjrewj");
    }

    @Override
    public void onPause() {
        super.onPause();
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
        void onTutorialFragmentInteraction(Uri uri);
    }

}
