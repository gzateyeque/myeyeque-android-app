package com.eyeque.eyeque;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.eyeque.eyeque.AccountActivity;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * File:            PlayModeResultAcvitity.java
 * Description:     The screen gives the result of quick test
 * Created:         2016/10/05
 * Author:          George Zhao
 *
 * Copyright (c) 2016 EyeQue Corp
 */
public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // Database instance
    private static SQLiteDatabase myDb = null;
    private WebView webview;

    // Data container for list view
    ArrayList<String> listItems = new ArrayList<String>();
    // Defining a string adapter whcih will handle the data of the ListView
    ArrayAdapter<String> adapter;
    int clickCounter = 0;  // record how many times the button has been clicked
    String[] items;

    public SettingFragment() {
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
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        final ListView settingListView = (ListView) rootView.findViewById(R.id.settingListView);
        if (SingletonDataHolder.lang.equals("zh")) {
            listItems.add("帐号");
            // listItems.add("订阅");
            listItems.add("购买");
            listItems.add("常见问题");
            listItems.add("博客");
            listItems.add("帮助");
            listItems.add("诊断");
            listItems.add("关于");
        }
        else {
            listItems.add("Account");
            // listItems.add("Subscription");
            listItems.add("Shop");
            listItems.add("FAQ");
            listItems.add("Blog");
            listItems.add("Help");
            listItems.add("Contact Support");
            listItems.add("About");
        }
        adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, listItems);
        settingListView.setAdapter(adapter);

        settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri;
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(getActivity(), AccountActivity.class);
                        startActivity(intent);
                        break;
                    // case 1:
                        // intent = new Intent(getActivity(), SubscriptionActivity.class);
                        // startActivity(intent);
                        // break;
                    case 1:
                        uri = Uri.parse(Constants.UrlBuyDevice);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        break;
                    case 2:
                        uri = Uri.parse(Constants.UrlFaq);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        break;
                    case 3:
                        uri = Uri.parse(Constants.UrlBlog);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        break;
                    case 4:
                        uri = Uri.parse(Constants.UrlSupport);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(getActivity(), DiagnosticsActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(getActivity(), AboutActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });


        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_dashboard, container, false);
        // Check local persistent eyeque.db database

        this.webview = (WebView) rootView.findViewById(R.id.bannerWebView);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        // webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.setVerticalScrollBarEnabled(false);
        webview.getSettings().setUseWideViewPort(true);

        //To disabled the horizontal and vertical scrolling:
        webview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("about:blank");
            }
        });
        NetConnection conn = new NetConnection();
        if (!conn.isConnected(getActivity())) {
            webview.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }
        webview.loadUrl(Constants.UrlBanner);

        Button logoutButton = (Button) rootView.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DatabaseHelper dbHelper = new DatabaseHelper(v.getContext());
                    myDb = dbHelper.getWritableDatabase();
                    Log.d("TAG", "delete toekn successfully");
                    String sql = "delete from " + Constants.USER_ENTITY_TABLE;
                    myDb.execSQL(sql);
                } catch (IOException e) {
                    Log.d("TAG", "open database failed");
                }
                SingletonDataHolder.token = "";
                SingletonDataHolder.newRegUser = true;
                SingletonDataHolder.userId = 0;
                // SingletonDataHolder.email = "";
                SingletonDataHolder.firstName = "";
                SingletonDataHolder.lastName = "";
                SingletonDataHolder.gender = 0;
                SingletonDataHolder.birthYear = 0;
                SingletonDataHolder.subscriptionStatus = false;
                SingletonDataHolder.subscriptionExpDate = "";
                SingletonDataHolder.profileReadingGlassesValue = "";
                getActivity().getFragmentManager().popBackStack();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSettingFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingFragmentInteractionListener");
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
        void onSettingFragmentInteraction(Uri uri);
    }
}
