package com.alienvault.threatscanner.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.alienvault.threatscanner.data.OTXResponsesContract;

import timber.log.Timber;

/**
 * Created by hbaxamoosa on 9/23/16.
 */

public class Utility {

    // These indices are tied to OTXRESPONSES_COLUMNS.  If OTXRESPONSES_COLUMNS changes, these must change.
    public static final int COLUMN_THREAT_SCORE = 0;
    public static final int COLUMN_IP_ADDRESS = 1;
    public static final int COLUMN_OTX_RESPONSE = 2;
    public static final int COLUMN_URL = 3;
    public static final int COLUMN_TYPE = 4;
    public static final String[] OTXRESPONSES_COLUMNS = {
            OTXResponsesContract.OTXResponsesList.COLUMN_THREAT_SCORE,
            OTXResponsesContract.OTXResponsesList.COLUMN_IP_ADDRESS,
            OTXResponsesContract.OTXResponsesList.COLUMN_OTX_RESPONSE,
            OTXResponsesContract.OTXResponsesList.COLUMN_URL,
            OTXResponsesContract.OTXResponsesList.COLUMN_TYPE
    };

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
