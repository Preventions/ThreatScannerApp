package com.alienvault.threatscanner.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alienvault.threatscanner.R;
import com.alienvault.threatscanner.adapter.OTXResponsesAdapter;
import com.alienvault.threatscanner.application.ThreatScanner;
import com.alienvault.threatscanner.data.OTXResponsesContract;
import com.alienvault.threatscanner.network.FetchIpAddress;
import com.alienvault.threatscanner.utility.Utility;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public static RecyclerView mRecyclerView;
    public static OTXResponsesAdapter mAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String notificationType;
    private RecyclerView.LayoutManager mLayoutManager;
    private ContentResolver mContentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("kicking this off!");

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_main); // switch the TextView for a CardView so that it shows a historical audit trail

        // consider adding intro screens to the app - https://medium.com/tangoagency/material-intro-screen-for-android-apps-c4317fbac923#.1zp72ni98

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String token = FirebaseInstanceId.getInstance().getToken();
        Timber.v(token);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // TODO implement notification for user, so that the check doesn't have to be triggered via button click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.isNetworkAvailable(getApplicationContext())) {
                    Timber.v("network is available");
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Timber.v("calling network task");
                    FetchIpAddress fetchIpAddress = new FetchIpAddress();
                    fetchIpAddress.execute();
                } else {
                    Timber.v("not connected to wifi");
                }
            }
        });

        // TODO include an intent extra to identify that the notification click came from FCM or from the app itself
        // see https://github.com/firebase/quickstart-android/tree/master/messaging for reference
        // if app was launched from notification, it will have an intent with extra of type:firebase
        Intent intent = getIntent();

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

        ContentResolver mResolver = getContentResolver();
        String selection = null;
        String[] selectionArgs = null;

        // create cursor to read OTXResponses stored in the DB
        Cursor mCursor = mResolver.query(OTXResponsesContract.OTXResponsesList.CONTENT_URI, Utility.OTXRESPONSES_COLUMNS, selection, selectionArgs, null);

        if (mCursor != null) {
            Timber.v("mCursor.getCount: " + mCursor.getCount());
            mCursor.moveToFirst();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview); // good reference here: https://github.com/codepath/android_guides/wiki/Using-the-RecyclerView

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an OTXResponsesAdapter
        mAdapter = new OTXResponsesAdapter(this, mCursor);
        mRecyclerView.setAdapter(mAdapter);
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
