package com.alienvault.threatscanner.network;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.alienvault.threatscanner.BuildConfig;
import com.alienvault.threatscanner.R;
import com.alienvault.threatscanner.adapter.OTXResponsesAdapter;
import com.alienvault.threatscanner.application.ThreatScanner;
import com.alienvault.threatscanner.data.OTXResponsesContract;
import com.alienvault.threatscanner.model.OTXResults;
import com.alienvault.threatscanner.ui.MainActivity;
import com.alienvault.threatscanner.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import timber.log.Timber;

/**
 * Created by hbaxamoosa on 9/27/16.
 */

public class QueryOTX extends AsyncTask<String, Void, OTXResults> {

    /**
     * Implement https://medium.com/google-developer-experts/finally-understanding-how-references-work-in-android-and-java-26a0d9c92f83#.feoqbpgb2
     */

    private static final int NOTIFICATION_ID = 999;
    private ContentResolver mContentResolver;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(OTXResults otxResults) {
        super.onPostExecute(otxResults);

        mContentResolver = ThreatScanner.getAppContext().getContentResolver();

        ContentValues OTXResponse = new ContentValues();
        OTXResponse.put(OTXResponsesContract.OTXResponsesList.COLUMN_THREAT_SCORE, otxResults.getThreatScore());
        OTXResponse.put(OTXResponsesContract.OTXResponsesList.COLUMN_IP_ADDRESS, "69.73.130.198");
        OTXResponse.put(OTXResponsesContract.OTXResponsesList.COLUMN_OTX_RESPONSE, "Scanning Host: " + otxResults.getScanningHost() + "\n" + "Malware Domain: " + otxResults.getMalwareDomain());
        OTXResponse.put(OTXResponsesContract.OTXResponsesList.COLUMN_URL, "https://otx.alienvault.com/indicator/ip/" + "69.73.130.198" + "/");
        OTXResponse.put(OTXResponsesContract.OTXResponsesList.COLUMN_TYPE, "OTX");
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

        // trigger a notification
        // String response = "Scanning Host: " + otxResults.getScanningHost() + "\n" + "Malware Domain: " + otxResults.getMalwareDomain();

        // see https://github.com/hbaxamoosa/HelpWanted/blob/master/app/src/main/java/com/baxamoosa/helpwanted/sync/HelpWantedSyncAdapter.java for reference
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ThreatScanner.getAppContext())
                        .setSmallIcon(R.drawable.alienvault)
                        .setContentTitle("AlienVault OTX warnings")
                        .setContentText("AlienVault OTX found malicious activity on this IP address!")
                        .setAutoCancel(true); // this along with setContentIntent makes the notification dismissable
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

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected OTXResults doInBackground(String... params) {
        // Timber.v("executing QueryOTX network task");

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String repMonJsonStr = null;

        try {
            // Construct the URL for the OTX API
            final String BASE_URL = "https://otx.alienvault.com:443/api/v1/indicators/IPv4/";
            final String IP = params[0];
            // Timber.v("ip address being queried is: " + params[0]);
            final String SECTION = "reputation";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(IP)
                    .appendPath(SECTION)
                    .build();

            Timber.v(builtUri.toString());

            // URL url = new URL(builtUri.toString()); hard-coding to a static URL for testing

            // see https://medium.com/code-better/hiding-api-keys-from-your-android-repository-b23f5598b906
            // see http://www.rainbowbreeze.it/environmental-variables-api-key-and-secret-buildconfig-and-android-studio/
            URL url = new URL("https://otx.alienvault.com:443/api/v1/indicators/IPv4/69.73.130.198/reputation");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("X-OTX-API-KEY", BuildConfig.OTX_API_KEY);
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                Timber.v("inputStream == null");
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            repMonJsonStr = buffer.toString();
            Timber.v(repMonJsonStr); // output JSON string

        } catch (IOException e) {
            Timber.e(" Error " + e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Timber.e("Error closing stream " + e);
                }
            }
        }
        try {
            return getReputationFromJson(repMonJsonStr);
        } catch (JSONException e) {
            Timber.e(e.getMessage() + e);
            e.printStackTrace();
        }
        return null;
    }

    private OTXResults getReputationFromJson(String repMonJsonStr) throws JSONException {

        // see http://www.androidhive.info/2012/01/android-json-parsing-tutorial/ for reference
        // see https://developer.android.com/reference/org/json/JSONObject.html for reference

        // create new OTXResults model object to store values retrieved from the JSON response
        OTXResults otxResults = new OTXResults();

        // Create JSONObject from results string
        JSONObject OTXResponse = new JSONObject(repMonJsonStr);

        // Create JSONObject from response node
        JSONObject response = OTXResponse.getJSONObject("reputation");
        otxResults.setThreatScore(response.getString("threat_score"));
        // Timber.v("threat_score: " + otxResults.getThreatScore());

        // Create JSONObject from counts node
        JSONObject counts = response.getJSONObject("counts");

        // Timber.v(counts.toString());

        otxResults.setScanningHost(counts.getString("Scanning Host"));
        otxResults.setMalwareDomain(counts.getString("Malware Domain"));

        // Timber.v("Scanning Home: " + otxResults.getScanningHost());
        // Timber.v("Malware Domain: " + otxResults.getMalwareDomain());
        return otxResults;
    }
}
