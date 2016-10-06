package com.alienvault.threatscanner.data;

/**
 * Created by hbaxamoosa on 10/3/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import timber.log.Timber;

/**
 * Manages a local database for otxresponses
 */

public class OTXResponsesDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "otxresponses.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    // private final String TAG = OTXResponsesDbHelper.class.getSimpleName();

    public OTXResponsesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold favorite movies.
        final String SQL_CREATE_OTXRESPONSES_TABLE = "CREATE TABLE " + OTXResponsesContract.OTXResponsesList.TABLE_NAME + " (" +
                OTXResponsesContract.OTXResponsesList.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                OTXResponsesContract.OTXResponsesList.COLUMN_THREAT_SCORE + " TEXT NOT NULL, " +
                OTXResponsesContract.OTXResponsesList.COLUMN_IP_ADDRESS + " TEXT NOT NULL, " +
                OTXResponsesContract.OTXResponsesList.COLUMN_OTX_RESPONSE + " TEXT NOT NULL," +
                OTXResponsesContract.OTXResponsesList.COLUMN_URL + " TEXT NOT NULL," +
                OTXResponsesContract.OTXResponsesList.COLUMN_TYPE + " TEXT NOT NULL" +
                " );";

        Timber.v("SQL_CREATE_OTXRESPONSES_TABLE is " + SQL_CREATE_OTXRESPONSES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_OTXRESPONSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + OTXResponsesContract.OTXResponsesList.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}