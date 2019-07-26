package com.alienvault.threatscanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alienvault.threatscanner.R;
import com.alienvault.threatscanner.utility.Utility;

import timber.log.Timber;

/**
 * Created by hbaxamoosa on 10/3/16.
 */

// see http://stackoverflow.com/questions/26312301/is-it-possible-to-make-cursoradapter-be-set-in-recycleview-just-like-listview for reference

public class OTXResponsesAdapter extends RecyclerView.Adapter<OTXResponsesAdapter.OTXResponsesAdapterViewHolder> {

    private static Context mContext;
    private static Cursor mCursor;

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
        Timber.v("mCursor position: " + position);
        mCursor.moveToPosition(position);
        holder.mThreatScore.setText(mCursor.getString(Utility.COLUMN_THREAT_SCORE));
        holder.mIpAddress.setText(mCursor.getString(Utility.COLUMN_IP_ADDRESS));
        holder.mOtxResponse.setText(mCursor.getString(Utility.COLUMN_OTX_RESPONSE));
    }

    @Override
    public int getItemCount() {
        /*if (mCursor != null) {
            Timber.v("getItemCount(): " + mCursor.getCount());
        }*/
        return (null != mCursor ? mCursor.getCount() : 0);
    }

    public static class OTXResponsesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mThreatScore;
        public TextView mIpAddress;
        public TextView mOtxResponse;

        public OTXResponsesAdapterViewHolder(View itemView) {
            super(itemView);
            mThreatScore = itemView.findViewById(R.id.threat_score);
            mIpAddress = itemView.findViewById(R.id.ip_address);
            mOtxResponse = itemView.findViewById(R.id.otx_response);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Timber.v("onClick(View v) inside OTXResponsesAdapterViewHolder");
            int adapterPosition = getAdapterPosition();
            // int layoutPosition = getLayoutPosition();

            // String url = "https://otx.alienvault.com/indicator/ip/69.73.130.198/";

            Timber.v("adapterPosition: " + adapterPosition);
            mCursor.moveToFirst();
            mCursor.move(adapterPosition);
            String url = mCursor.getString(Utility.COLUMN_URL);
            Timber.v("url: " + url);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            v.getContext().startActivity(intent);
        }
    }
}