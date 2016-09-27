package com.alienvault.threatscanner.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alienvault.threatscanner.R;
import com.alienvault.threatscanner.model.IpAddress;
import com.alienvault.threatscanner.network.FetchIpAddress;
import com.alienvault.threatscanner.network.QueryOTX;
import com.alienvault.threatscanner.utility.Utility;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public static TextView textView;

    public static void setIpAddress(IpAddress ipAddress) {
        // update the TextView with the device's IP Address
        textView.setText(ipAddress.getIpAddress());

        // make network call to query OTX
        QueryOTX queryOTX = new QueryOTX();
        queryOTX.execute(ipAddress.getIpAddress());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("kicking this off!");
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
