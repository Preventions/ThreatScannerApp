package com.alienvault.threatscanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alienvault.threatscanner.R;
import com.alienvault.threatscanner.utility.Utility;

import timber.log.Timber;

/**
 * Created by hbaxamoosa on 10/3/16.
 */

// see http://stackoverflow.com/questions/26312301/is-it-possible-to-make-cursoradapter-be-set-in-recycleview-just-like-listview for reference

public class OTXResponsesAdapter extends RecyclerView.Adapter<OTXResponsesAdapter.OTXResponsesAdapterViewHolder> {

    private static Context mContext;
    private Cursor mCursor;

    public OTXResponsesAdapter(Context context, Cursor cursor) {
        super();
        mContext = context;
        mCursor = cursor;
    }

    @Override
    public OTXResponsesAdapter.OTXResponsesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_main, parent, false);
        return new OTXResponsesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OTXResponsesAdapter.OTXResponsesAdapterViewHolder holder, int position) {
        Timber.v("onBindViewHolder(OTXResponsesAdapter.OTXResponsesAdapterViewHolder holder, int position)");

        for (int i = 0; i < mCursor.getCount(); i++) {
            Timber.v("i = " + i);

            holder.mThreatScore.setText(mCursor.getString(Utility.COL_ID));
            holder.mIpAddress.setText(mCursor.getString(Utility.COLUMN_IP_ADDRESS));
            holder.mOtxResponse.setText(mCursor.getString(Utility.COLUMN_OTX_RESPONSE));
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            Timber.v("getItemCount(): " + mCursor.getCount());
        }
        return (null != mCursor ? mCursor.getCount() : 0);
    }

    public static class OTXResponsesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mThreatScore;
        public TextView mIpAddress;
        public TextView mOtxResponse;

        public OTXResponsesAdapterViewHolder(View itemView) {
            super(itemView);
            mThreatScore = (TextView) itemView.findViewById(R.id.threat_score);
            mIpAddress = (TextView) itemView.findViewById(R.id.ip_address);
            mOtxResponse = (TextView) itemView.findViewById(R.id.otx_response);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Timber.v("onClick(View v) inside OTXResponsesAdapterViewHolder");
            int adapterPosition = getAdapterPosition();
            int layoutPosition = getLayoutPosition();
            Toast.makeText(mContext, "click on CardView", Toast.LENGTH_SHORT).show();

            String url = "https://otx.alienvault.com/indicator/ip/69.73.130.198/";

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            v.getContext().startActivity(intent);
        }
    }
}