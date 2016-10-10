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