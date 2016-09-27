package com.alienvault.threatscanner.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import timber.log.Timber;

/**
 * Created by hbaxamoosa on 9/23/16.
 */

public class Utility {

    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    public static boolean isNetworkAvailable(Context c) {

        /*if (BuildConfig.DEBUG) {
            Timber.v("isNetworkAvailable(Context c)");
        }*/

        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();
        Timber.v(ni.getExtraInfo());
        // TODO: 9/27/16  switch this to check for TYPE_WIFI
        if (ni != null && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
            Timber.v("on wifi");
            return true;
        }
        return false;
    }
}
