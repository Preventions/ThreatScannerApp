package com.alienvault.threatscanner.data;

/**
 * Created by hbaxamoosa on 10/3/16.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the otxresponses database.
 */
public class OTXResponsesContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.alienvault.threatscanner";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_OTXRESPONSES = "otxresponses";

    /*
        Inner class that defines the contents of the location table
     */
    public static final class OTXResponsesList implements BaseColumns {

        public static final String TABLE_NAME = "responses";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_OTXRESPONSES).build();

        // Columns for responses table
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_IP_ADDRESS = "ip_address";
        public static final String COLUMN_OTX_RESPONSE = "otx_response";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_OTXRESPONSES;

        public static Uri buildOTXResponsesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}