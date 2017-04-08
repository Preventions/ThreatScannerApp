package com.alienvault.threatscanner.service;

import android.content.Intent;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import timber.log.Timber;

/**
 * Created by hbaxamoosa on 4/8/17.
 */

public class MyJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        // Do some work here
        Timber.v("onStartJob(JobParameters job)");
        Timber.v(job.toString());

        // use this to start and trigger a service
        Intent i = new Intent(this, OtxQueryService.class);
        // potentially add data to the intent
        i.putExtra("KEY1", "Value to be used by the service");
        this.startService(i);
        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }
}
