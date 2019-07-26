package com.alienvault.threatscanner.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alienvault.threatscanner.R;
import com.alienvault.threatscanner.adapter.OTXResponsesAdapter;
import com.alienvault.threatscanner.application.ThreatScanner;
import com.alienvault.threatscanner.data.OTXResponsesContract;
import com.alienvault.threatscanner.network.FetchIpAddress;
import com.alienvault.threatscanner.service.MyJobService;
import com.alienvault.threatscanner.utility.Utility;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    /**
     * Nougat 7.1 static shortcut: https://medium.com/@tonyowen/android-7-1-static-shortcut-6c42d81ba11b#.ym8uchowc
     * and https://www.novoda.com/blog/exploring-android-nougat-7-1-app-shortcuts/
     */

    public static RecyclerView mRecyclerView;
    public static OTXResponsesAdapter mAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String notificationType;
    private RecyclerView.LayoutManager mLayoutManager;
    private ContentResolver mContentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_main); // switch the TextView for a CardView so that it shows a historical audit trail

        // consider adding intro screens to the app - https://medium.com/tangoagency/material-intro-screen-for-android-apps-c4317fbac923#.1zp72ni98

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String token = FirebaseInstanceId.getInstance().getToken();
        Timber.v(token);
        FloatingActionButton fab = findViewById(R.id.fab);

        // TODO implement notification for user, so that the check doesn't have to be triggered via button click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.isNetworkAvailable(getApplicationContext())) {
                    // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Timber.v("calling fetchIpAddress.execute()");
                    FetchIpAddress fetchIpAddress = new FetchIpAddress();
                    fetchIpAddress.execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Not connected to Wifi", Toast.LENGTH_SHORT).show();
                    Timber.v("not connected to wifi");
                }
            }
        });

        /**
         * see https://github.com/firebase/quickstart-android/tree/master/messaging for reference
         * if app was launched from notification, it will have an intent with extra of type:firebase
         */
        Intent intent = getIntent();

        /**
         * good info on notifications here: http://firstround.com/review/what-you-must-know-to-build-savvy-push-notifications/
         */
        if (intent != null) {
            Timber.v("intent != null");
            Bundle extras = intent.getExtras();
            if (extras != null) { // intent has extras
                notificationType = extras.getString("type");
                Timber.v("notificationType: " + notificationType);
                if (notificationType != null && notificationType.equals("firebase")) {

                    Timber.v("type: " + extras.getString("type"));
                    Timber.v("name: " + extras.getString("name"));
                    Timber.v("url: " + extras.getString("url"));

                    // insert values into ContentProvider
                    ContentValues OTXResponse = new ContentValues();
                    OTXResponse.put(OTXResponsesContract.OTXResponsesList.COLUMN_THREAT_SCORE, "N/A");
                    OTXResponse.put(OTXResponsesContract.OTXResponsesList.COLUMN_IP_ADDRESS, "Webcast");
                    OTXResponse.put(OTXResponsesContract.OTXResponsesList.COLUMN_OTX_RESPONSE, extras.getString("name"));
                    OTXResponse.put(OTXResponsesContract.OTXResponsesList.COLUMN_URL, extras.getString("url"));
                    OTXResponse.put(OTXResponsesContract.OTXResponsesList.COLUMN_TYPE, "FCM");

                    mContentResolver = this.getContentResolver();

                    mContentResolver.insert(OTXResponsesContract.OTXResponsesList.CONTENT_URI, OTXResponse);
                    // since a new item has been added, create a new Cursor
                    String selection = null;
                    String[] selectionArgs = null;

                    // create cursor to read OTXResponses stored in the DB
                    Cursor mCursor = mContentResolver.query(OTXResponsesContract.OTXResponsesList.CONTENT_URI, Utility.OTXRESPONSES_COLUMNS, selection, selectionArgs, null);

                    if (mCursor != null) {
                        Timber.v("mCursor.getCount: " + mCursor.getCount());
                        mCursor.moveToFirst();
                    }

                    // swap the adapter for the RecyclerView
                    MainActivity.mRecyclerView.swapAdapter(new OTXResponsesAdapter(ThreatScanner.getAppContext(), mCursor), false);
                }
            } else {
                Timber.v("extras == null");
            }
        }

        /**
         * clear the intent so that rotation or triggering of the app from the 'recents' menu doesn't add repeated notifications from FCM
         */
        intent.replaceExtras(new Bundle());
        intent.setAction("");
        intent.setData(null);
        intent.setFlags(0);

        ContentResolver mResolver = getContentResolver();
        String selection = null;
        String[] selectionArgs = null;

        // create cursor to read OTXResponses stored in the DB
        Cursor mCursor = mResolver.query(OTXResponsesContract.OTXResponsesList.CONTENT_URI, Utility.OTXRESPONSES_COLUMNS, selection, selectionArgs, " id DESC");

        if (mCursor != null) {
            Timber.v("mCursor.getCount: " + mCursor.getCount());
            mCursor.moveToFirst();
        }

        mRecyclerView = findViewById(R.id.recyclerview); // good reference here: https://github.com/codepath/android_guides/wiki/Using-the-RecyclerView

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an OTXResponsesAdapter
        mAdapter = new OTXResponsesAdapter(this, mCursor);
        mRecyclerView.setAdapter(mAdapter);

        // Create a new dispatcher using the Google Play driver.
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(MainActivity.this));

        Bundle myExtrasBundle = new Bundle();
        myExtrasBundle.putString("some_key", "some_value");

        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(MyJobService.class)
                // uniquely identifies the job
                .setTag("my-unique-tag")
                // one-off job
                .setRecurring(false)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(0, 60))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        // Constraint.ON_UNMETERED_NETWORK,
                        // only run when the device is charging
                        // Constraint.DEVICE_CHARGING
                )
                .setExtras(myExtrasBundle)
                .build();

        dispatcher.mustSchedule(myJob);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
