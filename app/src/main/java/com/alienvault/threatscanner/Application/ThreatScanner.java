package com.alienvault.threatscanner.application;

import android.app.Application;
import android.content.Context;

import com.alienvault.threatscanner.BuildConfig;
import com.facebook.stetho.Stetho;
import com.firebase.client.Firebase;

import timber.log.Timber;

/**
 * Created by hbaxamoosa on 9/23/16.
 */

public class ThreatScanner extends Application {

    private static Context context;

    public static Context getAppContext() {
        return ThreatScanner.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ThreatScanner.context = getApplicationContext();
        Firebase.setAndroidContext(this);

        //Including Jake Wharton's Timber logging library
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Timber.v("Timber.plant(new Timber.DebugTree());");
        } else {
            // Timber.plant(new CrashReportingTree());
        }

        // Facebook Stetho
        Stetho.initializeWithDefaults(this);
    }
}