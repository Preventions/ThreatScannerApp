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
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_THREAT_SCORE = 1;
    public static final int COLUMN_IP_ADDRESS = 2;
    public static final int COLUMN_OTX_RESPONSE = 3;
    public static final int COLUMN_URL = 4;
    public static final int COLUMN_TYPE = 5;
    public static final String[] OTXRESPONSES_COLUMNS = {
            OTXResponsesContract.OTXResponsesList.COLUMN_ID,
            OTXResponsesContract.OTXResponsesList.COLUMN_THREAT_SCORE,
            OTXResponsesContract.OTXResponsesList.COLUMN_IP_ADDRESS,
            OTXResponsesContract.OTXResponsesList.COLUMN_OTX_RESPONSE,
            OTXResponsesContract.OTXResponsesList.COLUMN_URL,
            OTXResponsesContract.OTXResponsesList.COLUMN_TYPE
    };

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    /**
     * Force non-instantiability: If you do not want an object to be created using the new keyword,
     * enforce it using a private constructor. Especially useful for utility classes that contain
     * only static functions.
     */
    private Utility() {
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = Utility.getConnectivityStatus(context);
        String status = null;
        if (conn == Utility.TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == Utility.TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == Utility.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    public static boolean isNetworkAvailable(Context context) {

        /*if (BuildConfig.DEBUG) {
            Timber.v("isNetworkAvailable(Context c)");
        }*/

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();
        // make sure this is checking for TYPE_WIFI
        if (ni != null && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
            Timber.v(ni.getExtraInfo());
            // Timber.v("on wifi");
            return true;
        }
        return false;
    }
}
