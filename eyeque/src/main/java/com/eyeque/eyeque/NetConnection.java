package com.eyeque.eyeque;

/**
 * Created by georgez on 3/2/16.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * The type Net connection.
 */
public class NetConnection {

    /**
     * The constant NETCONN_TIMEOUT.
     */
    public static int NETCONN_TIMEOUT = 5000;

    /**
     * Is connected boolean.
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}
