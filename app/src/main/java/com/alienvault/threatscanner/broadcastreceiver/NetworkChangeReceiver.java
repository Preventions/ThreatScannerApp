package com.alienvault.threatscanner.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.alienvault.threatscanner.service.OtxQueryService;
import com.alienvault.threatscanner.utility.Utility;

/**
 * Created by hbaxamoosa on 10/10/16.
 */

/**
 * Android 7.0 Nougat contains a number of optimizations around background processing, intending to
 * limit the amount of memory thrashing caused when many apps listen for the same implicit broadcast
 * via manifest registered receivers, leading to poor overall system performance when the device is
 * switching networks or when a picture or video was just captured. When targeting API 24, you’ll no
 * longer receive CONNECTIVITY_CHANGE broadcasts to manifest registered receivers (although runtime
 * receivers will continue to work as your process is already in memory) - consider using
 * JobScheduler to listen for network change events. For all apps, regardless of whether they target
 * API 24 or not, you’ll no longer be able to send or receive NEW_PICTURE or NEW_VIDEO broadcasts,
 * usually sent immediately after taking a new picture or new video, respectively. Instead, use
 * JobScheduler’s new ability to trigger based on content URI changes to kick off your job. Read the
 * documentation for all the details: https://goo.gl/37QRQz
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String status = Utility.getConnectivityStatusString(context);

        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();

        // use this to start and trigger a service
        Intent i = new Intent(context, OtxQueryService.class);
        // potentially add data to the intent
        i.putExtra("KEY1", "Value to be used by the service");
        context.startService(i);
    }
}