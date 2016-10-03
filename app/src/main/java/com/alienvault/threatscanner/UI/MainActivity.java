package com.alienvault.threatscanner.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alienvault.threatscanner.R;
import com.alienvault.threatscanner.application.ThreatScanner;
import com.alienvault.threatscanner.model.IpAddress;
import com.alienvault.threatscanner.model.OTXResults;
import com.alienvault.threatscanner.network.FetchIpAddress;
import com.alienvault.threatscanner.network.QueryOTX;
import com.alienvault.threatscanner.utility.Utility;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 999;
    private static TextView threatScore;
    private static TextView ipAddress;
    private static TextView otxResponse;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String webcastName;
    private String webcastURL;

    public static void setOtxResponse(OTXResults otxResults) {

        String response = "Scanning Host: " + otxResults.getScanningHost() + "\n" + "Malware Domain: " + otxResults.getMalwareDomain();
        // TODO make this clickable so that it points back to OTX
        Timber.v(response);
        Timber.v(otxResults.getThreatScore());

        threatScore.setText(otxResults.getThreatScore());
        ipAddress.setText("69.73.130.198");
        otxResponse.setText(response); // update the TextView with the OTX response

        // see https://github.com/hbaxamoosa/HelpWanted/blob/master/app/src/main/java/com/baxamoosa/helpwanted/sync/HelpWantedSyncAdapter.java for reference
        if (response != null) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(ThreatScanner.getAppContext())
                            .setSmallIcon(R.drawable.alienvault)
                            .setContentTitle("AlienVault OTX warnings")
                            .setContentText("AlienVault OTX found malicious activity on this IP address!");
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(ThreatScanner.getAppContext(), MainActivity.class);

            // The stack builder object will contain an artificial back stack for the started Activity.
            // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(ThreatScanner.getAppContext());
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) ThreatScanner.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
            // NOTIFICATION_ID allows you to update the notification later on.
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    public void setIpAddress(IpAddress ipAddress) {
        // update the TextView with the device's IP Address
        otxResponse.setText(ipAddress.getIpAddress());

        // make network call to query OTX
        QueryOTX queryOTX = new QueryOTX();
        queryOTX.execute(ipAddress.getIpAddress());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("kicking this off!");

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_main); // switch the TextView for a CardView so that it shows a historical audit trail

        // consider adding intro screens to the app - https://medium.com/tangoagency/material-intro-screen-for-android-apps-c4317fbac923#.1zp72ni98

        ipAddress = (TextView) findViewById(R.id.ip_address);
        otxResponse = (TextView) findViewById(R.id.otx_response);
        threatScore = (TextView) findViewById(R.id.threat_score);

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
        // if app was launched from notification, it will have an intent
        Intent intent = getIntent();

        if (intent != null) {
            Timber.v("intent != null");
            Bundle extras = intent.getExtras();
            if (intent.hasExtra("name")) {
                webcastName = extras.getString("name");
                webcastURL = extras.getString("url");
                otxResponse.setText("Come join AlienVault for " + webcastName + " " + webcastURL.toString() + ".");
            }
        }
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

    public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(webcastURL));
        startActivity(i);
    }
}
