package com.alienvault.threatscanner.network;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.alienvault.threatscanner.application.ThreatScanner;
import com.alienvault.threatscanner.model.IpAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import timber.log.Timber;

/**
 * Created by hbaxamoosa on 9/23/16.
 */

public class FetchIpAddress extends AsyncTask<Void, Void, IpAddress> {

    /**
     * Implement https://medium.com/google-developer-experts/finally-understanding-how-references-work-in-android-and-java-26a0d9c92f83#.feoqbpgb2
     */

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected IpAddress doInBackground(Void... params) {

        // Timber.v("executing FetchIpAddress network task");

        // These two need to be declared outside the try/catch so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String ipAddressJsonStr = null;

        try {
            final String BASE_URL = "https://api.ipify.org/?format=json";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon().build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            urlConnection.getResponseMessage();
            Timber.v("urlConnection.getResponseMessage(): " + urlConnection.getResponseMessage());

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            ipAddressJsonStr = buffer.toString();
            inputStream.close();
            Timber.v("ipAddressJsonStr: " + ipAddressJsonStr);

        } catch (IOException e) {
            Timber.e("Error " + e);
            Timber.e("Error " + urlConnection.getErrorStream());
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
            if (ipAddressJsonStr != null) {
                return getIpAddressFromJson(ipAddressJsonStr);
            } else {
                Toast.makeText(ThreatScanner.getAppContext(), "Could not retrieve IP address for device", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Timber.e(e.getMessage() + e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(IpAddress ipAddress) {
        super.onPostExecute(ipAddress);
        // call setIpAddress method from MainActivity to update the TextView with the device's IP Address
        // make network call to query OTX
        QueryOTX queryOTX = new QueryOTX();
        queryOTX.execute(ipAddress.getIpAddress());
    }

    private IpAddress getIpAddressFromJson(String ipAddressJsonStr) throws JSONException {

        /**
         * No + String: Having to concatenate a few Strings, + operator might do.
         * Never use it for a lot of String concatenations; the performance is really bad.
         * Prefer a StringBuilder instead.
         */

        // Create JSONObject from results string
        JSONObject ipAddressJsonObj = new JSONObject(ipAddressJsonStr);
        Timber.v("your ip address is: " + ipAddressJsonObj.getString("ip"));

        IpAddress ipAddress = new IpAddress();

        ipAddress.setIpAddress(ipAddressJsonObj.getString("ip"));
        // ipAddress.setIpAddress(ipAddressJsonStr);

        return ipAddress;
    }
}
