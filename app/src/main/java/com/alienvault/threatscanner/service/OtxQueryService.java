package com.alienvault.threatscanner.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alienvault.threatscanner.network.FetchIpAddress;
import com.alienvault.threatscanner.utility.Utility;

import timber.log.Timber;

/**
 * Created by hbaxamoosa on 10/10/16.
 */


/**
 * For reference, see http://www.vogella.com/tutorials/AndroidTaskScheduling/article.html
 * For reference, see http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
 * For reference, see http://www.vogella.com/tutorials/AndroidServices/article.html
 * For reference, see http://www.vogella.com/tutorials/AndroidBroadcastReceiver/article.html
 */

public class OtxQueryService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.v("onStartCommand(Intent intent, int flags, int startId)");

        if (Utility.isNetworkAvailable(getApplicationContext())) {
            Timber.v("network is available");
            Timber.v("calling network task");
            FetchIpAddress fetchIpAddress = new FetchIpAddress();
            fetchIpAddress.execute();
        } else {
            Toast.makeText(getApplicationContext(), "Not connected to Wifi", Toast.LENGTH_SHORT).show();
            Timber.v("not connected to wifi");
        }

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Timber.v("onBind(Intent intent)");
        Toast.makeText(getApplicationContext(), "OtxQueryService onBind(Intent intent) ", Toast.LENGTH_SHORT).show();
        return null;
    }
}